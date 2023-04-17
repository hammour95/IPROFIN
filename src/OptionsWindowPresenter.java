import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.signalpPrediction;
import optionsWindow.OptionsWindowController;

import java.io.IOException;


/**
 * The options window presenter
 * This is where we communicate between the optionsWindow and
 * the main Tool IProFin
 *
 */
public class OptionsWindowPresenter {

    double accuracy;
    int kmerSize;
    int kmerTolerance;
    int MSATolerance;

    public OptionsWindowPresenter(Stage stage, OptionsWindowController optionsWindowController,
                                  WindowPresenter presenter, WindowController controller) {

        bindings(optionsWindowController);
        integerOnly(optionsWindowController.getToleranceTextField());
        integerOnly(optionsWindowController.getKmerToleranceTextField());
        integerOnly(optionsWindowController.getKmerSizeTextField());

        var magic = new magicRun(presenter, controller);

        optionsWindowController.getSearchButton().setOnAction(e->{
            getData(optionsWindowController);

            optionsWindowController.getSettingsPane().setDisable(true);

            // Remove Paralogs
            if(optionsWindowController.getParalogsCheckBox().isSelected()) {
                controller.getRemoveParalogsMenuItem().fire();
            }

            // Optimize ID and Cov
            if(optionsWindowController.getIdAndCovCheckBox().isSelected()) {
                try {
                    magic.findOptimalParameters(accuracy);
                    controller.getStartButton().fire();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }

            // do the clustering
            controller.getStartButton().fire();

            // Exclude Cluster that not share at least 1 kmer
            if(optionsWindowController.getKmerFilterCheckBox().isSelected()) {
                magic.kmerSearch(kmerSize, kmerTolerance);
            }

            // make localization prediction
            if(optionsWindowController.getLocPredCheckBox().isSelected()) {
                presenter.locPred(controller);
                localizationFilter(optionsWindowController, controller);
            }

            // Predict Signals with SignalP6
            if(optionsWindowController.getClusterPredictCheckBox().isSelected()) {
                presenter.saveClustSignal(controller);

                var signalP = new signalpPrediction();
                optionsWindowController.getOptionsProgressBar().visibleProperty().bind(signalP.runningProperty());
                optionsWindowController.getOptionsProgressBar().progressProperty().bind(signalP.progressProperty());
                signalP.setOnSucceeded(s-> {
                    try {
                        controller.getSignalPButton().setDisable(false);
                        presenter.signalPResults(controller);
                        presenter.signalPUpdateTree(controller);

                        // REMOVE UNWANTED SIGNALS
                        signalFilter(optionsWindowController, controller);

                        // CHECK MSA
                        if(optionsWindowController.getCheckMSACheckBox().isSelected()) {
                            magic.checkMSA(MSATolerance);
                        }

                        //TODO: Confirm With DeepTMHMM

                        optionsWindowController.getSettingsPane().setDisable(false);
                        stage.close();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                signalP.start();
            }
            if (!optionsWindowController.getClusterPredictCheckBox().isSelected()) {
                if(optionsWindowController.getCheckMSACheckBox().isSelected()) {
                    try {
                        magic.checkMSA(MSATolerance);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
                optionsWindowController.getSettingsPane().setDisable(false);
                stage.close();
            }
        });
    }

    /**
     * This class binds the disabled properties of the settings with the selection of used method
     * @param optionsWindowController: The Controller class of the options Menu
     */
    private void bindings(OptionsWindowController optionsWindowController) {
        // ID and COV
        optionsWindowController.getHighSensitiveRadioButton().disableProperty().bind(
                optionsWindowController.getIdAndCovCheckBox().selectedProperty().not()
        );
        optionsWindowController.getMediumSensitiveRadioButton().disableProperty().bind(
                optionsWindowController.getIdAndCovCheckBox().selectedProperty().not()
        );
        optionsWindowController.getLowSensitiveRadioButton().disableProperty().bind(
                optionsWindowController.getIdAndCovCheckBox().selectedProperty().not()
        );

        // Kmer Check
        optionsWindowController.getKmerSizeTextField().disableProperty().bind(
                optionsWindowController.getKmerFilterCheckBox().selectedProperty().not()
        );
        optionsWindowController.getKmerToleranceTextField().disableProperty().bind(
                optionsWindowController.getKmerFilterCheckBox().selectedProperty().not()
        );


        // MSA
        optionsWindowController.getToleranceTextField().disableProperty().bind(
                optionsWindowController.getCheckMSACheckBox().selectedProperty().not()
        );

        // Signal Filter
        optionsWindowController.getSPCheckBox().disableProperty().bind(
                optionsWindowController.getSignalFilterCheckBox().selectedProperty().not()
        );
        optionsWindowController.getLIPOCheckBox().disableProperty().bind(
                optionsWindowController.getSignalFilterCheckBox().selectedProperty().not()
        );
        optionsWindowController.getTATCheckBox().disableProperty().bind(
                optionsWindowController.getSignalFilterCheckBox().selectedProperty().not()
        );
        optionsWindowController.getTATLIPOCheckBox().disableProperty().bind(
                optionsWindowController.getSignalFilterCheckBox().selectedProperty().not()
        );
        optionsWindowController.getPILINCheckBox().disableProperty().bind(
                optionsWindowController.getSignalFilterCheckBox().selectedProperty().not()
        );
        optionsWindowController.getOtherCheckBox().disableProperty().bind(
                optionsWindowController.getSignalFilterCheckBox().selectedProperty().not()
        );

        optionsWindowController.getSignalFilterCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );


        // Signal With main checkbox
        optionsWindowController.getSPCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );
        optionsWindowController.getLIPOCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );
        optionsWindowController.getTATCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );
        optionsWindowController.getTATLIPOCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );
        optionsWindowController.getPILINCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );
        optionsWindowController.getOtherCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );



