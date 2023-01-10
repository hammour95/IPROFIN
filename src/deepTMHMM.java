import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;

import model.processExecutor;

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
