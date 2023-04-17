import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import model.*;
import proteinExplorer.*;

import javafx.application.Platform;
import javafx.stage.Stage;
import optionsWindow.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The window presenter
 * This is where we communicate between the view and the model
 */
public class WindowPresenter {

    ArrayList<String> input = new ArrayList<>();
    int spec;
    String RegName = "protein=";
    HashMap<String, fastaParser.HeaderSequence> dataMap = new HashMap<>();
    HashMap<String, String> signalP = new HashMap<>();
    HashMap<TreeItem<String>, String> clustersRes = new HashMap<TreeItem<String>, String>();
    String method;
    String diamondDB;
    ArrayList<TreeItem<String>> toShow = new ArrayList<>();
    ArrayList<TreeItem<String>> toRemove = new ArrayList<>();

    String sequence;

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
            controller.getAutomaticButton().setDisable(false);

        });

        controller.getDiamondButton().setOnAction(e->{
            this.method = "diamond";
            controller.getToolLabel().setText("DIAMOND");
            controller.getModeLabel().setText("BlastP Options");
            controller.getMmseqsAdvOptionsPane().setVisible(false);
            controller.getShowAdvancedOptionsCheckBox().setSelected(false);
            controller.getAdvancedDiamondPane().setVisible(false);
            controller.getOptionsPane().setVisible(true);
            controller.getAutomaticButton().setDisable(false);
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
                if(method != null)
                    controller.getAutomaticButton().setDisable(false);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        controller.getOpenButton().setOnAction(e-> controller.getOpenMenuItem().fire());

        controller.getStartButton().setOnAction(e-> startButton(controller));

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
        controller.getHeaderCheckBox().setOnAction(e->
            onlyProName(controller, controller.getResultsTextArea().getRoot(), clustersRes));


        controller.getAutomaticButton().setOnAction(e-> {
            try {
                var optionsView = new optionsWindowView();

                var optionsStage =  new Stage();
                // set the stage and show
                optionsStage.setScene(new Scene(optionsView.getRoot()));
                var optionsPresenter = new OptionsWindowPresenter(optionsStage, optionsView.getController(),
                        this, controller);
                optionsStage.setTitle("Settings");
                optionsStage.show();
            } catch (IOException ex) {
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
                            controller.getMsaButton().setDisable(false);
                            controller.getMsaViewerMenuItem().setDisable(false);
                            controller.getJalViewMenuItem().setDisable(false);
                            controller.getProteinViewerButton().setDisable(false);

                            sequence = newValue.getChildren().get(0).getValue();

                            try {
                                msa.update();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        else{
                            controller.getSignalPMenuItem().setDisable(true);
                            controller.getDeepTMHMMMenuItem().setDisable(true);
                            controller.getMsaButton().setDisable(true);
                            controller.getMsaViewerMenuItem().setDisable(true);
                            controller.getJalViewMenuItem().setDisable(true);
                        }
                    }
                }
        );

        controller.getConservedTreeView().getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<TreeItem<String>>() {
                    @Override
                    public void changed(ObservableValue<? extends TreeItem<String>> observable,
                                        TreeItem<String> oldValue, TreeItem<String> newValue) {
                        controller.getResultsTextArea().getSelectionModel().select(
                                controller.getConservedTreeView().getSelectionModel().getSelectedIndex()
                        );
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

        controller.getMsaViewerMenuItem().setOnAction(e-> controller.getMsaButton().fire());

        controller.getJalViewMenuItem().setOnAction(e->{
            JalviewFX jalviewFX = new JalviewFX();
            jalviewFX.start(new Stage());
        });

        controller.getSignalPButton().setOnAction(e-> {

            saveClustSignal(controller);
            var signalP = new signalpPrediction();
            controller.getProgressBar().visibleProperty().bind(signalP.runningProperty());
            controller.getProgressBar().progressProperty().bind(signalP.progressProperty());
            controller.getSignalPButton().setDisable(true);

            signalP.setOnSucceeded(s-> {
                try {
                    controller.getSignalPButton().setDisable(false);
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
                try {
                    signalPResults(controller);
                    signalPUpdateTree(controller);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });
            signalP.start();
        });

        controller.getDeepTMHMMMenuItem().setOnAction(e-> {
            deepTMHMMStarter deepTMHMMStarter = new deepTMHMMStarter(controller);

            try {
                deepTMHMMStarter.start(new Stage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getKmerMenuItem().setOnAction(e->{
            var magic = new magicRun(this, controller);

            TextInputDialog textInputDialog = new TextInputDialog("4");
            textInputDialog.setHeaderText("Enter the Kmer Size");
            textInputDialog.showAndWait();

            TextInputDialog textInputDialog2 = new TextInputDialog("0");
            textInputDialog2.setHeaderText("Enter the Tolerance");
            textInputDialog2.showAndWait();


            int kmerSize = Integer.parseInt(textInputDialog.getEditor().getText());
            int kmerTol = Integer.parseInt(textInputDialog2.getEditor().getText());

            magic.kmerSearch(kmerSize, kmerTol);
        });

        controller.getCheckMSAMenuItem().setOnAction(e-> {
            TextInputDialog textInputDialog = new TextInputDialog("0");
            textInputDialog.setHeaderText("Enter the Tolerance");

            textInputDialog.showAndWait();
            int tolerance = Integer.parseInt(textInputDialog.getEditor().getText());

            var magic = new magicRun(this, controller);
            try {
                magic.checkMSA(tolerance);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getClusterCount2Label().textProperty().bind(controller.getClusterCountLabel().textProperty());

        controller.getSaveEpitopesMenuItem().setOnAction(e->{
            if(controller.getConservedTreeView().getRoot() != null) {
                saveEpitopes(stage, controller);
            }
            else{
                controller.getInfoLabel().setText("No results to be saved yet");
            }
        });

        controller.getProteinViewerButton().setOnAction(e-> {
            ProteomExplorer proteomExplorer = new ProteomExplorer(sequence);
            try {
                proteomExplorer.start(new Stage());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getRemoveParalogsMenuItem().setOnAction(e-> {
            try {
                removeParalogs(controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getExcludeSpecieMenuItem().setOnAction(e->{
            try {
                excludeSpec(stage, controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getLocPredMenuItem().setOnAction(e->locPred(controller));
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
        var directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open directory");

        var selectedDirectory = directoryChooser.showDialog(stage);
        importFiles(controller, selectedDirectory);
    }

    private void importFiles(WindowController controller, File selectedDirectory) throws IOException {
        fastaParser data = new fastaParser();
        int count = 0;

        dataMap.clear();
        input.clear();

        if (selectedDirectory != null)
        {
            controller.getInfoLabel().setText("empty");
            File[] files = selectedDirectory.listFiles();

            if(files != null) {
                for (File file : files) {
                    if (file.getName().endsWith(".fasta") || file.getName().endsWith(".fa") ||
                            file.getName().endsWith(".faa")) {
                        this.input.add(file.getAbsolutePath());
                        count += 1;
                        data.read(file.getAbsolutePath());
                    }
                }
            }
            // change the regName to match the annotation nuccore/bakta
            String header = data.get(0).header();
            String annotation = "";
            if(header.split("protein=").length > 1){
                annotation = "NucCore";
                RegName = "protein=";
            } else if (header.split("product=").length > 1) {
                annotation = "Bakta";
                RegName = "product=";
            }
            else{
                RegName = " ";
            }

            controller.getInfoLabel().setText(String.valueOf(count + " FastA files are loaded,   Annotation: " +
                    annotation));
            this.spec = count;
        }
        controller.getMinMatchesTxtField().setMax(spec);

        for(var HeaderSequence : data){
            String key = "";
            if(HeaderSequence.header().startsWith("lcl|")) {
                key = HeaderSequence.header().substring(4).split(" ")[0];
            }
            else{
                key = HeaderSequence.header().split("\\|")[1];
            }
            this.dataMap.put(key, HeaderSequence);
        }
    }

    private void removeParalogs(WindowController controller) throws IOException {
        var lastIndex = input.get(0).lastIndexOf("/");
        var path = input.get(0).substring(0, lastIndex);

        var pipeline = new processExecutor(new String[]{"sh", "./src/model/removeParalogs.sh", path});
        pipeline.run();

        controller.getInfoLabel().setText("Paralogs are removed!");
        importFiles(controller, new File(path+"/preProcessed/"));
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

        if(!toShow.isEmpty()) {
            show(controller);
        }
        if(!toRemove.isEmpty()) {
            remove(controller);
        }
    }

    private void onlyProName(WindowController controller, TreeItem<String> treeRoot,
                             HashMap<TreeItem<String>, String> proNames) {

        var children = treeRoot.getChildren();

        if(controller.getHeaderCheckBox().isSelected()) {
            proNames.clear();
            if (!this.dataMap.isEmpty()) {
                for (var ch : children) {
                    proNames.put(ch, ch.getValue());
                    if (ch.getValue().split(RegName).length == 2) {
                        ch.setValue(ch.getValue().split(RegName)[1].split("]")[0]);
                    }
                    for (var ch2 : ch.getChildren()) {
                        proNames.put(ch2, ch2.getValue());
                        if (ch2.getValue().split(RegName).length == 2) {
                            ch2.setValue(ch2.getValue().split(RegName)[1].split("]")[0]);
                        }
                    }
                }
            }
        }

        else {
            for (var ch : children) {
                if(proNames.containsKey(ch)) {
                    ch.setValue(proNames.get(ch));
                }
                for (var ch2 : ch.getChildren()) {
                    if(proNames.containsKey(ch2)) {
                        ch2.setValue(proNames.get(ch2));
                    }
                }
            }
        }
        if(!signalP.isEmpty() && !children.get(0).getValue().contains("SignalP")) {
            signalPUpdateTree(controller);
        }
    }

    private void startButton(WindowController controller) {
        clearResults();
        signalP.clear();

        if(method != null) {
            try {
                new startClustering(controller, this, spec, method, input);
                controller.getMinMatchesTxtField().setValue(spec);
                if(new File("Results/cluster_cluster.tsv").exists()) {
                    summary(controller, spec);
                    numberOfClusters(controller);
                }
                controller.getResultsPane().setVisible(true);
                controller.getSignalPButton().setDisable(false);
                controller.getKmerMenuItem().setDisable(false);
                controller.getFilterButton().setDisable(false);
                controller.getSequenceCheckBox().setDisable(false);
                controller.getCheckMSAMenuItem().setDisable(false);
                controller.getFilterButton().setDisable(false);
                controller.getShowButton().setDisable(false);
                controller.getRemoveButton().setDisable(false);

                controller.getConservedTab().setDisable(true);
                controller.getResultsPane().getSelectionModel().select(controller.getResultsTab());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        else{
            controller.getInfoLabel().setText("Please choose a method!");
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
            }
            if(!toRemove.isEmpty()) {
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
        results.delete();
    }

    private void clearFilter(WindowController controller) {
        toShow.clear();
        toRemove.clear();
        controller.getFilterButton().fire();
    }

    public void numberOfClusters(WindowController controller) {
        var root = controller.getResultsTextArea().getRoot();
        var children = root.getChildren();

        controller.getClusterCountLabel().setText(String.valueOf(children.size()));
    }

    public void signalPResults(WindowController controller) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("Results/SignalP/prediction_results.txt"));

        String line;
        while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#")) {
                String[] parts = line.split("\t");
                if (parts[0].split(RegName).length > 1) {
                    signalP.put(parts[0].split(RegName)[1].split("]")[0], parts[1]+" ");
                } else {
                    signalP.put(parts[0], parts[1]+" ");
                }
            }
        }
    }

    public void signalPUpdateTree(WindowController controller) {
        var treeRoot = controller.getResultsTextArea().getRoot();
        var children = treeRoot.getChildren();

        if(!signalP.isEmpty()) {
            for (var ch : children) {
                if (controller.getHeaderCheckBox().isSelected()) {
                    ch.setValue(ch.getValue() + " | SignalP6= " + signalP.get(ch.getValue()));
                } else {
                    ch.setValue(ch.getValue() + " | SignalP6= " +
                            signalP.get(ch.getValue().split(RegName)[1].split("]")[0]));
                }
            }
        }
    }

    public void saveClustSignal(WindowController controller) {
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
    }

    private void saveEpitopes(Stage stage, WindowController controller) {
        var DirectoryChooser = new DirectoryChooser();
        DirectoryChooser.setTitle("Save epitopes as txt files");

        var selectedDir = DirectoryChooser.showDialog(stage);
        if (selectedDir != null)
        {
            try {
                var treeRoot = controller.getConservedTreeView().getRoot();
                var children = treeRoot.getChildren();
                BufferedWriter br = new BufferedWriter(new FileWriter(selectedDir + "/epitopes.txt"));

                for(var ch:children){
                    br.write(">" + ch.getValue() + "\n");
                    for (var ch2 : ch.getChildren()) {
                        br.write(ch2.getValue() + "\n");
                    }
                }
                br.close();
            }
            catch (IOException e) {
                controller.getInfoLabel().setText("ERROR: " + e.getMessage());
            }
        }
    }

    private void excludeSpec(Stage stage, WindowController controller) throws IOException {
        controller.getHeaderCheckBox().setSelected(false);
        var FileChooser = new FileChooser();
        FileChooser.setTitle("Open FastA file");
        FileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("fasta file", "*.fasta", "*.fa"),
                new FileChooser.ExtensionFilter("All files", "*.*"));
        var selectedFile = FileChooser.showOpenDialog(stage);
        if (selectedFile != null)
        {
            saveClustSignal(controller);

            ArrayList<String> command = new ArrayList<>();
            command.add("./src/model/mmseqs/bin/mmseqs");
            command.add("easy-cluster");
            command.add("Results/rep.fasta");
            command.add(selectedFile.getAbsolutePath());
            command.add("Results/exclude");
            command.add("tmp");


            var process = new processExecutor(command.toArray(new String[0]));
            process.run();

            BufferedReader reader = new BufferedReader(new FileReader("Results/exclude_cluster.tsv"));

            Map<String, String> map = new HashMap<>();

            String line;
            while((line = reader.readLine()) != null) {
                String[] parts = line.split("\t");
                if(!map.containsKey(parts[0])) {
                    map.put(parts[0], parts[1]);
                }
                else{
                    map.remove(parts[0]);
                }
            }

            var root = controller.getResultsTextArea().getRoot();
            var children = root.getChildren();

            for(var ch:children) {
                boolean found = false;
                for(var key:map.keySet()) {
                    if(ch.getValue().contains(key) || ch.getValue().contains(map.get(key))){
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    toRemove.add(ch);
                }
            }
            remove(controller);
            numberOfClusters(controller);
        }
    }

    public void locPred(WindowController controller){
        saveClustSignal(controller);

        HashMap<String, String> prediction = new HashMap<>();
        ArrayList<String> command = new ArrayList<>();

        command.add("./src/model/topologyPred/venv/bin/python");
        command.add("./src/model/topologyPred/locPred.py");
        command.add("-m");
        command.add("1");
        command.add("-i");
        command.add("Results/rep.fasta");

        processExecutor processExecutor = new processExecutor(command.toArray(new String[0]));

        try {
            processExecutor.run();

            BufferedReader reader = new BufferedReader(new FileReader("locPredResults.csv"));
            String line;

            while((line=reader.readLine()) != null) {
                String[] parts = line.split("\t");
                prediction.put(parts[0], parts[1]);
            }

            var children = controller.getResultsTextArea().getRoot().getChildren();

            for(var ch: children) {
                if(controller.getHeaderCheckBox().isSelected()) {
                    ch.setValue(ch.getValue() + " [Localization: " +
                            prediction.get(ch.getValue().split(" ")[0])+ "]");
                }
                else {
                    var name = ch.getValue().split("\t")[1].split(" ")[0];
                    ch.setValue(ch.getValue() + " [Localization: " + prediction.get(name) + "]");
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
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
