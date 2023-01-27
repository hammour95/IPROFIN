package proteinExplorer;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;
import java.util.Objects;

/**
 * this creates scene graph from a fxml file
 * Majd Hammour
 */
public class ViewerWindowView {
	private final ViewerWindowController controller;
	private final Parent root;

	public ViewerWindowView() throws IOException {
		try (var ins = Objects.requireNonNull(getClass().getResource("/proteinExplorer/ViewerWindow.fxml")).openStream()) {
			var fxmlLoader = new FXMLLoader();
			root = fxmlLoader.load(ins);
			controller = fxmlLoader.getController();
		}
	}

	public ViewerWindowController getController() {
		return controller;
	}

	public Parent getRoot() {
		return root;
	}
}
