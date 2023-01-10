import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import model.processExecutor;
import model.fastaParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class MSA extends Application {

    WindowController controller;

    WebView webView;
    ArrayList<String> entries = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        webView = new WebView();

        var webEngine = webView.getEngine();

        webEngine.load("http://localhost:63343/IPROFIN/model/MSAViewer/viewer.html?_ijt=gbso9fg1tutoeq3m7diunsvn9o&_ij_reload=RELOAD_ON_SAVE");

        Scene scene = new Scene(webView, 1200, 600);

        primaryStage.setTitle("MSA Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public MSA(WindowController controller) {
        this.controller = controller;
    }

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
    }


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

        fastaParser parser = new fastaParser();
        parser.read("src/aligned.fasta");
    }
}
