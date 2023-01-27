package model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Executes command line process in the java program
 */
public class processExecutor {
    ProcessBuilder processBuilder = new ProcessBuilder();
    String[] commands;

    public processExecutor(String[] commands) {
        this.commands = commands;
        processBuilder.command(commands);
    }

    /**
     * executes the command and print the Terminal console to the java console
     * @return  String console
     */
    public String run() throws IOException {
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
