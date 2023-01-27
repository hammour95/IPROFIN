package proteinExplorer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import proteinExplorer.model.PdbParser;

public class ProteomExplorer extends Application {

    String sequence;
    public ProteomExplorer(String sequence) {
        this.sequence = sequence;
    }
    @Override
    public void start(Stage stage) throws Exception {
        var view = new ViewerWindowView(); // create view

        // set the stage and show
        stage.setScene(new Scene(view.getRoot()));
        var model = new PdbParser();
        var presenter = new ViewerWindowPresenter(stage, view.getController(), model, sequence);

        for (char c : sequence.toCharArray())
            view.getController().getSequenceListView().getItems().add(c);

        stage.setTitle("Proteome Explorer");
        stage.show();
    }
}
