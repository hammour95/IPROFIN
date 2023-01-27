import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import model.processExecutor;

import java.io.*;
import java.util.ArrayList;

/**
 * Multi Sequence Alignment
 * a class that get the selection from the results Window
 * export the cluster to a FastA file
 * then do the MSA using Clustal
 * and represent the MSA using "MSAViewer"
 */
public class MSA extends Application {

    WindowController controller;

    WebView webView;

    WebEngine webEngine;
    ArrayList<String> entries = new ArrayList<>();

    /**
     * Using a webView MSAViewer can be started
     *
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {


        webView = new WebView();

        webEngine = webView.getEngine();

        String currentPath = new java.io.File(".").getCanonicalPath();

        webEngine.load("file:///" + currentPath + "/src/model/MSAViewer/viewer.html");

        Scene scene = new Scene(webView, 1200, 600);

        primaryStage.setTitle("MSA Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public MSA(WindowController controller) {
        this.controller = controller;
    }

    /**
     * update the MSA if the selection was changed
     * First. by importing the members of the cluster into
     * an arrayList and call the alignment class
     * @throws IOException
     */
    public void update() throws IOException {
        var selected = controller.getResultsTextArea().getSelectionModel().getSelectedItem();
        entries.clear();
        for(var ch: selected.getChildren()) {
            if(!ch.isLeaf()) {
                entries.add(">" + ch.getValue());
                entries.add(ch.getChildren().get(0).getValue());
            }
        }
        alignCluster();
        if(webEngine != null)
            webEngine.reload();
    }

    /**
     * This class export the selected cluster into fastA file
     * Then starts clustal to do the alignment
     * and reads the results
     *
     * The results will be also exported as a variable in javaScript file
     * which can be used in MSAViewer
     *
     * @throws IOException
     */
    public void alignCluster() throws IOException {
        BufferedWriter wr = new BufferedWriter(new FileWriter("Results/cluster.fasta"));
        for(var i: entries) {
            wr.write(i + "\n");
        }
        wr.close();
        var command = new ArrayList<String>();
        command.add("src/model/clustal");
        command.add("-i");
        command.add("Results/cluster.fasta");
        command.add("-o");
        command.add("src/aligned.fasta");
        command.add("--force");

        processExecutor processExecutor = new processExecutor(command.toArray(new String[0]));
        processExecutor.run();

        BufferedReader reader = new BufferedReader(new FileReader("src/aligned.fasta"));

        BufferedWriter writer = new BufferedWriter(new FileWriter("src/model/MSAViewer/alignment.js"));

        String line;

        writer.write("var fasta = \"");
        while((line = reader.readLine()) != null) {
            writer.write(line + "\\n");
        }
        writer.write("\"");

        writer.close();
    }
}
