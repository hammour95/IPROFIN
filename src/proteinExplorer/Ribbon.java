package proteinExplorer;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import proteinExplorer.model.PdbComplex;
import proteinExplorer.model.PdbPolymer;

/**
 * Basic Ribbon implementation
 *
 * helix --> RED
 * Sheet --> YELLOW
 * loops --> GRAY
 *
 * Meshes are not completely smoothed!
 */
public class Ribbon {

    public static Group getRibbons (PdbComplex complex){
        Group ribbons = new Group();

        for(PdbPolymer polymer: complex.getPolymers()){
            var monomers = polymer.getMonomers();
            // getting two monomers
            for(int i=0; i < monomers.size() - 1; i++){
                var monomer = monomers.get(i);
                var nextMonomer = monomers.get(i+1);
                // the monomers have to have structure
                if(!monomer.getGetSecStructure().equals("Loop") && !nextMonomer.getGetSecStructure().equals("Loop")){

                    // getting the indices
                    var v1 = monomer.getSpecAtom("CA").getCoord();
                    var v2 = monomer.getSpecAtom("CB").getCoord();
                    var v0 = v1.add(v1.subtract(v2));

                    var v4 = nextMonomer.getSpecAtom("CA").getCoord();
                    var v5 = nextMonomer.getSpecAtom("CB").getCoord();
                    var v3 = v4.add(v4.subtract(v5));


                    float[] points = {
                            (float) v0.getX(), (float) v0.getY(), (float) v0.getZ(),
                            (float) v1.getX(), (float) v1.getY(), (float) v1.getZ(),
                            (float) v2.getX(), (float) v2.getY(), (float) v2.getZ(),

                            (float) v3.getX(), (float) v3.getY(), (float) v3.getZ(),
                            (float) v4.getX(), (float) v4.getY(), (float) v4.getZ(),
                            (float) v5.getX(), (float) v5.getY(), (float) v5.getZ(),
                    };


                    int[] faces = {
                            0, 0, 1, 0, 4, 0,
                            0, 0, 4, 0, 5, 0,
                            1, 0, 2, 0, 3, 0,
                            1, 0, 3, 0, 4, 0,

                            0, 0, 4, 0, 1, 0,  // sames triangles, facing other way
                            0, 0, 5, 0, 4, 0,
                            1, 0, 3, 0, 2, 0,
                            1, 0, 4, 0, 3, 0
                    };

                    int[] faceSmoothingGroups = {1, 1, 1, 1, 2, 2, 2, 2};

                    float[] texCoords = {0, 0};

                    var mesh = new TriangleMesh();
                    mesh.getPoints().addAll(points);
                    mesh.getFaces().addAll(faces);
                    mesh.getFaceSmoothingGroups().addAll(faceSmoothingGroups);
                    mesh.getTexCoords().addAll(texCoords);

                    var meshView = new MeshView(mesh);

                    // setting the colors
                    var material = new PhongMaterial();
                    if (monomer.getGetSecStructure().equals("Helix")) {
                        material.setDiffuseColor(Color.RED);
                        material.setSpecularColor(Color.RED.darker());
                    }
                    else if (monomer.getGetSecStructure().equals("Sheet")) {
                        material.setDiffuseColor(Color.YELLOW);
                        material.setSpecularColor(Color.YELLOW.darker());
                    }

                    meshView.setMaterial(material);
                    ribbons.getChildren().add(meshView);
                }
                // creating the loops
                else{
                    var yAxis = new Point3D(0, 100, 0);
                    var atom1 = monomer.getSpecAtom("N");
                    var atom2 = nextMonomer.getSpecAtom("N");

                    var midPoint = atom1.getCoord().midpoint(atom2.getCoord());
                    var direction = atom1.getCoord().subtract(atom2.getCoord());

                    var perpendicularAxis = yAxis.crossProduct(direction);
                    var angle = yAxis.angle(direction);

                    var stick = new Cylinder(0.1, atom1.getCoord().distance(atom2.getCoord()));
                    stick.setMaterial(new PhongMaterial(Color.BLACK));
                    stick.setRotationAxis(perpendicularAxis);
                    stick.setRotate(angle);

                    stick.setTranslateX(midPoint.getX());
                    stick.setTranslateY(midPoint.getY());
                    stick.setTranslateZ(midPoint.getZ());

                    ribbons.getChildren().add(stick);
                }
            }
        }
        return ribbons;
    }
}
