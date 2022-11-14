import javafx.beans.property.BooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TreeItem;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import jdk.jfr.ContentType;
import model.*;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * The window presenter
 * This is where we communicate between the view and the model
 */
public class WindowPresenter {

    ArrayList<String> input = new ArrayList<>();
    int spec;
    String RegName = "product=";

    fastaParser data = new fastaParser();
    HashMap<String, fastaParser.HeaderSequence> dataMap = new HashMap<>();

    public WindowPresenter(Stage stage, WindowController controller) {

        controller.getCloseMenuItem().setOnAction(e-> Platform.exit());

        controller.getFullScreenMenuItem().setOnAction(e-> stage.setFullScreen(!stage.fullScreenProperty().get()));

        controller.getAboutMenuItem().setOnAction(e-> showAbout());

        controller.getSaveMenuItem().setOnAction(e-> save(stage, controller));

        controller.getSaveClustersMenuItem().setOnAction(e-> saveCluster(stage, controller));

        controller.getRemoveButton().setOnAction(e-> remove(controller));

        controller.getShowButton().setOnAction(e->show(controller));

        controller.getMmseqsUsageMenuItem().setOnAction(e-> {
            try {mmseqsAbout(controller);} catch (IOException ex) {throw new RuntimeException(ex);} });

        controller.getOpenMenuItem().setOnAction(e-> {
            try {
                openFile(stage, controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getStartButton().setOnAction(e-> {
            try {
                startMMseqs(controller);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        controller.getDarkModeMenuItem().selectedProperty().addListener(e -> {
            if(controller.getDarkModeMenuItem().isSelected())
                stage.getScene().getStylesheets().add("modena_dark.css");
            else
                stage.getScene().getStylesheets().remove("modena_dark.css");
        });

        controller.getFilterButton().setOnAction(e-> {
            File results = new File("Results/linclust_cluster.tsv");
            if(results.exists()) {
                int filter = (int)controller.getMinMatchesTxtField().getValue();
                try {
                    adjacencySummery(controller, filter);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        controller.getSequenceCheckBox().setOnAction(e-> controller.getFilterButton().fire());
        controller.getHeaderCheckBox().setOnAction(e-> controller.getFilterButton().fire());


        // adding the coverage mode options
        ArrayList<String> covMods = new ArrayList<>();
        covMods.add("coverage of query and target");
        covMods.add("coverage of target");
        covMods.add("coverage of query");
        covMods.add("target seq. length has to be at least x% of query length");
        covMods.add("query seq. length has to be at least x% of target length");
        covMods.add("short seq. needs to be at least x% of the other seq. length");

        ObservableList<String> list = FXCollections.observableArrayList(covMods);
        controller.getCovModeChoiceBox().setItems(list);
        controller.getCovModeChoiceBox().setValue("coverage of query and target");
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


    /**
     * Start the tool mmseqs2 on the line cluster mode and show the results
     */
    private void startMMseqs(WindowController controller) throws IOException {
        float minSeqId = (float) controller.getMinSeqIdSlider().getValue();
        float coverage = (float) controller.getCoverageSlider().getValue();

        int covMode = 0;
        switch (controller.getCovModeChoiceBox().getValue()) {
            case "coverage of query and target":
                break;
            case "coverage of target":
                covMode = 1;
                break;
            case "coverage of query":
                covMode = 2;
                break;
            case "target seq. length has to be at least x% of query length":
                covMode = 3;
                break;
            case "query seq. length has to be at least x% of target length":
                covMode = 4;
                break;
            case "short seq. needs to be at least x% of the other seq. length":
                covMode = 5;
                break;
        }

        if(this.input.isEmpty()) {
            controller.getInfoLabel().setText("please open the FastA files directory!");
        }

        else {
            controller.getInfoLabel().setText("Running mmseq2 // Parameters: minSeqId: " + minSeqId + " coverage: " +
                    coverage + " covMode: " + covMode);
            // make the results folder if it not exists
            File resultsDir = new File("Results/");
            if (!resultsDir.exists()){
                resultsDir.mkdirs();
            }

            // loading the command arguments
            mmseq mmseq = new mmseq(this.input.toArray(new String[this.input.size()]), minSeqId, coverage, covMode);
            processExecutor processExecutor = new processExecutor(mmseq.getCommand());

            // running mmseqs2 and generating the result files
            controller.getStdOutTextArea().setText(processExecutor.run());

            controller.getInfoLabel().setText("Finished mmseqs2 analysis // Spec.: " + spec +
                    " Parameters: minSeqId: " + minSeqId + " coverage: " +
                    coverage + " covMode: " + covMode);



            // Showing the result files in the plain results tab
            var serviceRep = new readFile("Results/linclust_rep_seq.fasta");
            serviceRep.setOnSucceeded(e-> {
                var results = serviceRep.getValue();
                for(String s: results.split("\n")){
                    controller.getRepresentativesTextArea().getItems().add(s);
                }
            });

            var serviceAdj = new readFile("Results/linclust_cluster.tsv");
            serviceAdj.setOnSucceeded(e->{
                var results = serviceAdj.getValue();
                for(String s: results.split("\n")){
                    controller.getAdjecencyListTextArea().getItems().add(s);
                }
            });

            var serviceFlc = new readFile("Results/linclust_all_seqs.fasta");
            serviceFlc.setOnSucceeded(e-> {
                var results = serviceFlc.getValue();
                for(String s: results.split("\n")){
                    controller.getFlcTextArea().getItems().add(s);
                }
            });
            Executors.newSingleThreadExecutor().submit(serviceFlc);
            Executors.newSingleThreadExecutor().submit(serviceRep);
            Executors.newSingleThreadExecutor().submit(serviceAdj);


            // Showing the results
            adjacencySummery(controller, 0);
        }
    }

    private void adjacencySummery(WindowController controller, int filter) throws IOException {
        controller.getRemoveButton().setDisable(false);
        controller.getShowButton().setDisable(false);
        BufferedReader reader = new BufferedReader(new FileReader("Results/linclust_cluster.tsv"));

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
                    br.write(">" + ch.getValue().split("\t")[1] + "\n");
                    br.write(ch.getChildren().get(0).getValue() + "\n");
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
                    if(!controller.getSequenceCheckBox().isSelected() &&
                            !controller.getHeaderCheckBox().isSelected()) {
                        BufferedWriter br = new BufferedWriter(new FileWriter(selectedDir + "/Cluster" + name +".fasta"));
                        br.write(">" + ch.getValue().split("\t")[1] + "\n");
                        br.write(ch.getChildren().get(0).getValue() + "\n");
                        for (var ch2 : ch.getChildren()) {
                            if (ch2.getValue().split(RegName).length == 2) {
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

        var toRemove = new ArrayList<TreeItem<String>>();

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

        var toRemove = new ArrayList<TreeItem<String>>();

        if(!children.isEmpty()) {
            var text = controller.getRemoveTextField().getText().toLowerCase();
            if(text.length() > 0) {
                for(var ch:children) {
                    var header = ch.getValue().toLowerCase();
                    if(!header.contains(text)) {
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
}
