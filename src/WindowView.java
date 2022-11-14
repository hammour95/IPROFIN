import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

/**
 * this creates scene graph from a fxml file
 * Majd Hammour
 */
public class WindowView {
    private final WindowController controller;
    private final Parent root;

    public WindowView() throws IOException {
        try (var ins = Objects.requireNonNull(getClass().getResource("Window.fxml")).openStream()) {
            var fxmlLoader = new FXMLLoader();
            root = fxmlLoader.load(ins);
            controller = fxmlLoader.getController();
        }
    }

    public WindowController getController() {
        return controller;
    }

    public Parent getRoot() {
        return root;
    }
}
