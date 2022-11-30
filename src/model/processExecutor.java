package model;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class processExecutor  extends Task<String> {
    ProcessBuilder processBuilder = new ProcessBuilder();
    String[] commands;

    public processExecutor(String[] commands) {
        this.commands = commands;
        processBuilder.command(commands);
    }
    @Override
    protected String call() throws IOException {
        Process process = processBuilder.start();

        StringBuilder output = new StringBuilder();

        BufferedReader stdout = new BufferedReader(new InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        // Read the output from the command
        String s = null;
        while ((s = stdout.readLine()) != null) {
            System.out.println(s);
            output.append(s + "\n");
        }

        // Read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
            output.append(s + "\n");
        }

        return output.toString();
    }
}
