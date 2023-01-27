package proteinExplorer;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.Property;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.util.Duration;

/**
 * Class for enabling mouse interactions (rotation)
 */
public class MouseInteraction {
    private static double oldX ;
    private static double oldY ;

    private static RotateTransition autoRotate ;

    private static Boolean rotating = false;

    public static void installRotate (Pane pane , Property<Transform> figureTransformProperty, Node node, PerspectiveCamera camera) {

        pane.setOnMousePressed (e->{
            oldX = e.getSceneX();
            oldY = e.getSceneY();
            e.consume();
        });

        pane.setOnMouseDragged(e->{
            pane.setCursor(Cursor.CLOSED_HAND);
            var delta = new Point2D(e.getSceneX() - oldX, e.getSceneY() -oldY);
            var dragOrthogonalAxis = new Point3D(delta.getY(), -delta.getX(), 0) ;

            if(e.isControlDown()) {
                camera.setTranslateX(camera.getTranslateX() + ((oldX - e.getSceneX()) / 10));
                camera.setTranslateY(camera.getTranslateY() + ((oldY - e.getSceneY()) / 10));
            }
            else {
                var rotate = new Rotate(0.25 * delta.magnitude(), dragOrthogonalAxis);
                figureTransformProperty.setValue(rotate.createConcatenation(figureTransformProperty.getValue()));
            }
            oldX = e.getSceneX();
            oldY = e.getSceneY();
            e.consume();

            if(e.isShiftDown()) {
                autoRotate = new RotateTransition();
                autoRotate.setNode(node);
                autoRotate.setAxis(dragOrthogonalAxis);
                autoRotate.setByAngle(360);
                autoRotate.setDuration(Duration.seconds(15));
                autoRotate.setCycleCount(1);
                autoRotate.setInterpolator(Interpolator.LINEAR);
                autoRotate.play();

                rotating = true;
            }
        });
        pane.setOnMouseReleased(e-> pane.setCursor(Cursor.DEFAULT));
    }
}
