import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * The window presenter
 * This is where we communicate between the view and the model
 */
public class WindowPresenter {

    ArrayList<String> input = new ArrayList<>();
    int spec;
    String RegName = "protein=";

    fastaParser data = new fastaParser();
    HashMap<String, fastaParser.HeaderSequence> dataMap = new HashMap<>();

    HashMap<String, String> deepTM = new HashMap<>();

    HashMap<String, String> signalP = new HashMap<>();

    String method;

    String diamondDB;

    ArrayList<TreeItem<String>> toShow = new ArrayList<>();
    ArrayList<TreeItem<String>> toRemove = new ArrayList<>();

    public WindowPresenter(Stage stage, WindowController controller) {

        new addOptions(controller);
        new toolTips(controller);
        MSA msa = new MSA(controller);

        controller.getCloseMenuItem().setOnAction(e-> {
            clearResults();
            Platform.exit();
        });

        controller.getFullScreenMenuItem().setOnAction(e-> stage.setFullScreen(!stage.fullScreenProperty().get()));

        controller.getAboutMenuItem().setOnAction(e-> showAbout());

        controller.getSaveMenuItem().setOnAction(e-> save(stage, controller));
        controller.getSaveButton().setOnAction(e-> save(stage, controller));

        controller.getSaveClustersMenuItem().setOnAction(e-> saveCluster(stage, controller));
        controller.getSaveClustersButton().setOnAction(e-> saveCluster(stage, controller));

        controller.getRemoveButton().setOnAction(e-> {
            remove(controller);
            numberOfClusters(controller);
        });

        controller.getShowButton().setOnAction(e-> {
            show(controller);
            numberOfClusters(controller);
        });

        controller.getClearFiltersButton().setOnAction(e-> clearFilter(controller));

        controller.getLinclustButton().setOnAction(e->{
            this.method = "linclust";
            controller.getToolLabel().setText("MMseqs");
            controller.getModeLabel().setText("LinClust Options");
            controller.getMmseqsAdvOptionsPane().setVisible(false);
            controller.getShowAdvancedOptionsCheckBox().setSelected(false);
            controller.getAdvancedDiamondPane().setVisible(false);
            controller.getOptionsPane().setVisible(true);
            controller.getCoverageSlider().setDisable(false);
            if(!input.isEmpty())
                controller.getAutomaticButton().setDisable(false);
        });

        controller.getClusterButton().setOnAction(e->{
            this.method = "cluster";
            controller.getToolLabel().setText("MMseqs");
            controller.getModeLabel().setText("Cluster Options");
            controller.getMmseqsAdvOptionsPane().setVisible(false);
            controller.getShowAdvancedOptionsCheckBox().setSelected(false);
            controller.getAdvancedDiamondPane().setVisible(false);
            controller.getOptionsPane().setVisible(true);
            controller.getCoverageSlider().setDisable(false);
            controller.getAutomaticButton().setDisable(true);

        });

        controller.getDiamondButton().setOnAction(e->{
            this.method = "diamond";
            controller.getToolLabel().setText("DIAMOND");
            controller.getModeLabel().setText("BlastP Options");
            controller.getMmseqsAdvOptionsPane().setVisible(false);
            controller.getShowAdvancedOptionsCheckBox().setSelected(false);
            controller.getAdvancedDiamondPane().setVisible(false);
            controller.getOptionsPane().setVisible(true);
            controller.getAutomaticButton().setDisable(true);
        });

        controller.getShowAdvancedOptionsCheckBox().setOnAction(e->{
            if(this.method.equals("linclust") || this.method.equals("cluster")) {
                if(controller.getShowAdvancedOptionsCheckBox().isSelected()) {
                    controller.getMmseqsAdvOptionsPane().setVisible(true);
                }
                else if(!controller.getShowAdvancedOptionsCheckBox().isSelected()) {
                    controller.getMmseqsAdvOptionsPane().setVisible(false);
                }
            } else if (this.method.equals("diamond")) {
                if(controller.getShowAdvancedOptionsCheckBox().isSelected()) {
                    controller.getAdvancedDiamondPane().setVisible(true);
                }
                else if(!controller.getShowAdvancedOptionsCheckBox().isSelected()) {
                    controller.getAdvancedDiamondPane().setVisible(false);
                }
            }
        });

        controller.getMmseqsUsageMenuItem().setOnAction(e-> {
            try {mmseqsAbout(controller);} catch (IOException ex) {throw new RuntimeException(ex);} });

        controller.getOpenMenuItem().setOnAction(e-> {
            try {
                openFile(stage, controller);
                if(method != null && method.equals("linclust"))
                    controller.getAutomaticButton().setDisable(false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        controller.getOpenButton().setOnAction(e-> controller.getOpenMenuItem().fire());

        controller.getStartButton().setOnAction(e-> {
            clearResults();

            if(method != null) {
                try {
                    new startClustering(controller, this, spec, method, input);
                    controller.getMinMatchesTxtField().setValue(spec);
                    if(new File("Results/cluster_cluster.tsv").exists()) {
                        summary(controller, spec);
                        numberOfClusters(controller);
                    }
                    controller.getResultsPane().setVisible(true);
                    controller.getMsaButton().setDisable(false);
                    controller.getSignalPButton().setDisable(false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else{
                controller.getInfoLabel().setText("Please choose a method!");
            }
            if(!signalP.isEmpty()) {
                signalPUpdateTree(controller);
            }
        });

        controller.getDarkModeMenuItem().selectedProperty().addListener(e -> {
            if(controller.getDarkModeMenuItem().isSelected())
                stage.getScene().getStylesheets().add("modena_dark.css");
            else
                stage.getScene().getStylesheets().remove("modena_dark.css");
        });

        controller.getFilterButton().setOnAction(e-> {
            File results = new File("Results/cluster_cluster.tsv");
            if(results.exists()) {
                int filter = (int)controller.getMinMatchesTxtField().getValue();
                try {
                    summary(controller, filter);
                    numberOfClusters(controller);
                    if(!signalP.isEmpty()) {
                        signalPUpdateTree(controller);
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        controller.getSequenceCheckBox().setOnAction(e-> controller.getFilterButton().fire());
        controller.getHeaderCheckBox().setOnAction(e-> controller.getFilterButton().fire());

        controller.getAutomaticButton().setOnAction(e-> {
            try {
                new autoParameters(this, controller).start(new Stage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getResultsTextArea().getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem<String>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observable,
                                        TreeItem<String> oldValue, TreeItem<String> newValue) {
                        if(newValue != null && !(newValue == controller.getResultsTextArea().getRoot()) &&
                                newValue.getChildren().size() > 1){
                            controller.getSignalPMenuItem().setDisable(false);
                            controller.getDeepTMHMMMenuItem().setDisable(false);
                            try {
                                msa.update();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            controller.getSignalPMenuItem().setDisable(true);
                            controller.getDeepTMHMMMenuItem().setDisable(true);
                        }
                    }
                }
        );

        controller.getMsaButton().setOnAction(e-> {
            try {
                msa.start(new Stage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getSignalPButton().setOnAction(e-> {
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter("Results/rep.fasta"));
                var treeRoot = controller.getResultsTextArea().getRoot();
                var children = treeRoot.getChildren();
                for(var ch:children){
                    if(controller.getHeaderCheckBox().isSelected()) {
                        br.write(">" + ch.getValue() + "\n");
                        br.write(ch.getChildren().get(0).getValue() + "\n");
                    }
                    else {
                        br.write(">" + ch.getValue().split("\t")[1] + "\n");
                        br.write(ch.getChildren().get(0).getValue() + "\n");
                    }
                }
                br.close();
            }
            catch (IOException exc) {
                controller.getInfoLabel().setText("ERROR: " + exc.getMessage());
            }
            var signalP = new signalpPrediction();
            controller.getProgressBar().visibleProperty().bind(signalP.runningProperty());
            controller.getProgressBar().progressProperty().bind(signalP.progressProperty());
            controller.getSignalPButton().disableProperty().bind(signalP.runningProperty());

            signalP.setOnSucceeded(s-> {
                try {
                    signalPResults(controller);
                    signalPUpdateTree(controller);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            signalP.start();
        });

        controller.getSignalPMenuItem().setOnAction(e->{
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter("Results/rep.fasta"));
                var ch = controller.getResultsTextArea().getSelectionModel().getSelectedItem();
                if(controller.getHeaderCheckBox().isSelected()) {
                    br.write(">" + ch.getValue() + "\n");
                    br.write(ch.getChildren().get(0).getValue() + "\n");
                }
                else {
                    br.write(">" + ch.getValue().split("\t")[1] + "\n");
                    br.write(ch.getChildren().get(0).getValue() + "\n");
                }

                br.close();
            }
            catch (IOException exc) {
                controller.getInfoLabel().setText("ERROR: " + exc.getMessage());
            }
            var signalP = new signalpPrediction();
            controller.getProgressBar().visibleProperty().bind(signalP.runningProperty());
            controller.getProgressBar().progressProperty().bind(signalP.progressProperty());
            controller.getSignalPButton().disableProperty().bind(signalP.runningProperty());

            signalP.setOnSucceeded(s-> {
                System.out.println("fertig");
                try {
                    signalPResults(controller);
                    signalPUpdateTree(controller);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            signalP.start();
        });
    }

    /**
     * Shows the About window
     */
    private static void showAbout() {
        var aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setTitle("About");
        aboutAlert.setHeaderText(null);
        aboutAlert.setContentText("IProFin by: Majd Hammour \n" +
                "Identical Proteins Finder\nUser interface depends on the tool MMSeqs2");
        aboutAlert.showAndWait();
    }

    /**
     * prints in the stdOut tab the usage of mmseqs 2
     */
    private static void mmseqsAbout(WindowController controller) throws IOException {
        controller.getStdOutTextArea().clear();

        String[] path = {"./src/model/mmseqs/bin/mmseqs", "-h"};

        processExecutor processExecutor = new processExecutor(path);
        controller.getStdOutTextArea().setText(processExecutor.run());
    }

    /**
     * specify the path to the directory of the fasta files
     */
    private void openFile(Stage stage, WindowController controller) throws IOException {
        data.clear();
        dataMap.clear();
        int count = 0;
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open directory");

        var selectedDirectory = directoryChooser.showDialog(stage);
        if (selectedDirectory != null)
        {
            controller.getInfoLabel().setText("empty");
            File[] files = selectedDirectory.listFiles();

            if(files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".fasta")) {
                        this.input.add(file.getAbsolutePath());
                        count += 1;
                        this.data.read(file.getAbsolutePath());
                    }
                }
            }
            controller.getInfoLabel().setText(String.valueOf(count + " FastA files are loaded"));
            this.spec = count;
        }
        controller.getMinMatchesTxtField().setMax(spec);

        for(var HeaderSequence : this.data){
            String key = HeaderSequence.header().substring(4).split(" ")[0];
            this.dataMap.put(key, HeaderSequence);
        }
    }

    public void summary(WindowController controller, int filter) throws IOException {
        controller.getRemoveButton().setDisable(false);
        controller.getShowButton().setDisable(false);
        controller.getClearFiltersButton().setDisable(false);
        BufferedReader reader = new BufferedReader(new FileReader("Results/cluster_cluster.tsv"));

        Map<String, ArrayList<String>> map = new HashMap<>();

        String line;

        while((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            if(!map.containsKey(parts[0])) {
                map.put(parts[0], new ArrayList<String>());
                map.get(parts[0]).add(parts[1]);
            }
            else{
                map.get(parts[0]).add(parts[1]);
            }
        }
        TreeItem<String> root = new TreeItem<>("Summary");

        for(var key:map.keySet()) {
            if(map.get(key).size() >= filter) {
                TreeItem<String> cluster = new TreeItem<>(map.get(key).size()+"\t"+dataMap.get(key).header());
                Tooltip.install(cluster.getGraphic(), new Tooltip("map.get(key).size()"));
                cluster.getChildren().add(new TreeItem<>(dataMap.get(key).sequence()));

                for(String clust: map.get(key)){
                    if(controller.getSequenceCheckBox().isSelected()) {
                        cluster.getChildren().add(new TreeItem<>(dataMap.get(clust).sequence()));
                    }
                    else {
                        TreeItem<String> child = new TreeItem<>(dataMap.get(clust).header());
                        child.getChildren().add(new TreeItem<>(dataMap.get(clust).sequence()));
                        cluster.getChildren().add(child);
                    }
                }
                root.getChildren().add(cluster);
            }
        }
        root.setExpanded(true);
        controller.getResultsTextArea().setRoot(root);

        if(controller.getHeaderCheckBox().isSelected()) {
            if (!this.dataMap.isEmpty()) {
                var treeRoot = controller.getResultsTextArea().getRoot();
                var children = treeRoot.getChildren();
                for (var ch : children) {
                    if (ch.getValue().split(RegName).length == 2) {
                        ch.setValue(ch.getValue().split(RegName)[1].split("]")[0]);
                    }
                    for (var ch2 : ch.getChildren()) {
                        if (ch2.getValue().split(RegName).length == 2) {
                            ch2.setValue(ch2.getValue().split(RegName)[1].split("]")[0]);
                        }
                    }
                }
            }
        }
        if(!toShow.isEmpty()) {
            show(controller);
        }
        if(!toRemove.isEmpty()) {
            remove(controller);
        }
    }

    private void save(Stage stage, WindowController controller) {
        var FileChooser = new FileChooser();
        FileChooser.setTitle("Save results as FastA file");
        FileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*"));
        var selectedFile = FileChooser.showSaveDialog(stage);
        if (selectedFile != null)
        {
            try {
                BufferedWriter br = new BufferedWriter(new FileWriter(selectedFile));
                var treeRoot = controller.getResultsTextArea().getRoot();
                var children = treeRoot.getChildren();
                for(var ch:children){
                    if(controller.getHeaderCheckBox().isSelected()) {
                        br.write(">" + ch.getValue() + "\n");
                        br.write(ch.getChildren().get(0).getValue() + "\n");
                    }
                    else {
                        br.write(">" + ch.getValue().split("\t")[1] + "\n");
                        br.write(ch.getChildren().get(0).getValue() + "\n");
                    }
                }
                br.close();
            }
            catch (IOException e) {
                controller.getInfoLabel().setText("ERROR: " + e.getMessage());
            }
        }
    }

    private void saveCluster(Stage stage, WindowController controller) {
        var DirectoryChooser = new DirectoryChooser();
        DirectoryChooser.setTitle("Save clusters as FastA files");

        var selectedDir = DirectoryChooser.showDialog(stage);
        if (selectedDir != null)
        {
            try {
                var treeRoot = controller.getResultsTextArea().getRoot();
                var children = treeRoot.getChildren();
                int name = 0;
                if(children.size() > 100){
                    var alert = new Alert(Alert.AlertType.CONFIRMATION,"Save many files?",
                            ButtonType.YES, ButtonType.NO);
                    alert.setTitle("WARNING");
                    alert.setHeaderText(null);
                    alert.setContentText("WARNING TOO MANY FILES \n Do you want to save "
                            + String.valueOf(children.size()) + " Files?");
                    alert.showAndWait();
                    if(alert.getResult() == ButtonType.NO)
                        return;
                }
                for(var ch:children){
                    name += 1;
                    if(!controller.getSequenceCheckBox().isSelected()) {
                        BufferedWriter br = new BufferedWriter(new FileWriter(selectedDir + "/Cluster" + name +".fasta"));
                        for (var ch2 : ch.getChildren()) {
                            if (!ch2.isLeaf()) {
                                br.write(">" + ch2.getValue() + "\n");
                                br.write(ch2.getChildren().get(0).getValue() + "\n");
                            }
                        }
                        br.close();
                    }
                    else {
                        BufferedWriter br = new BufferedWriter(new FileWriter(
                                selectedDir + "/Cluster" + name +".fasta"));
                        int counter = 0;
                        for (var ch2 : ch.getChildren()) {
                            counter += 1;
                            if (!ch2.getChildren().isEmpty()) {
                                br.write(">" + ch2.getValue() + counter + "\n");
                                br.write(ch2.getChildren().get(0).getValue() + "\n");
                            }
                            else {
                                br.write(">" + ch.getValue() + counter + "\n");
                                br.write(ch2.getValue() + "\n");
                            }
                        }
                        br.close();
                    }
                }
            }
            catch (IOException e) {
                controller.getInfoLabel().setText("ERROR: " + e.getMessage());
            }
        }
    }

    private void remove(WindowController controller) {

        var treeRoot = controller.getResultsTextArea().getRoot();
        var children = treeRoot.getChildren();

        if(!children.isEmpty()) {
            var text = controller.getRemoveTextField().getText().toLowerCase();
            if(text.length() > 0) {
                for(var ch:children) {
                    var header = ch.getValue().toLowerCase();
                    if(header.contains(text)) {
                        toRemove.add(ch);
                    }
                }
                for(var ch:toRemove){
                    children.remove(ch);
                }
                controller.getInfoLabel().setText(toRemove.size() + " Protein were filtered");
            }
        }
    }

    private void show(WindowController controller) {
        var treeRoot = controller.getResultsTextArea().getRoot();
        var children = treeRoot.getChildren();

        if(!children.isEmpty()) {
            var text = controller.getRemoveTextField().getText().toLowerCase();
            if(text.length() > 0) {
                for(var ch:children) {
                    var header = ch.getValue().toLowerCase();
                    if(!header.contains(text)) {
                        toShow.add(ch);
                    }
                }
                for(var ch:toShow){
                    children.remove(ch);
                }
                controller.getInfoLabel().setText(toShow.size() + " Protein were filtered");
            }
        }
    }
    private void clearResults(){
        File results = new File("Results/");
        if(results.exists()) {
            try {
                Files.walk(results.toPath()).map(Path::toFile).forEach(File::delete);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        File signalP = new File("Results/SignalP");
        if(signalP.exists()) {
            signalP.delete();
        }
        results.delete();
    }

    private void clearFilter(WindowController controller) {
        toShow.clear();
        toRemove.clear();
        controller.getFilterButton().fire();
    }

    private void numberOfClusters(WindowController controller) {
        var root = controller.getResultsTextArea().getRoot();
        var children = root.getChildren();

        controller.getClusterCountLabel().setText(String.valueOf(children.size()));
    }

    private void signalPResults(WindowController controller) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Results/SignalP/prediction_results.txt"));

        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#")) {
                String[] parts = line.split("\t");
                if (parts[0].split(RegName).length > 1) {
                    signalP.put(parts[0].split(RegName)[1].split("]")[0], parts[1]);
                } else {
                    signalP.put(parts[0], parts[1]);
                }
            }
        }
    }

    private void signalPUpdateTree(WindowController controller) {
        var treeRoot = controller.getResultsTextArea().getRoot();
        var children = treeRoot.getChildren();

        for(var ch:children){
            if(controller.getHeaderCheckBox().isSelected()) {
                ch.setValue(ch.getValue() + " | SignalP6= " + signalP.get(ch.getValue()));
            }
            else {
                ch.setValue(ch.getValue() + " | SignalP6= " +
                        signalP.get(ch.getValue().split(RegName)[1].split("]")[0]));
            }
        }
    }

    public ArrayList<String> getInput() {
        return input;
    }

    public int getSpec() {
        return spec;
    }

    public String getRegName() {
        return RegName;
    }

    public fastaParser getData() {
        return data;
    }

    public HashMap<String, fastaParser.HeaderSequence> getDataMap() {
        return dataMap;
    }

    public String getMethod() {
        return method;
    }

    public ArrayList<TreeItem<String>> getToShow() {
        return toShow;
    }

    public ArrayList<TreeItem<String>> getToRemove() {
        return toRemove;
    }

    public String getDiamondDB() {
        return diamondDB;
    }

    public void setDiamondDB(String diamondDB) {
        this.diamondDB = diamondDB;
    }
}





// can be removed better version was programmed
    /*
    private void adjacencySummery(WindowController controller, int filter) throws IOException {
        controller.getRemoveButton().setDisable(false);
        controller.getShowButton().setDisable(false);
        BufferedReader reader = new BufferedReader(new FileReader("Results/cluster_cluster.tsv"));

        ArrayList<String[]> data = new ArrayList<>();
        String line;

        while((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");
            data.add(parts);
        }
        String tmp = null;
        int count = 0;

        TreeItem<String> root = new TreeItem<>("Summary");
        ArrayList<String> items = new ArrayList<>();

        for(String[] entry: data) {
            if(!entry[0].equals(tmp)){
                if(tmp == null){
                    tmp = entry[0];
                    count += 1;
                    items.add(entry[1]);
                }
                else {
                    if(count >= filter) {
                        TreeItem<String> cluster;
                        if(this.dataMap.containsKey(tmp)){
                            cluster = new TreeItem<>(count + "\t" + this.dataMap.get(tmp).header());
                            cluster.getChildren().add(new TreeItem<>(this.dataMap.get(tmp).sequence()));
                        }
                        else
                            cluster = new TreeItem<>(count + "\t" + tmp);

                        for(String s: items){
                            TreeItem<String> child;
                            TreeItem<String> seq;
                            if(this.dataMap.containsKey(s)) {
                                if(controller.getSequenceCheckBox().isSelected()) {
                                    seq = new TreeItem<>(this.dataMap.get(s).sequence());
                                    cluster.getChildren().add(seq);
                                }
                                else {
                                    child = new TreeItem<>(this.dataMap.get(s).header());
                                    seq = new TreeItem<>(this.dataMap.get(s).sequence());
                                    child.getChildren().add(seq);
                                    cluster.getChildren().add(child);
                                }
                            }
                            else {
                                child = new TreeItem<>(s);
                                cluster.getChildren().add(child);
                            }
                        }
                        root.getChildren().add(cluster);
                    }
                    items.clear();
                    count = 1;
                    tmp = entry[0];
                }
            }
            else {
                count += 1;
                items.add(entry[1]);
            }
        }
        root.setExpanded(true);
        controller.getResultsTextArea().setRoot(root);

        if(controller.getHeaderCheckBox().isSelected()) {
            if(!this.dataMap.isEmpty()) {
                var treeRoot = controller.getResultsTextArea().getRoot();
                var children = treeRoot.getChildren();
                for (var ch : children) {
                    if(ch.getValue().split(RegName).length == 2) {
                        ch.setValue(ch.getValue().split(RegName)[1].split("]")[0]);
                    }
                    for (var ch2 : ch.getChildren()) {
                        if(ch2.getValue().split(RegName).length == 2) {
                            ch2.setValue(ch2.getValue().split(RegName)[1].split("]")[0]);
                        }
                    }
                }
            }
        }
    }
    */
