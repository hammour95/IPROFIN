package optionsWindow;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

public class optionsWindowView {
    private final OptionsWindowController optionsController;
    private final Parent root;

    public optionsWindowView() throws IOException {
        try (var ins = Objects.requireNonNull(getClass().getResource("optionsWindow.fxml")).openStream()) {
            var fxmlLoader = new FXMLLoader();
            root = fxmlLoader.load(ins);
            optionsController = fxmlLoader.getController();
        }
    }

    public OptionsWindowController getController() {
        return optionsController;
    }

    public Parent getRoot() {
        return root;
    }
}
