import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import model.processExecutor;

public class deepTMHMM extends Task<String> {
    WindowController controller;

    @Override
    protected String call() throws Exception {
        var selected = controller.getResultsTextArea().getSelectionModel().getSelectedItem();

        BufferedWriter writer = new BufferedWriter(new FileWriter("Results/query.fasta"));
        writer.write(">" + selected.getValue() + "\n" + selected.getChildren().get(0));
        writer.close();

        ArrayList<String> command = new ArrayList<>();

        command.add("./src/model/signalP/venv/bin/python");
        command.add("./src/model/signalP/deepTM.py");
        command.add("-i");
        command.add("Results/query.fasta");

        processExecutor processExecutor = new processExecutor(command.toArray(new String[0]));


        return processExecutor.run();
    }

    public deepTMHMM(WindowController controller){
        this.controller = controller;
    }
}
