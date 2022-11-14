import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class fastaParser extends ArrayList<fastaParser.HeaderSequence> {

    public void read(String fileName) throws IOException
    {
        try(BufferedReader reader = new BufferedReader(new FileReader(fileName)))
        {
            read(reader);
        }
    }

    public void read(BufferedReader r) throws IOException {

        String line = null;
        String header = null;
        StringBuilder sequence = new StringBuilder();

        while ((line = r.readLine()) != null)
        {
            if (line.startsWith(">"))
            {

                if (header == null)
                {
                    header = line.substring(1);
                }
                else
                {
                    add(new HeaderSequence(header, sequence.toString()));
                    header = line.substring(1);
                    sequence.setLength(0);
                }

            }
            else
            {
                sequence.append(line);
            }
        }
        if (header != null)
        {
            add(new HeaderSequence(header, sequence.toString()));
        }
    }

    public static record HeaderSequence(String header, String sequence){}
}
