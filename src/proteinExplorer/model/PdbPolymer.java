package proteinExplorer.model;

import java.util.ArrayList;

/**
 * Class representing the Polymers
 */
public class PdbPolymer {
    ArrayList<PdbMonomer> monomers;
    int number;
    String label;

    public PdbPolymer(ArrayList<PdbMonomer> monomers, int number, String label) {
        this.monomers = monomers;
        this.number = number;
        this.label = label;
    }

    public ArrayList<PdbMonomer> getMonomers() {
        return monomers;
    }

    public int getNumber() {
        return number;
    }

    public String getLabel() {
        return label;
    }
}
