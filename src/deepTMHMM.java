import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import model.processExecutor;

/**
 * Starting the prediction Tool DeepTMHMM
 * First, from the selected cluster, the header and
 * representative sequence will be written in a FastA file
 * Then, using the commandExecuter
 * python script will be executed to make the prediction
 * on DTU "Technical University of Denmark" Servers
 */
public class deepTMHMM extends Service<String> {
    WindowController controller;

    public deepTMHMM(WindowController controller){
        this.controller = controller;
    }

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            protected String call() throws Exception {
                var selected = controller.getResultsTextArea().getSelectionModel().getSelectedItem();
                String header = "";
                if(controller.getHeaderCheckBox().isSelected()){
                    header = selected.getValue();
                }
                else{
                    header = selected.getValue().split("\t")[1];
                }
                BufferedWriter writer = new BufferedWriter(new FileWriter("Results/query.fasta"));
                writer.write(">" + header + "\n" + selected.getChildren().get(0).getValue());
                writer.close();

                ArrayList<String> command = new ArrayList<>();

                command.add("./src/model/signalP/venv/bin/python");
                command.add("./src/model/signalP/deepTM.py");
                command.add("-i");
                command.add("Results/query.fasta");

                processExecutor processExecutor = new processExecutor(command.toArray(new String[0]));


                return processExecutor.run();
            }
        };
    }
}
