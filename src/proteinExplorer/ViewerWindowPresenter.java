package proteinExplorer;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.*;

import proteinExplorer.model.*;

/**
 * The window presenter
 * This is where we communicate between the view and the model
 */
public class ViewerWindowPresenter {

	static PdbComplex complex;

	static MonomerSelectionModel monomerSelectionModel = new MonomerSelectionModel();

	static Map<PdbMonomer, List<Shape3D>> item2shapes = new HashMap<>();

	static UndoRedoManger undoManager = new UndoRedoManger();

	public ViewerWindowPresenter(Stage stage, ViewerWindowController controller, PdbParser model, String sequence) {


		controller.getCloseMenuItem().setOnAction(e -> Platform.exit());

		controller.getAboutMenuItem().setOnAction(e -> showAbout());

		controller.getFullScreenMenuItem().setOnAction(e -> stage.setFullScreen(!stage.fullScreenProperty().get()));

		controller.getClearSelectionMenuItem().setOnAction(e -> monomerSelectionModel.clearSelection());

		controller.getAminoAcidRadioButton().setOnAction(e -> draw(controller, complex));
		controller.getAtomsRadioButton().setOnAction(e -> draw(controller, complex));
		controller.getStructureRadioButton().setOnAction(e -> draw(controller, complex));

		controller.getSavePdbButton().setOnAction(event -> saveFile(stage, controller, model.getContents()));

		controller.getGetPdbButton().setOnAction(event -> {
			String pdbCode = controller.getGetIdTextField().getText();
			if (pdbCode.length() == 4) {
				try {
					downloadPdb(pdbCode, controller, model);
				} catch (IOException e) {
					controller.getInfoLabel().setText("Usage: PDBWebClient [-404]");
				}
			} else {
				controller.getInfoLabel().setText("Invalid PDB ID...");
			}
		});

		controller.getSearchButton().setOnAction(event -> {
			controller.getSearchListView().getItems().clear();
			PDBWebClient client = new PDBWebClient();
			try {
				ArrayList<String> entries = client.searchPdb(sequence,
						controller.getSimilaritySlider().getValue() / 100);
				for (String id : entries) {
					controller.getSearchListView().getItems().add(id);
				}
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		});

		controller.getSearchListView().getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
						try {
							downloadPdb(newValue, controller, model);
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
				}
		);

		monomerSelectionModel.getSelectedItems().addListener((SetChangeListener<? super PdbMonomer>) c -> {
			if (c.wasAdded()) {
				Platform.runLater(() -> {
					if (monomerSelectionModel.getSelectedItems().size() == 1) {
						updateOpacity(flatten(item2shapes), 0.2);
					}
					updateOpacity(item2shapes.get(c.getElementAdded()), 1);
					controller.getInfoTreeView().getSelectionModel().select(
							c.getElementAdded().getIndex() + 2);

					controller.getSequenceListView().getSelectionModel().select(
							c.getElementAdded().getIndex());
				});
			} else if (c.wasRemoved()) {
				Platform.runLater(() -> {
					if (monomerSelectionModel.getSelectedItems().size() > 0) {
						updateOpacity(item2shapes.get(c.getElementRemoved()), 0.2);
						controller.getInfoTreeView().getSelectionModel().clearSelection(
								c.getElementRemoved().getIndex() + 2);

						controller.getSequenceListView().getSelectionModel().clearSelection(
								c.getElementRemoved().getIndex());
					} else {
						updateOpacity(flatten((item2shapes)), 1);
						controller.getInfoTreeView().getSelectionModel().clearSelection();
						controller.getSequenceListView().getSelectionModel().clearSelection();
					}
				});
			}
		});

		controller.getInfoTreeView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		controller.getSequenceListView().getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

		controller.getDarkModeMenuItem().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("DarkMode", (BooleanProperty) v, o, n));
			if (n)
				stage.getScene().getStylesheets().add("modena_dark.css");
			else
				stage.getScene().getStylesheets().remove("modena_dark.css");
		});


		// Undo Redo
		controller.getUndoMenuItem().setOnAction(e -> undoManager.undo());
		controller.getUndoMenuItem().textProperty().bind(undoManager.undoLabelProperty());
		controller.getUndoMenuItem().disableProperty().bind(undoManager.canUndoProperty().not());

		controller.getRedoMenuItem().setOnAction(e -> undoManager.redo());
		controller.getRedoMenuItem().textProperty().bind(undoManager.redoLabelProperty());
		controller.getRedoMenuItem().disableProperty().bind(undoManager.canRedoProperty().not());

		// Monitoring property changes
		controller.getScaleSlider().valueProperty().addListener((v, o, n) ->
				undoManager.add(new PropertyCommand<>("radius", (DoubleProperty) v, o, n)));

		controller.getSticksScaleBar().valueProperty().addListener((v, o, n) ->
				undoManager.add(new PropertyCommand<>("sticks", (DoubleProperty) v, o, n)));

		controller.getRibbonsRadioButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("ribbons", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});

		controller.getBallsCheckButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("balls", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});

		controller.getSticksCheckButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("sticks", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});

		controller.getBallsAndSticksRadioButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("ballsSticks", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});

		controller.getAtomsRadioButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("atoms", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});

		controller.getAminoAcidRadioButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("aminoAcids", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});

		controller.getStructureRadioButton().selectedProperty().addListener((v, o, n) -> {
			undoManager.add(new PropertyCommand<>("Structure", (BooleanProperty) v, o, n));
			draw(controller, complex);
		});
	}

	private static void showAbout() {
		var aboutAlert = new Alert(Alert.AlertType.INFORMATION);
		aboutAlert.setTitle("About");
		aboutAlert.setHeaderText(null);
		aboutAlert.setContentText("ProteomExplorer by: Majd Hammour \n" +
				"Simple PDB viewer \nvisualize proteins using spheres to represent atoms and\n" +
				"cylinders to represent bonds.");
		aboutAlert.showAndWait();
	}

	public static Group showBalls(ViewerWindowController controller, PdbComplex complex) {
		var figure = new Group();
		for (PdbPolymer polymer : complex.getPolymers()) {
			for (PdbMonomer monomer : polymer.getMonomers()) {
				List<Shape3D> shapes = new ArrayList<>();
				for (PdbAtom atom : monomer.getAtoms()) {
					var ball = new Sphere(atom.getRadiusPM());
					ball.setTranslateX(atom.getCoord().getX());
					ball.setTranslateY(atom.getCoord().getY());
					ball.setTranslateZ(atom.getCoord().getZ());

					ball.setMaterial(changeColor(controller, atom, monomer));

					// binding the scale with the slider
					ball.scaleXProperty().bind(controller.getScaleSlider().valueProperty());
					ball.scaleYProperty().bind(controller.getScaleSlider().valueProperty());
					ball.scaleZProperty().bind(controller.getScaleSlider().valueProperty());

					Tooltip.install(ball, new Tooltip(atom.getId() + " " + atom.getRole()));

					ball.setOnMouseClicked(e -> {
						monomerSelectionModel.setSelected(monomer, monomerSelectionModel.select(monomer));
					});
					shapes.add(ball);
					figure.getChildren().add(ball);
				}
				item2shapes.put(monomer, shapes);
			}
		}

		return figure;
	}

	public static Group showSticks(ViewerWindowController controller, PdbComplex complex) {
		var figure = new Group();

		for (PdbPolymer polymer : complex.getPolymers()) {
			for (PdbMonomer monomer : polymer.getMonomers()) {
				for (PdbAtom atom1 : monomer.getAtoms()) {
					for (PdbAtom atom2 : monomer.getAtoms()) {
						if ((atom1.getCoord().distance(atom2.getCoord()) < 2) && atom1 != atom2) {
							var yAxis = new Point3D(0, 100, 0);

							var midPoint = atom1.getCoord().midpoint(atom2.getCoord());
							var direction = atom1.getCoord().subtract(atom2.getCoord());

							var perpendicularAxis = yAxis.crossProduct(direction);
							var angle = yAxis.angle(direction);

							var stick = new Cylinder(0.1, 1);
							stick.setMaterial(new PhongMaterial(Color.BLACK));
							stick.setRotationAxis(perpendicularAxis);
							stick.setRotate(angle);

							stick.setTranslateX(midPoint.getX());
							stick.setTranslateY(midPoint.getY());
							stick.setTranslateZ(midPoint.getZ());

							// binding the scale with the slider
							stick.scaleXProperty().bind(controller.getSticksScaleBar().valueProperty());
							stick.scaleYProperty().bind(controller.getSticksScaleBar().valueProperty());
							stick.scaleZProperty().bind(controller.getSticksScaleBar().valueProperty());
							//stick.radiusProperty().bind(controller.getSticksScaleBar().valueProperty());
							figure.getChildren().add(stick);
						}
					}
				}
			}
		}

		return figure;
	}

	// Drawing the complex to the Pane
	public static void draw(ViewerWindowController controller, PdbComplex complex) {
		controller.getCenterPane().getChildren().clear();
		item2shapes.clear();
		updateSeqInfo(controller.getInfoTreeView(), complex);
		monomerSelectionModel.clearSelection();


		var figure = new Group();

		if (controller.getBallsAndSticksRadioButton().isSelected()) {

			if (controller.getBallsCheckButton().isSelected())
				figure.getChildren().add(showBalls(controller, complex));

			if (controller.getSticksCheckButton().isSelected())
				figure.getChildren().add(showSticks(controller, complex));
		}

		if (controller.getRibbonsRadioButton().isSelected()) {
			figure.getChildren().add(Ribbon.getRibbons(complex));
		}


		var subScene = new SubScene(figure, 600, 600, true,
				SceneAntialiasing.BALANCED);
		subScene.widthProperty().bind(controller.getCenterPane().widthProperty());
		subScene.heightProperty().bind(controller.getCenterPane().heightProperty());

		// setting the camera
		var camera = new PerspectiveCamera(true);
		camera.setFarClip(10000);
		camera.setNearClip(0.01);
		camera.setTranslateZ(-100);

		double meanX = 0;
		double meanY = 0;
		int numberAtoms = 0;
		for (PdbPolymer polymer : complex.getPolymers()) {
			for (PdbMonomer monomer : polymer.getMonomers()) {
				for (PdbAtom atom : monomer.getAtoms()) {
					numberAtoms++;
					meanX += atom.getCoord().getX();
					meanY += atom.getCoord().getY();
				}
			}
		}
		meanX = meanX/numberAtoms;
		meanY = meanY/numberAtoms;

		camera.setTranslateX(meanX);
		camera.setTranslateY(meanY);
		
		// Mouse scroll zoom
		controller.getCenterPane().setOnScroll(e -> {
			camera.setTranslateZ(camera.getTranslateZ() + e.getDeltaY());
		});

		subScene.setCamera(camera);

		controller.getCenterPane().getChildren().add(subScene);

		// enable figure rotation
		var figureTransformProperty = new SimpleObjectProperty<Transform>(new Rotate());
		figureTransformProperty.addListener((v, o, n) -> figure.getTransforms().setAll(n));
		MouseInteraction.installRotate(controller.getCenterPane(), figureTransformProperty, figure, camera);

		// generate the Charts
		generateCharts(controller, complex);
	}

	// helper function to change the color depending on the user selection
	public static PhongMaterial changeColor(ViewerWindowController controller, PdbAtom atom, PdbMonomer monomer) {
		var colors = new PhongMaterial();
		// Atom coloring
		if (controller.getAtomsRadioButton().isSelected()) {
			colors.setDiffuseColor(atom.getColor().darker());
			colors.setSpecularColor(atom.getColor().brighter());
		}

		// Amino acids coloring
		else if (controller.getAminoAcidRadioButton().isSelected()) {
			Random rand = new Random();
			rand.setSeed(monomer.getLabel());

			int r = rand.nextInt(75, 255);
			int g = rand.nextInt(75, 255);
			int b = rand.nextInt(75, 255);

			Color color = Color.rgb(r, g, b);
			colors.setDiffuseColor(color.darker());
			colors.setSpecularColor(color.brighter());
		}

		// secondary structure
		else if (controller.getStructureRadioButton().isSelected()) {
			Color color = Color.GRAY;   // if it is gray there is a problem in the secondary structure

			if (monomer.getGetSecStructure().equals("Helix")) {
				color = Color.RED;
			} else if (monomer.getGetSecStructure().equals("Sheet")) {
				color = Color.YELLOW;
			} else if (monomer.getGetSecStructure().equals("Loop")) {
				color = Color.GREEN;
			}

			colors.setDiffuseColor(color.darker());
			colors.setSpecularColor(color.brighter());
		}
		return colors;
	}

	// Connecting to RCSB API and drawing the complex
	public static void downloadPdb(String code, ViewerWindowController controller, PdbParser model) throws IOException {
		PDBWebClient client = new PDBWebClient();
		String PDB = client.getPdb(code);

		complex = model.read(PDB);

		for (char c : model.getSequence(complex).toCharArray())
			controller.getSequenceListView().getItems().add(c);

		controller.getInfoLabel().setText("Chains: " + model.getChainCount(complex) +
				" Monomers: " + model.getMonomerCount(complex) +
				" Atoms: " + model.getAtomCount(complex));

		draw(controller, complex);

		controller.getPdbTextArea().setText(model.getContents());

		controller.getSavePdbButton().disableProperty().set(false);
	}

	// Saves the pdb file >> can be used to get files from RCSB
	public static void saveFile(Stage stage, ViewerWindowController controller, String content) {
		var FileChooser = new FileChooser();
		FileChooser.setTitle("Save PDB file");
		FileChooser.getExtensionFilters().addAll(
				new FileChooser.ExtensionFilter("PDB file", "*.pdb"),
				new FileChooser.ExtensionFilter("All files", "*.*"));
		var selectedFile = FileChooser.showSaveDialog(stage);
		if (selectedFile != null) {
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter(selectedFile));
				br.write(content);
				br.close();
			} catch (IOException e) {
				controller.getInfoLabel().setText("ERROR: " + e.getMessage());
			}
		}
	}

	// build the tree view of the complex
	public static void updateSeqInfo(TreeView<String> treeView, PdbComplex complex) {
		TreeItem<String> root = new TreeItem<>("Complex");
		int i = 0;
		for (PdbPolymer polymer : complex.getPolymers()) {
			TreeItem<String> chain = new TreeItem<>("CHAIN: " + polymer.getLabel());
			for (PdbMonomer monomer : polymer.getMonomers()) {
				TreeItem<String> aminoAcid = new TreeItem<>(String.valueOf("Monomer: " + monomer.getLabel()));
				for (PdbAtom atom : monomer.getAtoms()) {
					TreeItem<String> a = new TreeItem<>(atom.getName());
					aminoAcid.getChildren().add(a);
				}
				chain.getChildren().add(aminoAcid);
			}
			root.getChildren().add(chain);
		}
		treeView.setRoot(root);
	}

	// helper function for the selection model >> changing the opacity of atoms
	public static <T> void updateOpacity(Collection<? extends Shape3D> list, double opacity) {
		for (var shape : list) {
			var color = ((PhongMaterial) shape.getMaterial()).getDiffuseColor();
			color = new Color(color.getRed(), color.getGreen(), color.getBlue(), opacity);
			((PhongMaterial) shape.getMaterial()).setDiffuseColor(color);
			((PhongMaterial) shape.getMaterial()).setSpecularColor(color.brighter());
		}
	}

	public static List<Shape3D> flatten(Map<PdbMonomer, List<Shape3D>> map) {
		List<Shape3D> list = new ArrayList<>();
		for (List<Shape3D> value : map.values()) {
			list.addAll(value);
		}
		return list;
	}

	// Generates charts for a complex
	public static void generateCharts(ViewerWindowController controller, PdbComplex complex) {
		controller.getPieAminoAcidsPane().getChildren().clear();
		controller.getPieStructurePane().getChildren().clear();

		var stats = new Statistics(complex);

		controller.getAtomsLabel().setText("Atoms: " + stats.getAtoms());
		controller.getChainsLabel().setText("Chains: " + stats.getChains());
		controller.getMonomersLabel().setText("Monomers: " + stats.getMonomers());

		var structureChart = new PieChart();

		structureChart.getData().add(new PieChart.Data("Helices", stats.getHelices()));
		structureChart.getData().add(new PieChart.Data("Sheets", stats.getSheets()));
		structureChart.getData().add(new PieChart.Data("Loops", stats.getLoops()));

		controller.getPieStructurePane().getChildren().add(structureChart);

		var map = stats.getAminoAcids();

		var aminoChart = new PieChart();

		for (char key : map.keySet()) {
			aminoChart.getData().add(new PieChart.Data(String.valueOf(key), map.get(key)));
		}
		controller.getPieAminoAcidsPane().getChildren().add(aminoChart);
	}
}