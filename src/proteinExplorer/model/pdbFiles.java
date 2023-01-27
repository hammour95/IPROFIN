package proteinExplorer.model;

import java.io.*;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Reading files from directory and get only pdb files
 * or gzipped pdb files
 *
 * Saves the files with the name of the Complex in HashMap
 */
public class pdbFiles extends HashMap<String, String> {

    public void read(File file) throws IOException {
        if(file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            for (File pdb : listOfFiles) {
                if (pdb.getName().endsWith(".pdb")) {
                    BufferedReader r = new BufferedReader(new FileReader(pdb.getPath()));
                    String line = null;
                    while ((line = r.readLine()) != null) {
                        if (line.startsWith("TITLE")) {
                            String name = line.substring(10);
                            put(name, pdb.getPath());
                            break;
                        }
                    }
                }
                if (pdb.getName().endsWith(".pdb.gz")) {
                    InputStream fileStream = new FileInputStream(pdb.getPath());
                    InputStream gzipStream = new GZIPInputStream(fileStream);
                    Reader decoder = new InputStreamReader(gzipStream);
                    BufferedReader buffered = new BufferedReader(decoder);

                    String line = null;
                    while ((line = buffered.readLine()) != null) {
                        if (line.startsWith("TITLE")) {
                            String name = line.substring(10);
                            put(name, pdb.getPath());
                            break;
                        }
                    }
                }
            }
        }
    }
}
