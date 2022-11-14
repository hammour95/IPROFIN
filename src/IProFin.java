import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class IProFin extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        var view = new WindowView(); // create view

        // set the stage and show
        stage.setScene(new Scene(view.getRoot()));
        var presenter = new WindowPresenter(stage, view.getController());
        stage.setTitle("IProFin");
        stage.show();
    }
}
