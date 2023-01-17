import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import jalview.gui.AlignFrame;
import jalview.io.DataSourceType;
import jalview.io.FileLoader;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class JalviewFX extends Application {

    @Override
    public void start(Stage stage) {
        // load an alignment
        af = new
                FileLoader(false).LoadFileWaitTillLoaded("src/aligned.fasta",
                DataSourceType.FILE);
        // we may need to wait around because of some concurrency issues
        //while (af==null) {
        //  try { Thread.sleep(50); } catch (InterruptedException x) {}
        //
        //}
        af.setPreferredSize(new Dimension(800,550));
        final SwingNode swingNode = new SwingNode();
        createAndSetSwingContent(swingNode);

        StackPane pane = new StackPane();
        pane.getChildren().add(swingNode);

        stage.setScene(new Scene(pane, 800, 550));
        stage.show();
    }
    AlignFrame af=null;
    private void createAndSetSwingContent(final SwingNode swingNode) {

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // just show the alignment panel - mouse events are handle but no keyboard events

                swingNode.setContent((jalview.gui.AlignmentPanel)af.getAlignPanels().get(0));
                // alternatively this shows the whole alignment window - properly handles keyboard events
                //            swingNode.setContent(af);
                //            af.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
