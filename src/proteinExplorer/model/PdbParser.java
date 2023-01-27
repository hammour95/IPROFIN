package proteinExplorer.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * PDB File parser
 * by: Mohammad Majd Hammour
 *
 * reads .pdb & .pdb.gz & String in PDB format (Downloaded from RCSB)
 */
public class PdbParser {

    String pdbContents = "";

    public PdbComplex read(String fileName) throws IOException {

        // Initializing the complex
        PdbComplex complex = new PdbComplex(new ArrayList<PdbPolymer>());

        BufferedReader r;
        // gzipped files
        if (fileName.endsWith(".gz")) {
            InputStream fileStream = new FileInputStream(fileName);
            InputStream gzipStream = new GZIPInputStream(fileStream);
            Reader decoder = new InputStreamReader(gzipStream);
            r = new BufferedReader(decoder);

            // .pdb files
        } else if (fileName.endsWith(".pdb")) {
            r = new BufferedReader(new FileReader(fileName));
        }
        // PDB as String
        else {
            r = new BufferedReader(new StringReader(fileName));
        }

        // Some variables to simplify reading
        PdbMonomer residue = null;
        PdbPolymer chain = null;

        int chainNum = 0;
        int aaNum = 0;

        boolean flag = true;   // the first iteration
        int index = 0;         // used to check if we ...
        int newIndex = 0;      // ... still in the same monomer

        // as the Structure comes before Atom entries
        // the structure saved first as Map to save time and memory
        // reading two times. Key is the Monomer index and value is the structure
        Map<Integer, String> secStruc = new HashMap<Integer, String>();

        String line = null;

        StringBuilder contents = new StringBuilder();

        while ((line = r.readLine()) != null) {
            // our model variables
            String name = "";
            String letter = "";
            int radius = 0;
            Color color = null;
            int id = 0;
            int monoId = 0;
            String role = "";
            Point3D coords = null;
            String aa = "";
            String chId = "";
            String structure = "Loop";

            contents.append(line).append('\n');

            // getting the lines of the atoms
            if (line.startsWith("ATOM")) {
                id = Integer.parseInt(line.substring(6, 11).replace(" ", ""));
                monoId =Integer.parseInt(line.substring(22, 26).replace(" ", ""));;
                role = line.substring(12, 16).replace(" ", "");
                aa = line.substring(17, 20);
                chId = line.substring(21, 22);
                coords = new Point3D(Double.parseDouble(line.substring(30, 38)),
                        Double.parseDouble(line.substring(38, 46)),
                        Double.parseDouble(line.substring(46, 54)));
                letter = line.substring(76, 78).replace(" ", "");
                // check if the secondary structure is available
                if(secStruc.containsKey(monoId)) {
                    structure = secStruc.get(monoId);
                }

                // Each atom will get his name, radius and color
                switch (line.substring(13, 14)) {
                    case "N" -> {
                        name = "Nitrogen";
                        radius = 71;
                        color = Color.BLUE;
                    }
                    case "C" -> {
                        name = "Carbon";
                        radius = 91;
                        color = Color.GRAY;
                    }
                    case "O" -> {
                        name = "Oxygen";
                        radius = 66;
                        color = Color.RED;
                    }
                    case "H" -> {
                        name = "Hydrogen";
                        radius = 31;
                        color = Color.WHITE;
                    }
                    case "S" -> {
                        name = "sulfur";
                        radius = 100;
                        color = Color.YELLOW;
                    }
                }
                // new atom will be created
                PdbAtom atom = new PdbAtom(name, letter, radius, color, id, role, coords);

                newIndex = Integer.parseInt(line.substring(22, 26).replace(" ", ""));

                // the First entry
                if(flag){
                    index = newIndex;
                    flag = false;

                    chain = new PdbPolymer(new ArrayList<PdbMonomer>(), chainNum, chId);
                    // adding the first chain
                    complex.getPolymers().add(chain);

                    residue = new PdbMonomer(new ArrayList<PdbAtom>(), getOneLetter(aa), structure, monoId, aaNum);
                    // adding the first residue
                    chain.getMonomers().add(residue);

                    // adding the first at om
                    residue.getAtoms().add(atom);
                }
                // not the first entry
                else{
                    // check if we are in the same chain
                    if(chain.getLabel().equals(chId)){
                        // check if we are in the same Monomer
                        if(index == newIndex){
                            residue.getAtoms().add(atom);
                        }
                        else{
                            // add new Monomer
                            residue = new PdbMonomer(new ArrayList<PdbAtom>(), getOneLetter(aa), structure, monoId, aaNum);
                            chain.getMonomers().add(residue);
                            residue.getAtoms().add(atom);
                            index = newIndex;
                            aaNum += 1;
                        }
                    }
                    else {
                        // add new chain
                        chainNum += 1;
                        chain = new PdbPolymer(new ArrayList<PdbMonomer>(), chainNum, chId);
                        residue = new PdbMonomer(new ArrayList<PdbAtom>(), getOneLetter(aa), structure, monoId, aaNum);
                        chain.getMonomers().add(residue);
                        residue.getAtoms().add(atom);
                    }
                }
            }

            // Checking the structure entries
            if(line.startsWith("HELIX")) {
                int start = Integer.parseInt(line.substring(21, 25).replace(" ", ""));
                int end = Integer.parseInt(line.substring(33, 37).replace(" ", ""));

                for(int i = start; i <= end; i++) {
                    secStruc.put(i, "Helix");
                }
            }
            if(line.startsWith("SHEET")) {
                int start = Integer.parseInt(line.substring(22, 26).replace(" ", ""));
                int end = Integer.parseInt(line.substring(33, 37).replace(" ", ""));

                for(int i = start; i <= end; i++) {
                    secStruc.put(i, "Sheet");
                }
            }

        }
        pdbContents = contents.toString();

        return complex;
    }


