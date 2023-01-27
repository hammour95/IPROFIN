package model;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Basic file reader
 * returns the content of a file as a String
 */
public class readFile extends Task<String> {
    String fileName;
    public readFile(String fileName) throws IOException {
        this.fileName = fileName;
    }

    @Override
    protected String call() throws Exception {
        StringBuilder text = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));

        String line;

        while((line = reader.readLine()) != null) {
            text.append(line).append("\n");
        }
        return text.toString();
    }
}
