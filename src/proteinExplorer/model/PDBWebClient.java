package proteinExplorer.model;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * RestAPI connection with RCSB
 * gets a specific pdb entry using the pdb code
 * &
 * search for pdb entries with 70% similar sequence
 */
public class PDBWebClient {

    public String getPdb(String id) throws IOException {
        String code = id.toLowerCase();
        URL url = new URL("https://files.rcsb.org/download/" + code + ".pdb");

        return new String(getFromURL(url).readAllBytes());
    }


    public ArrayList<String> searchPdb(String seq, double sim) throws IOException {
        URL url = new URL("https://search.rcsb.org/rcsbsearch/v2/query?json=" +
                "%7B%22query%22%3A%7B%22type%22%3A%22terminal%22%2C%22service%22%3A%22sequence%22" +
                "%2C%22parameters%22%3A%7B%22evalue_cutoff%22%3A1%2C%22identity_cutoff%22%3A" + sim +
                "%2C%22sequence_type%22%3A%22protein%22%2C%22value%22" +
                "%3A%22" + seq +
                "%22%7D%7D%2C%22request_options%22%3A%7B%22scoring_strategy%22" +
                "%3A%22sequence%22%7D%2C%22return_type%22%3A%22entry%22%7D");

        String output = new String(getFromURL(url).readAllBytes());
        BufferedReader br = new BufferedReader(new StringReader(output));

        String line = null;
        ArrayList<String> entries = new ArrayList<>();

        while ((line=br.readLine()) != null) {
            if (line.contains("identifier")){
                String[] parts = line.replace(" ","").split(":");
                entries.add(parts[1].substring(1,5));
            }
        }
        return entries;
    }

    public static InputStream getFromURL(URL url) throws IOException {
        var connection = (HttpsURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();
        return connection.getInputStream();
    }
}
