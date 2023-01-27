package proteinExplorer.model;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

/**
 * Class representing the Atoms
 */
public class PdbAtom {
    String Name;
    String Letter;
    int RadiusPM;
    Color color;
    int id;
    String role;
    Point3D coord;

    public PdbAtom(String Name, String Letter, int RadiusPM, Color color,
                   int id, String role, Point3D coord){
        this.Name = Name;
        this.Letter = Letter;
        this.RadiusPM = RadiusPM;
        this.color = color;
        this.id = id;
        this.role = role;
        this.coord = coord;
    }

    public String getName() {
        return Name;
    }

    public String getLetter() {
        return Letter;
    }

    public int getRadiusPM() {
        return RadiusPM;
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public Point3D getCoord() {
        return coord;
    }
}
