package optionsWindow;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import org.jmol.util.C;

public class OptionsWindowController {

    @FXML
    private CheckBox checkMSACheckBox;

    @FXML
    private CheckBox clusterPredictCheckBox;

    @FXML
    private CheckBox confirmDeepTMCheckBox;

    @FXML
    private RadioButton highSensitiveRadioButton;

    @FXML
    private CheckBox idAndCovCheckBox;

    @FXML
    private CheckBox kmerFilterCheckBox;

    @FXML
    private TextField kmerSizeTextField;

    @FXML
    private RadioButton lowSensitiveRadioButton;

    @FXML
    private RadioButton mediumSensitiveRadioButton;

    @FXML
    private ProgressBar optionsProgressBar;

    @FXML
    private Button searchButton;

    @FXML
    private ToggleGroup sensitive;

    @FXML
    private TextField toleranceTextField;

    @FXML
    private TextField kmerToleranceTextField;

    @FXML
    private CheckBox signalFilterCheckBox;

    @FXML
    private CheckBox SPCheckBox;

    @FXML
    private CheckBox LIPOCheckBox;

    @FXML
    private CheckBox TATCheckBox;

    @FXML
    private CheckBox TATLIPOCheckBox;

    @FXML
    private CheckBox PILINCheckBox;

    @FXML
    private CheckBox otherCheckBox;

    @FXML
    private AnchorPane SettingsPane;

    public CheckBox getCheckMSACheckBox() {
        return checkMSACheckBox;
    }

    public CheckBox getClusterPredictCheckBox() {
        return clusterPredictCheckBox;
    }

    public CheckBox getConfirmDeepTMCheckBox() {
        return confirmDeepTMCheckBox;
    }

    public RadioButton getHighSensitiveRadioButton() {
        return highSensitiveRadioButton;
    }

    public CheckBox getIdAndCovCheckBox() {
        return idAndCovCheckBox;
    }

    public CheckBox getKmerFilterCheckBox() {
        return kmerFilterCheckBox;
    }

    public TextField getKmerSizeTextField() {
        return kmerSizeTextField;
    }

    public RadioButton getLowSensitiveRadioButton() {
        return lowSensitiveRadioButton;
    }

    public RadioButton getMediumSensitiveRadioButton() {
        return mediumSensitiveRadioButton;
    }

    public ProgressBar getOptionsProgressBar() {
        return optionsProgressBar;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public ToggleGroup getSensitive() {
        return sensitive;
    }

    public TextField getToleranceTextField() {
        return toleranceTextField;
    }

    public TextField getKmerToleranceTextField() {
        return kmerToleranceTextField;
    }

    public CheckBox getSignalFilterCheckBox() {
        return signalFilterCheckBox;
    }

    public CheckBox getSPCheckBox() {
        return SPCheckBox;
    }

    public CheckBox getLIPOCheckBox() {
        return LIPOCheckBox;
    }

    public CheckBox getTATCheckBox() {
        return TATCheckBox;
    }

    public CheckBox getTATLIPOCheckBox() {
        return TATLIPOCheckBox;
    }

    public CheckBox getPILINCheckBox() {
        return PILINCheckBox;
    }

    public CheckBox getOtherCheckBox() {
        return otherCheckBox;
    }

    public AnchorPane getSettingsPane() {
        return SettingsPane;
    }
}
