package model;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.util.ArrayList;

/**
 * Class executing the prediction of signal peptides using SignalP6
 * as a Service
 */
public class signalpPrediction extends Service<String> {

    @Override
    protected Task<String> createTask() {
        return new Task<String>() {
            @Override
            public String call() throws Exception {
                ArrayList<String> command = new ArrayList<>();
                command.add("./src/model/signalP/venv/bin/signalp6");
                command.add("--fastafile");
                command.add("Results/rep.fasta");
                command.add("--output_dir");
                command.add("Results/SignalP/");
                command.add("--mode");
                //command.add("slow-sequential");
                command.add("fast");


                processExecutor processExecutor = new processExecutor(command.toArray(new String[0]));


                return processExecutor.run();
            }
        };
    }
}
