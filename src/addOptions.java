import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

/**
 *  Adding cluster tool options to the UI
 */
public class addOptions {
    public addOptions(WindowController controller) {
        // Cov mode
        ArrayList<String> covMods = new ArrayList<>();
        covMods.add("coverage of query and target");
        covMods.add("coverage of target");
        covMods.add("coverage of query");
        covMods.add("target seq. length has to be at least x% of query length");
        covMods.add("query seq. length has to be at least x% of target length");
        covMods.add("short seq. needs to be at least x% of the other seq. length");

        ObservableList<String> list = FXCollections.observableArrayList(covMods);
        controller.getCovModeChoiceBox().setItems(list);
        controller.getCovModeChoiceBox().setValue("coverage of query and target");

        // Alignment-mode
        ArrayList<String> alnMods = new ArrayList<>();
        alnMods.add("automatic");
        alnMods.add("only score and end_pos");
        alnMods.add("also start_pos and cov");
        alnMods.add("also seq.id");
        alnMods.add("only ungapped alignment");

        ObservableList<String> alnList = FXCollections.observableArrayList(alnMods);
        controller.getAlnModeChoiceBox().setItems(alnList);
        controller.getAlnModeChoiceBox().setValue("automatic");

        // Alignment-output-mode
        ArrayList<String> alnOutMods = new ArrayList<>();
        alnOutMods.add("automatic");
        alnOutMods.add("only score and end_pos");
        alnOutMods.add("also start_pos and cov");
        alnOutMods.add("also seq.id");
        alnOutMods.add("only ungapped alignment");
        alnOutMods.add("score only (output) cluster format");

        ObservableList<String> alnOutList = FXCollections.observableArrayList(alnOutMods);
        controller.getAlnOutputModeChoiceBox().setItems(alnOutList);
        controller.getAlnOutputModeChoiceBox().setValue("automatic");

        //  Seq-id-mode
        ArrayList<String> seqIdMods = new ArrayList<>();
        seqIdMods.add("alignment length");
        seqIdMods.add("shorter sequence");
        seqIdMods.add("longer sequence");

        ObservableList<String> seqIdList = FXCollections.observableArrayList(seqIdMods);
        controller.getSeqIdModeChoiceBox().setItems(seqIdList);
        controller.getSeqIdModeChoiceBox().setValue("alignment length");

        //  Cluster-mode
        ArrayList<String> clusterMods = new ArrayList<>();
        clusterMods.add("Set-Cover (greedy)");
        clusterMods.add("Connected component (BLASTclust)");
        clusterMods.add("Greedy clustering by sequence length (CDHIT)");

        ObservableList<String> clusterList = FXCollections.observableArrayList(clusterMods);
        controller.getClusterModeChoiceBox().setItems(clusterList);
        controller.getClusterModeChoiceBox().setValue("Set-Cover (greedy)");

        // Diamond scoring matrix
        ArrayList<String> matrix = new ArrayList<>();
        matrix.add("BLOSUM45");
        matrix.add("BLOSUM50");
        matrix.add("BLOSUM62");
        matrix.add("BLOSUM80");
        matrix.add("BLOSUM90");
        matrix.add("PAM250");
        matrix.add("PAM70");
        matrix.add("PAM30");

        ObservableList<String> matrixList = FXCollections.observableArrayList(matrix);
        controller.getMatrixChoiceBox().setItems(matrixList);
        controller.getMatrixChoiceBox().setValue("BLOSUM62");

        // binding the checkboxes with the textFields
        controller.getQueryCoverCheckBox().setOnAction(e->{
            controller.getCoverageSlider().setDisable(!controller.getQueryCoverCheckBox().isSelected() &&
                    !controller.getSubjectCoverCheckBox().isSelected());
        });

        controller.getSubjectCoverCheckBox().setOnAction(e->{
            controller.getCoverageSlider().setDisable(!controller.getQueryCoverCheckBox().isSelected() &&
                    !controller.getSubjectCoverCheckBox().isSelected());
        });

        controller.geteValueDCheckBox().setOnAction(e->{
            controller.geteValueDTextField().setDisable(!controller.geteValueDCheckBox().isSelected());
        });

        controller.getGabOpenCheckBox().setOnAction(e->{
            controller.getGabOpenTextField().setDisable(!controller.getGabOpenCheckBox().isSelected());
        });

        controller.getGabExtendCheckBox().setOnAction(e->{
            controller.getGabExtendTextField().setDisable(!controller.getGabExtendCheckBox().isSelected());
        });

        controller.getMatrixCheckBox().setOnAction(e->{
            controller.getMatrixChoiceBox().setDisable(!controller.getMatrixCheckBox().isSelected());
        });

        controller.getThreadsDCheckBox().setOnAction(e->{
            controller.getTreadsDTextField().setDisable(!controller.getThreadsDCheckBox().isSelected());
        });


    }
}