    private char getOneLetter(String aa) {
        Map<String, Character> AminoAcids = new HashMap<>();
        AminoAcids.put("ALA",'A');
        AminoAcids.put("ARG",'R');
        AminoAcids.put("ASN",'N');
        AminoAcids.put("ASP",'D');
        AminoAcids.put("CYS",'C');
        AminoAcids.put("GLU",'E');
        AminoAcids.put("GLN",'Q');
        AminoAcids.put("GLY",'G');
        AminoAcids.put("HIS",'H');
        AminoAcids.put("ILE",'I');
        AminoAcids.put("LYS",'K');
        AminoAcids.put("MET",'M');
        AminoAcids.put("PHE",'F');
        AminoAcids.put("PRO",'P');
        AminoAcids.put("SER",'S');
        AminoAcids.put("THR",'T');
        AminoAcids.put("TRP",'W');
        AminoAcids.put("TYR",'Y');
        AminoAcids.put("VAL",'V');
        AminoAcids.put("LEU",'L');

        return AminoAcids.get(aa);
    }

    public String getSequence(PdbComplex complex) {
        StringBuilder seq = new StringBuilder();

        for(PdbPolymer polymer: complex.getPolymers()) {
            for (PdbMonomer monomer : polymer.getMonomers()) {
                seq.append(monomer.getLabel());
            }
        }
        return seq.toString();
    }

    public int getChainCount(PdbComplex complex) {
        return complex.getPolymers().size();
    }

    public int getMonomerCount(PdbComplex complex) {
        int count = 0;

        for(PdbPolymer polymer: complex.getPolymers()) {
            count += polymer.getMonomers().size();
        }
        return count;
    }

    public int getAtomCount(PdbComplex complex) {
        int count = 0;

        for(PdbPolymer polymer: complex.getPolymers()) {
            for (PdbMonomer monomer : polymer.getMonomers()) {
                count += monomer.getAtoms().size();
            }
        }
        return count;
    }

    public String getContents(){
        return pdbContents;
    }
}