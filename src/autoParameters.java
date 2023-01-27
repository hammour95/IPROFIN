import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Basic optimal parameters finder
 * tries all the combination of ID and COV and report the
 * best one that maximize number of clusters that have
 * the exact number of species.
 * It generates also an interactive plot to check the
 * combinations.
 * (Slow... not used -> check "magicRun.java")
 */
public class autoParameters extends Application {

    ArrayList<Integer> clusters = new ArrayList<>();
    ArrayList<Double> idValue = new ArrayList<>();
    ArrayList<Double> covValue = new ArrayList<>();
    WindowController controller;
    WindowPresenter presenter;

    public autoParameters(WindowPresenter presenter, WindowController controller) {
        this.presenter = presenter;
        this.controller = controller;

    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        checkParams(presenter, controller);


        var centerPane = new StackPane();

        var figure = new Group();

        for(int i = 0; i < clusters.size(); i++) {
            var point = new Sphere(1);
            double id = idValue.get(i);
            double cov = covValue.get(i);

            point.setTranslateX(idValue.get(i) * 200);
            point.setTranslateY(-clusters.get(i));
            point.setTranslateZ(covValue.get(i) * 200);

            Tooltip.install(point, new Tooltip("ID: " + idValue.get(i) + "\nCOV: " + covValue.get(i) +
                    "\nCluster: "+ clusters.get(i)));

            point.setOnMouseClicked(e->{
                controller.getMinSeqIdSlider().setValue(id);
                controller.getCoverageSlider().setValue(cov);
            });
            figure.getChildren().add(point);
        }
        var yPoint = new Point3D(0, 1000, 0);
        var xPoint = new Point3D(100, 0, 0);
        var zPoint = new Point3D(0, 0, 100);

        var xAxis = new Cylinder(0.1, 400);
        var yAxis = new Cylinder(0.1, 1000);
        var zAxis = new Cylinder(0.1, 400);

        xAxis.setRotationAxis(yPoint.crossProduct(xPoint));
        xAxis.setRotate(yPoint.angle(xPoint));

        yAxis.setRotationAxis(yPoint.crossProduct(yPoint));
        yAxis.setRotate(yPoint.angle(yPoint));

        zAxis.setRotationAxis(yPoint.crossProduct(zPoint));
        zAxis.setRotate(yPoint.angle(zPoint));

        figure.getChildren().addAll(xAxis, yAxis, zAxis);

        var subScene = new SubScene(figure, 600, 600, true,
                SceneAntialiasing.BALANCED);

        subScene.widthProperty().bind(centerPane.widthProperty());
        subScene.heightProperty().bind(centerPane.heightProperty());

        // setting the camera
        var camera = new PerspectiveCamera(true);
        camera.setFarClip(10000);
        camera.setNearClip(0.001);
        camera.setTranslateZ(-100);

        // Mouse scroll zoom
        centerPane.setOnScroll(e->{
            camera.setTranslateZ(camera.getTranslateZ()+e.getDeltaY());
        });

        subScene.setCamera(camera);

        centerPane.getChildren().add(subScene);

        // enable figure rotation
        var figureTransformProperty = new SimpleObjectProperty<Transform>(new Rotate());
        figureTransformProperty.addListener((v, o, n) -> figure.getTransforms().setAll(n));
        MouseInteraction.installRotate(centerPane, figureTransformProperty, figure, camera);

        var scene = new Scene(centerPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();

        saveParam();

    }

    private void checkParams(WindowPresenter presenter, WindowController controller) throws IOException {

        var input = presenter.getInput();
        var spec = presenter.getSpec();
        var method = presenter.getMethod();

        for(double id = 0; id <= 1; id += 0.1) {
            controller.getMinSeqIdSlider().setValue(id);
            for(double cov = 0; cov <= 1; cov += 0.1) {
                controller.getCoverageSlider().setValue(cov);

                new startClustering(controller, presenter, spec, method, input);
                presenter.summary(controller, spec);

                var root = controller.getResultsTextArea().getRoot();
                var children = root.getChildren();

                int counter = 0;

                for(var ch: children) {
                    if(ch.getChildren().size() == spec + 1) {
                        counter += 1;
                    }
                }
                clusters.add(counter);
                idValue.add(id);
                covValue.add(cov);
            }
        }
    }

    public void saveParam() throws IOException {
        BufferedWriter br = new BufferedWriter(new FileWriter("params.tsv"));
        br.write("Clusters\tID\tCOV\n");
        for(int i = 0; i < clusters.size(); i++) {
            String cluster = String.valueOf(clusters.get(i));
            String id = String.valueOf(idValue.get(i));
            String cov = String.valueOf(covValue.get(i));

            br.write(cluster + "\t"+ id + "\t" + cov + "\n");
        }
        br.close();
    }
}
