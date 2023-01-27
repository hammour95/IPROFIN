import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * User interface to represent the results of DeepTMHMM
 * ++++ Still basic ++++
 * reads the results file and
 * shows them in a gridView
 */
public class deepTMHMMStarter extends Application {

    WindowController controller;
    BorderPane borderPane;
    ScrollPane scrollPane;
    GridPane gridPane;
    Label label;

    @Override
    public void start(Stage primaryStage) throws Exception {

        deepTMHMM deepTM= new deepTMHMM(controller);

        controller.getProgressBar().visibleProperty().bind(deepTM.runningProperty());
        controller.getProgressBar().progressProperty().bind(deepTM.progressProperty());

        deepTM.setOnSucceeded(s-> {
            String ResPath = "Results/predicted_topologies.3line";

            ArrayList<String> lines = new ArrayList<>();

            borderPane = new BorderPane();
            label = new Label();
            gridPane = new GridPane();
            scrollPane = new ScrollPane(gridPane);
            gridPane.setAlignment(Pos.CENTER);

            borderPane.setTop(label);
            borderPane.setCenter(scrollPane);

            try {
                BufferedReader reader = new BufferedReader(new FileReader(ResPath));

                String line;

                while((line = reader.readLine()) != null) {
                    lines.add(line);
                }

                label.setText(lines.get(0));

                for(int i = 0; i < lines.get(1).length(); i++) {
                    int index = i+1;
                    char aminoAcid = lines.get(1).charAt(i);
                    char prediction = lines.get(2).charAt(i);
                    gridPane.add(new Label(String.valueOf(index)+ "  "), i, 0);
                    gridPane.add(new Label(String.valueOf(aminoAcid)), i, 1);
                    gridPane.add(new Label(String.valueOf(prediction)), i, 2);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Scene scene = new Scene(borderPane, 800, 125);

            primaryStage.setTitle("DeepTMHMM");
            primaryStage.setScene(scene);
            primaryStage.show();
        });
        deepTM.start();
    }

    public deepTMHMMStarter(WindowController controller){
        this.controller = controller;
    }
}