        optionsWindowController.getSignalFilterCheckBox().disableProperty().bind(
                optionsWindowController.getClusterPredictCheckBox().selectedProperty().not()
        );


        optionsWindowController.getConfirmDeepTMCheckBox().disableProperty().bind(
                optionsWindowController.getCheckMSACheckBox().selectedProperty().not()
        );

        optionsWindowController.getLocalizationFilterCheckBox().disableProperty().bind(
                optionsWindowController.getLocPredCheckBox().selectedProperty().not()
        );

        optionsWindowController.getCytoCheckBox().disableProperty().bind(
                optionsWindowController.getLocPredCheckBox().selectedProperty().not()
        );

        optionsWindowController.getCytoMemCheckBox().disableProperty().bind(
                optionsWindowController.getLocPredCheckBox().selectedProperty().not()
        );

        optionsWindowController.getPeriCheckBox().disableProperty().bind(
                optionsWindowController.getLocPredCheckBox().selectedProperty().not()
        );

        optionsWindowController.getOuterCheckBox().disableProperty().bind(
                optionsWindowController.getLocPredCheckBox().selectedProperty().not()
        );

        optionsWindowController.getExtraCheckBox().disableProperty().bind(
                optionsWindowController.getLocPredCheckBox().selectedProperty().not()
        );
    }

    /**
     * Allows only integer input in the text field
     * @param field: JavaFX TextField
     */
    private void integerOnly(TextField field) {
        field.textProperty().addListener(
                new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        if(!newValue.matches("\\d*")) {
                            field.setText(newValue.replaceAll("[^\\d]", ""));
                        }
                    }
                }
        );
    }

    /**
     * Getting the data from the setting UI
     * @param optionsWindowController: The settings controller class
     */
    private void getData(OptionsWindowController optionsWindowController) {
        // accuracy
        if(optionsWindowController.getHighSensitiveRadioButton().isSelected()) {
            accuracy = 0.025;
        } else if (optionsWindowController.getMediumSensitiveRadioButton().isSelected()) {
            accuracy = 0.05;
        } else if (optionsWindowController.getLowSensitiveRadioButton().isSelected()) {
            accuracy = 0.1;
        }

        // kmer Size
        if(!optionsWindowController.getKmerSizeTextField().getText().isEmpty()) {
            kmerSize = Integer.parseInt(optionsWindowController.getKmerSizeTextField().getText());
        }

        // kmer Tolerance
        if(!optionsWindowController.getKmerToleranceTextField().getText().isEmpty()) {
            kmerTolerance = Integer.parseInt(optionsWindowController.getKmerToleranceTextField().getText());
        }

        // MSA Tolerance
        if(!optionsWindowController.getToleranceTextField().getText().isEmpty()) {
            MSATolerance = Integer.parseInt(optionsWindowController.getToleranceTextField().getText());
        }
    }

    /**
     * remove unwanted Signals from the results
     * @param optionsWindowController: the controller of the settings
     * @param controller: The main Controller
     */
    private void signalFilter(OptionsWindowController optionsWindowController, WindowController controller) {
        if(optionsWindowController.getSignalFilterCheckBox().isSelected()) {
            if(!optionsWindowController.getOtherCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("SignalP6= OTHER ");
                controller.getRemoveButton().fire();
            }

            if(!optionsWindowController.getPILINCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("SignalP6= PILIN ");
                controller.getRemoveButton().fire();
            }

            if(!optionsWindowController.getTATCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("SignalP6= TAT ");
                controller.getRemoveButton().fire();
            }

            if(!optionsWindowController.getTATLIPOCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("SignalP6= TATLIPO ");
                controller.getRemoveButton().fire();
            }

            if(!optionsWindowController.getSPCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("SignalP6= SP ");
                controller.getRemoveButton().fire();
            }

            if(!optionsWindowController.getLIPOCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("SignalP6= LIPO ");
                controller.getRemoveButton().fire();
            }
            controller.getRemoveTextField().setText("");
        }
    }

    /**
     * remove unwanted protein localizations from the results
     * @param optionsWindowController: the controller of the settings
     * @param controller: The main Controller
     */
    private void localizationFilter(OptionsWindowController optionsWindowController, WindowController controller) {
        if (optionsWindowController.getLocalizationFilterCheckBox().isSelected()) {
            if (!optionsWindowController.getCytoCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("[Localization: cytoplasmic]");
                controller.getRemoveButton().fire();
            }

            if (!optionsWindowController.getCytoMemCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("[Localization: cytoplasmicmembrane]");
                controller.getRemoveButton().fire();
            }

            if (!optionsWindowController.getPeriCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("[Localization: periplasmic]");
                controller.getRemoveButton().fire();
            }

            if (!optionsWindowController.getOuterCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("[Localization: outermembrane]");
                controller.getRemoveButton().fire();
            }

            if (!optionsWindowController.getExtraCheckBox().isSelected()) {
                controller.getRemoveTextField().setText("[Localization: extracellular]");
                controller.getRemoveButton().fire();
            }

            controller.getRemoveTextField().setText("");
        }
    }
}
