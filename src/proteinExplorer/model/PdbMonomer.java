package proteinExplorer.model;

import java.util.ArrayList;

/**
 * Class representing the Amino acids
 */
public class PdbMonomer {

    ArrayList<PdbAtom> atoms;
    char label;
    String getSecStructure;

    int id;

    int index;

    public PdbMonomer(ArrayList<PdbAtom> atoms, char label, String getSecStructure, int id, int index) {
        this.atoms = atoms;
        this.label = label;
        this.getSecStructure = getSecStructure;
        this.id = id;
        this.index = index;
    }

    public ArrayList<PdbAtom> getAtoms() {
        return atoms;
    }

    public char getLabel() {
        return label;
    }

    public String getGetSecStructure() {
        return getSecStructure;
    }

    public int getId() {
        return id;
    }

    // used to get CA and CB atoms to build the mashes
    public PdbAtom getSpecAtom(String label){
        for(PdbAtom atom: atoms){
            if(atom.getRole().equals(label))
                return atom;
        }
        return getSpecAtom("C");
    }

    public int getIndex() {
        return index;
    }
}
