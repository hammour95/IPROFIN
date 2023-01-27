package proteinExplorer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;

public class ViewerWindowController {

    @FXML
    private ToggleGroup Color;

    @FXML
    private ToggleGroup Vis;

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private RadioButton aminoAcidRadioButton;

    @FXML
    private Label atomsLabel;

    @FXML
    private RadioButton atomsRadioButton;

    @FXML
    private RadioButton ballsAndSticksRadioButton;

    @FXML
    private CheckBox ballsCheckButton;

    @FXML
    private Pane centerPane;

    @FXML
    private Label chainsLabel;

    @FXML
    private MenuItem clearSelectionMenuItem;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private Label complexLabel;

    @FXML
    private MenuItem copyMenuItem;

    @FXML
    private CheckMenuItem darkModeMenuItem;

    @FXML
    private MenuItem fullScreenMenuItem;

    @FXML
    private TextField getIdTextField;

    @FXML
    private Button getPdbButton;

    @FXML
    private Label infoLabel;

    @FXML
    private TreeView<String> infoTreeView;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Label monomersLabel;

    @FXML
    private TextArea pdbTextArea;

    @FXML
    private Pane pieAminoAcidsPane;

    @FXML
    private Pane pieStructurePane;

    @FXML
    private MenuItem redoMenuItem;

    @FXML
    private RadioButton ribbonsRadioButton;

    @FXML
    private Button savePdbButton;

    @FXML
    private Slider scaleSlider;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<String> searchListView;

    @FXML
    private ListView<Character> sequenceListView;

    @FXML
    private Slider similaritySlider;

    @FXML
    private CheckBox sticksCheckButton;

    @FXML
    private Slider sticksScaleBar;

    @FXML
    private RadioButton structureRadioButton;

    @FXML
    private TabPane tabsTabPane;

    @FXML
    private MenuItem undoMenuItem;

    @FXML
    private Tab viewerTab;

    public ToggleGroup getColor() {
        return Color;
    }

    public ToggleGroup getVis() {
        return Vis;
    }

    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public RadioButton getAminoAcidRadioButton() {
        return aminoAcidRadioButton;
    }

    public Label getAtomsLabel() {
        return atomsLabel;
    }

    public RadioButton getAtomsRadioButton() {
        return atomsRadioButton;
    }

    public RadioButton getBallsAndSticksRadioButton() {
        return ballsAndSticksRadioButton;
    }

    public CheckBox getBallsCheckButton() {
        return ballsCheckButton;
    }

    public Pane getCenterPane() {
        return centerPane;
    }

    public Label getChainsLabel() {
        return chainsLabel;
    }

    public MenuItem getClearSelectionMenuItem() {
        return clearSelectionMenuItem;
    }

    public MenuItem getCloseMenuItem() {
        return closeMenuItem;
    }

    public Label getComplexLabel() {
        return complexLabel;
    }

    public MenuItem getCopyMenuItem() {
        return copyMenuItem;
    }

    public CheckMenuItem getDarkModeMenuItem() {
        return darkModeMenuItem;
    }

    public MenuItem getFullScreenMenuItem() {
        return fullScreenMenuItem;
    }

    public TextField getGetIdTextField() {
        return getIdTextField;
    }

    public Button getGetPdbButton() {
        return getPdbButton;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public TreeView<String> getInfoTreeView() {
        return infoTreeView;
    }

    public TabPane getMainTabPane() {
        return mainTabPane;
    }

    public Label getMonomersLabel() {
        return monomersLabel;
    }

    public TextArea getPdbTextArea() {
        return pdbTextArea;
    }

    public Pane getPieAminoAcidsPane() {
        return pieAminoAcidsPane;
    }

    public Pane getPieStructurePane() {
        return pieStructurePane;
    }

    public MenuItem getRedoMenuItem() {
        return redoMenuItem;
    }

    public RadioButton getRibbonsRadioButton() {
        return ribbonsRadioButton;
    }

    public Button getSavePdbButton() {
        return savePdbButton;
    }

    public Slider getScaleSlider() {
        return scaleSlider;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public ListView<String> getSearchListView() {
        return searchListView;
    }

    public ListView<Character> getSequenceListView() {
        return sequenceListView;
    }

    public Slider getSimilaritySlider() {
        return similaritySlider;
    }

    public CheckBox getSticksCheckButton() {
        return sticksCheckButton;
    }

    public Slider getSticksScaleBar() {
        return sticksScaleBar;
    }

    public RadioButton getStructureRadioButton() {
        return structureRadioButton;
    }

    public TabPane getTabsTabPane() {
        return tabsTabPane;
    }

    public MenuItem getUndoMenuItem() {
        return undoMenuItem;
    }

    public Tab getViewerTab() {
        return viewerTab;
    }
}
