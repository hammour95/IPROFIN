import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;

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

    @FXML
    private Button openButton;

    @FXML
    private Button saveButton;

    @FXML
    private Button saveClustersButton;

    @FXML
    private Button linclustButton;

    @FXML
    private Button clusterButton;

    @FXML
    private Button diamondButton;

    @FXML
    private Label toolLabel;

    @FXML
    private Label modeLabel;

    @FXML
    private CheckBox showAdvancedOptionsCheckBox;

    @FXML
    private Pane mmseqsAdvOptionsPane;

    @FXML
    private ChoiceBox<String> alnModeChoiceBox;

    @FXML
    private ChoiceBox<String> alnOutputModeChoiceBox;

    @FXML
    private TextField eValueTextField;

    @FXML
    private TextField minAlnLenTextField;

    @FXML
    private ChoiceBox<String> seqIdModeChoiceBox;

    @FXML
    private ChoiceBox<String> clusterModeChoiceBox;

    @FXML
    private TextField kmerPerSeqTextField;

    @FXML
    private TextField threadsTextField;

    @FXML
    private Pane optionsPane;

    @FXML
    private TabPane resultsPane;

    @FXML
    private Pane advancedDiamondPane;

    @FXML
    private CheckBox queryCoverCheckBox;

    @FXML
    private CheckBox subjectCoverCheckBox;

    @FXML
    private CheckBox eValueDCheckBox;

    @FXML
    private CheckBox gabOpenCheckBox;

    @FXML
    private CheckBox gabExtendCheckBox;

    @FXML
    private CheckBox matrixCheckBox;

    @FXML
    private CheckBox threadsDCheckBox;

    @FXML
    private TextField eValueDTextField;

    @FXML
    private TextField gabOpenTextField;

    @FXML
    private TextField gabExtendTextField;

    @FXML
    private TextField treadsDTextField;

    @FXML
    private ChoiceBox<String> matrixChoiceBox;

    @FXML
    private Button clearFiltersButton;

    @FXML
    private Label clusterCountLabel;

    @FXML
    private Button automaticButton;

    @FXML
    private Button msaButton;

    @FXML
    private Button signalPButton;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private MenuItem signalPMenuItem;

    @FXML
    private MenuItem deepTMHMMMenuItem;

    @FXML
    private MenuItem jalViewMenuItem;

    @FXML
    private MenuItem msaViewerMenuItem;

    @FXML
    private Tab resultsTab;

    @FXML
    private MenuItem kmerMenuItem;

    @FXML
    private Tab conservedTab;

    @FXML
    private TreeView<String> conservedTreeView;

    @FXML
    private MenuItem checkMSAMenuItem;

    @FXML
    private Label clusterCount2Label;

    @FXML
    private CheckBox header2CheckBox;

    @FXML
    private MenuItem saveEpitopesMenuItem;

    @FXML
    private MenuItem PDBStructureMenuItem;

    @FXML
    private Button proteinViewerButton;

    @FXML
    private MenuItem removeParalogsMenuItem;

    @FXML
    private MenuItem excludeSpecieMenuItem;

    @FXML
    private MenuItem locPredMenuItem;

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

    public Button getOpenButton() {
        return openButton;
    }

    public Button getSaveButton() {
        return saveButton;
    }

    public Button getSaveClustersButton() {
        return saveClustersButton;
    }

    public Button getLinclustButton() {
        return linclustButton;
    }

    public Button getClusterButton() {
        return clusterButton;
    }

    public Button getDiamondButton() {
        return diamondButton;
    }

    public Label getToolLabel() {
        return toolLabel;
    }

    public Label getModeLabel() {
        return modeLabel;
    }

    public CheckBox getShowAdvancedOptionsCheckBox() {
        return showAdvancedOptionsCheckBox;
    }

    public Pane getMmseqsAdvOptionsPane() {
        return mmseqsAdvOptionsPane;
    }

    public ChoiceBox<String> getAlnModeChoiceBox() {
        return alnModeChoiceBox;
    }

    public ChoiceBox<String> getAlnOutputModeChoiceBox() {
        return alnOutputModeChoiceBox;
    }

    public TextField geteValueTextField() {
        return eValueTextField;
    }

    public TextField getMinAlnLenTextField() {
        return minAlnLenTextField;
    }

    public ChoiceBox<String> getSeqIdModeChoiceBox() {
        return seqIdModeChoiceBox;
    }

    public ChoiceBox<String> getClusterModeChoiceBox() {
        return clusterModeChoiceBox;
    }

    public TextField getKmerPerSeqTextField() {
        return kmerPerSeqTextField;
    }

    public TextField getThreadsTextField() {
        return threadsTextField;
    }

    public Pane getOptionsPane() {
        return optionsPane;
    }

    public TabPane getResultsPane() {
        return resultsPane;
    }

    public Pane getAdvancedDiamondPane() {
        return advancedDiamondPane;
    }

    public CheckBox getQueryCoverCheckBox() {
        return queryCoverCheckBox;
    }

    public CheckBox getSubjectCoverCheckBox() {
        return subjectCoverCheckBox;
    }

    public CheckBox geteValueDCheckBox() {
        return eValueDCheckBox;
    }

    public CheckBox getGabOpenCheckBox() {
        return gabOpenCheckBox;
    }

    public CheckBox getGabExtendCheckBox() {
        return gabExtendCheckBox;
    }

    public CheckBox getMatrixCheckBox() {
        return matrixCheckBox;
    }

    public CheckBox getThreadsDCheckBox() {
        return threadsDCheckBox;
    }

    public TextField geteValueDTextField() {
        return eValueDTextField;
    }

    public TextField getGabOpenTextField() {
        return gabOpenTextField;
    }

    public TextField getGabExtendTextField() {
        return gabExtendTextField;
    }

    public TextField getTreadsDTextField() {
        return treadsDTextField;
    }

    public ChoiceBox<String> getMatrixChoiceBox() {
        return matrixChoiceBox;
    }

    public Button getClearFiltersButton() {
        return clearFiltersButton;
    }

    public Label getClusterCountLabel() {
        return clusterCountLabel;
    }

    public Button getAutomaticButton() {
        return automaticButton;
    }

    public Button getMsaButton() {
        return msaButton;
    }

    public Button getSignalPButton() {
        return signalPButton;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MenuItem getDeepTMHMMMenuItem() {
        return deepTMHMMMenuItem;
    }

    public MenuItem getSignalPMenuItem() {
        return signalPMenuItem;
    }

    public MenuItem getJalViewMenuItem() {
        return jalViewMenuItem;
    }

    public MenuItem getMsaViewerMenuItem() {
        return msaViewerMenuItem;
    }

    public Tab getResultsTab() {
        return resultsTab;
    }

    public MenuItem getKmerMenuItem() {
        return kmerMenuItem;
    }

    public Tab getConservedTab() {
        return conservedTab;
    }

    public TreeView<String> getConservedTreeView() {
        return conservedTreeView;
    }

    public MenuItem getCheckMSAMenuItem() {
        return checkMSAMenuItem;
    }

    public CheckBox getHeader2CheckBox() {
        return header2CheckBox;
    }

    public Label getClusterCount2Label() {
        return clusterCount2Label;
    }

    public MenuItem getSaveEpitopesMenuItem() {
        return saveEpitopesMenuItem;
    }

    public MenuItem getPDBStructureMenuItem() {
        return PDBStructureMenuItem;
    }

    public Button getProteinViewerButton() {
        return proteinViewerButton;
    }

    public MenuItem getRemoveParalogsMenuItem() {
        return removeParalogsMenuItem;
    }

    public MenuItem getExcludeSpecieMenuItem() {
        return excludeSpecieMenuItem;
    }

    public MenuItem getLocPredMenuItem() {
        return locPredMenuItem;
    }
}
