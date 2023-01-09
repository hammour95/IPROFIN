import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import model.processExecutor;
import model.fastaParser;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MSA extends Application {

    WindowController controller;

    GridPane gridPane;
    VBox vBox;

    ScrollPane scrollPane;
    ArrayList<String> entries = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {

        gridPane = new GridPane();

        if(!entries.isEmpty()) {
            printListView();
        }
        var scrollPane = new ScrollPane();
        scrollPane.setContent(gridPane);
        Scene scene = new Scene(scrollPane, 800, 500);

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
        printListView();
    }

    public void printListView() throws IOException {
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

        if(gridPane != null) {
            gridPane = new GridPane();
            int r = 0;
            for(var headerSequence: parser) {
                int c = 0;
                gridPane.addRow(r);
                r ++;
                for(var nec: headerSequence.sequence().toCharArray()){
                    if(c == 0) {
                        gridPane.addColumn(c);
                    }
                    gridPane.add(new Label(String.valueOf(nec)), c, r);
                    c++;
                }
            }
        }
    }
}
