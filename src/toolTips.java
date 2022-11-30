import javafx.scene.control.Tooltip;

/**
 * Adding tool tips "Hints" to the buttons in the UI
 */
public class toolTips {

    public toolTips(WindowController controller) {
        Tooltip.install(controller.getOpenButton(), new Tooltip("Open species fastA folder"));

        Tooltip.install(controller.getClusterButton(), new Tooltip("MMseqs Cluster, slower but more sensitive"));

        Tooltip.install(controller.getLinclustButton(), new Tooltip("MMseqs linClust, faster clustering"));

        Tooltip.install(controller.getDiamondButton(), new Tooltip("Clustering using DIAMOND"));

        Tooltip.install(controller.getSaveButton(), new Tooltip("Save clusters representative proteins > fastA"));

        Tooltip.install(controller.getSaveClustersButton(), new Tooltip("Save every cluster > fastA"));

        Tooltip.install(controller.getStartButton(), new Tooltip("Start Clustering"));

        Tooltip.install(controller.geteValueTextField(), new Tooltip(
                "List matches below this E-value (range 0.0-inf)"));

        Tooltip.install(controller.getMinAlnLenTextField(), new Tooltip(
                "Minimum alignment length (range 0-INT_MAX)"));


    }
}
