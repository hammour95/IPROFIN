import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WindowController {

    @FXML
    private MenuItem aboutMenuItem;

    @FXML
    private ListView<String> adjecencyListTextArea;

    @FXML
    private MenuItem closeMenuItem;

    @FXML
    private ChoiceBox<String> covModeChoiceBox;

    @FXML
    private Slider coverageSlider;

    @FXML
    private CheckMenuItem darkModeMenuItem;

    @FXML
    private Button filterButton;

    @FXML
    private ListView<String> flcTextArea;

    @FXML
    private MenuItem fullScreenMenuItem;

    @FXML
    private Label infoLabel;

    @FXML
    private Slider minMatchesTxtField;

    @FXML
    private Slider minSeqIdSlider;

    @FXML
    private MenuItem mmseqsUsageMenuItem;

    @FXML
    private MenuItem openMenuItem;

    @FXML
    private ListView<String> representativesTextArea;

    @FXML
    private TreeView<String> resultsTextArea;

    @FXML
    private Button startButton;

    @FXML
    private TextArea stdOutTextArea;

    @FXML
    private CheckBox sequenceCheckBox;

    @FXML
    private CheckBox headerCheckBox;

    @FXML
    private MenuItem saveMenuItem;

    @FXML
    private MenuItem saveClustersMenuItem;

    @FXML
    private TextField removeTextField;

    @FXML
    private Button removeButton;

    @FXML
    private Button showButton;


    public MenuItem getAboutMenuItem() {
        return aboutMenuItem;
    }

    public ListView<String> getAdjecencyListTextArea() {
        return adjecencyListTextArea;
    }

    public MenuItem getCloseMenuItem() {
        return closeMenuItem;
    }

    public ChoiceBox<String> getCovModeChoiceBox() {
        return covModeChoiceBox;
    }

    public Slider getCoverageSlider() {
        return coverageSlider;
    }

    public CheckMenuItem getDarkModeMenuItem() {
        return darkModeMenuItem;
    }

    public Button getFilterButton() {
        return filterButton;
    }

    public ListView<String> getFlcTextArea() {
        return flcTextArea;
    }

    public MenuItem getFullScreenMenuItem() {
        return fullScreenMenuItem;
    }

    public Label getInfoLabel() {
        return infoLabel;
    }

    public Slider getMinMatchesTxtField() {
        return minMatchesTxtField;
    }

    public Slider getMinSeqIdSlider() {
        return minSeqIdSlider;
    }

    public MenuItem getMmseqsUsageMenuItem() {
        return mmseqsUsageMenuItem;
    }

    public MenuItem getOpenMenuItem() {
        return openMenuItem;
    }

    public ListView<String> getRepresentativesTextArea() {
        return representativesTextArea;
    }

    public TreeView<String> getResultsTextArea() {
        return resultsTextArea;
    }

    public Button getStartButton() {
        return startButton;
    }

    public TextArea getStdOutTextArea() {
        return stdOutTextArea;
    }

    public CheckBox getSequenceCheckBox() {
        return sequenceCheckBox;
    }

    public CheckBox getHeaderCheckBox() {
        return headerCheckBox;
    }

    public MenuItem getSaveMenuItem() {
        return saveMenuItem;
    }

    public MenuItem getSaveClustersMenuItem() {
        return saveClustersMenuItem;
    }

    public TextField getRemoveTextField() {
        return removeTextField;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public Button getShowButton() {
        return showButton;
    }
}
