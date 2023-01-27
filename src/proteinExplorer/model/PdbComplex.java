package proteinExplorer.model;

import java.util.ArrayList;

/**
 * Class representing the Complex
 */
public class PdbComplex {
    ArrayList<PdbPolymer> polymers;

    public PdbComplex( ArrayList<PdbPolymer> polymers) {
        this.polymers = polymers;
    }

    public  ArrayList<PdbPolymer> getPolymers() {
        return polymers;
    }

}
