import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.application.Application;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class paramsicherung extends Application {

    ArrayList<Integer> idClusters = new ArrayList<>();
    ArrayList<Integer> idProducts = new ArrayList<>();
    ArrayList<Double> idValue = new ArrayList<>();
    ArrayList<Integer> covClusters = new ArrayList<>();
    ArrayList<Integer> covProducts = new ArrayList<>();
    ArrayList<Double> covValue = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Parameter Test");

        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel("Identity");
        yAxis.setLabel("Clusters");

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);

        lineChart.setTitle("Identity Value Check");

        XYChart.Series<Number, Number> series1 = new XYChart.Series<>();
        series1.setName("number of clusters");

        for (int i = 0; i < idValue.size(); i++) {
            series1.getData().add(new XYChart.Data<Number, Number>(idValue.get(i), idClusters.get(i)));
        }

        XYChart.Series<Number, Number> series2 = new XYChart.Series<>();
        series2.setName("number of clusters with one product");

        for (int i = 0; i < idValue.size(); i++) {
            series2.getData().add(new XYChart.Data<Number, Number>(idValue.get(i), idProducts.get(i)));
        }


        NumberAxis x2Axis = new NumberAxis();
        NumberAxis y2Axis = new NumberAxis();

        x2Axis.setLabel("Coverage");
        y2Axis.setLabel("Clusters");

        LineChart<Number, Number> lineChart2 = new LineChart<>(x2Axis, y2Axis);

        lineChart2.setTitle("Coverage Value Check");

        XYChart.Series<Number, Number> series3 = new XYChart.Series<>();
        series3.setName("number of clusters");

        for (int i = 0; i < covValue.size(); i++) {
            series3.getData().add(new XYChart.Data<Number, Number>(covValue.get(i), covClusters.get(i)));
        }

        XYChart.Series<Number, Number> series4 = new XYChart.Series<>();
        series4.setName("number of clusters with one product");

        for (int i = 0; i < covValue.size(); i++) {
            series4.getData().add(new XYChart.Data<Number, Number>(covValue.get(i), covProducts.get(i)));
        }


        lineChart.getData().addAll(series1, series2);
        lineChart2.getData().addAll(series3, series4);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(lineChart, lineChart2);

        Scene scene = new Scene(hBox, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * The identity parameter will be searched and the coverage will be set on 50% || Mode 0
     * Then, the coverage parameter will be searched and the id will be set on 50% || Mode 1
     */
    public void autoSearch(WindowPresenter presenter, WindowController controller) throws IOException {
        // first the id
        checkParams(presenter, controller, 0);
        // then the cov
        checkParams(presenter, controller, 1);
    }

    /**
     * checking the parameters ID and COV
     *
     * @param mode 0 => to check ID OR 2 => to check COV
     */
    private void checkParams(WindowPresenter presenter, WindowController controller, int mode) throws IOException {
        if (mode == 0) {
            controller.getCoverageSlider().setValue(0);
        } else if (mode == 1) {
            controller.getMinSeqIdSlider().setValue(0);
        }

        var input = presenter.getInput();
        var spec = presenter.getSpec();
        var method = presenter.getMethod();
        var RegName = presenter.getRegName();

        if (!input.isEmpty()) {
            for (double i = 0; i <= 1; i = i + 0.1) {
                if (mode == 0) {
                    controller.getMinSeqIdSlider().setValue(i);
                } else if (mode == 1) {
                    controller.getCoverageSlider().setValue(i);
                }

                new startClustering(controller, presenter, spec, method, input);
                presenter.summary(controller, spec);

                var root = controller.getResultsTextArea().getRoot();
                var children = root.getChildren();

                Map<String, Integer> products = new HashMap<>();
                for (var ch : children) {
                    if (ch.getValue().split(RegName).length == 2) {
                        String name = ch.getValue().split(RegName)[1].split("]")[0];
                        products.put(name, 1);

                        for (var ch2 : ch.getChildren()) {
                            if (ch2.getValue().split(RegName).length == 2) {
                                String name2 = ch2.getValue().split(RegName)[1].split("]")[0];
                                if (!name.equals(name2)) {
                                    products.replace(name, products.get(name) + 1);
                                }
                            }
                        }
                    }
                }
                int counter = 0;
                for (var key : products.keySet()) {
                    if (products.get(key) < 4) {
                        counter += 1;
                    }
                }

                if (mode == 0) {
                    idValue.add(i);
                    idClusters.add(children.size());
                    idProducts.add(counter);
                } else if (mode == 1) {
                    covValue.add(i);
                    covClusters.add(children.size());
                    covProducts.add(counter);
                }
            }
        }
    }
}
