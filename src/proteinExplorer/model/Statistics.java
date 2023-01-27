package proteinExplorer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * gets some information about the complex needed
 * to generate the charts
 */
public class Statistics {
    private final PdbComplex complex;
    private final int Sheets;
    private final int Helices;

    private final int Loops;

    private final Map<Character, Integer> aminoAcids;

    private final int chains;

    private final int monomers;

    private final int atoms;


    public Statistics(PdbComplex complex) {
        this.complex = complex;

        int Sheets = 0;
        int Helices = 0;
        int Loops = 0;
        int atoms = 0;
        int monomers = 0;
        int chains = 0;

        Map<Character, Integer> aminoAcids = new HashMap<>();

        for(PdbPolymer polymer: complex.getPolymers()) {
            chains +=1;
            for (PdbMonomer monomer : polymer.getMonomers()) {
                monomers += 1;
                if(!aminoAcids.containsKey(monomer.getLabel()))
                    aminoAcids.put(monomer.getLabel(), 1);
                else
                    aminoAcids.replace(monomer.getLabel(), aminoAcids.get(monomer.getLabel())+1);

                if(monomer.getGetSecStructure().equals("Sheet"))
                    Sheets += 1;
                else if(monomer.getGetSecStructure().equals("Helix"))
                    Helices += 1;
                else if(monomer.getGetSecStructure().equals("Loop"))
                    Loops += 1;

                for(PdbAtom atom: monomer.getAtoms()){
                    atoms += 1;
                }
            }
        }
        this.Helices = Helices;
        this.Sheets = Sheets;
        this.Loops = Loops;
        this.aminoAcids = aminoAcids;
        this.chains = chains;
        this.monomers = monomers;
        this.atoms = atoms;
    }

    public PdbComplex getComplex() {
        return complex;
    }

    public int getSheets() {
        return Sheets;
    }

    public int getHelices() {
        return Helices;
    }

    public int getLoops() {
        return Loops;
    }

    public Map<Character, Integer> getAminoAcids() {
        return aminoAcids;
    }

    public int getChains() {
        return chains;
    }

    public int getMonomers() {
        return monomers;
    }

    public int getAtoms() {
        return atoms;
    }
}
