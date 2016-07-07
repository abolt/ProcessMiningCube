package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;

import application.controllers.mainview.CubeRepositoryController;
import application.controllers.menu.MenuBarController;
import application.controllers.wizard.CubeWizardController;
import application.models.cube.Cube;
import application.models.cube.CubeStructure;
import application.models.dimension.Attribute;
import application.models.dimension.Dimension;
import application.models.wizard.MappingRow;
import application.operations.io.Importer;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainController extends BorderPane implements Initializable {

	private Stage mainStage;
	private CubeRepositoryController cubeRepositoryController;

	// menubar
	@FXML
	MenuBar menuBar;
	@FXML
	MenuBarController menuBarController;

	public MainController(Stage stage) {
		mainStage = stage;

		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/application/views/MainView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		initialize();
	}

	/**
	 * Old stuff
	 */

	// bus
	private Importer importer;
	private XLog log;
	private ObservableList<MappingRow> mappingRows;
	private ObservableList<Dimension> dimensions;
	private Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>> selectedValues;
	private String distributionModel;
	private Cube cube;

	// // tabbed pane (used for selection)
	// @FXML
	// TabPane tabbedPane;
	// // steps
	// @FXML
	// Tab tabImportData, tabMapping, tabDimensions, tabCube, tabMaterialize,
	// tabVisualize;
	// @FXML
	// ImportDataController tabImportDataController;
	// @FXML
	// MappingController tabMappingController;
	// @FXML
	// DimensionsController tabDimensionsController;
	// @FXML
	// CubeController tabCubeController;
	// @FXML
	// MaterializeController tabMaterializeController;
	// @FXML
	// VisualizeController tabVisualizeController;

	@FXML
	public void initialize() {
		menuBarController.init(this);
		cubeRepositoryController = new CubeRepositoryController(this);
		this.setCenter(cubeRepositoryController);

		// tabImportDataController.init(this);
		// tabImportDataController.initializeTab(tabImportData);
		//
		// tabMappingController.init(this);
		// tabMappingController.initializeTab(tabMapping);
		//
		// tabDimensionsController.init(this);
		// tabDimensionsController.initializeTab(tabDimensions);
		//
		// tabCubeController.init(this);
		// tabCubeController.initializeTab(tabCube);
		//
		// tabMaterializeController.init(this);
		// tabMaterializeController.initializeTab(tabMaterialize);
		//
		// tabVisualizeController.init(this);
		// tabVisualizeController.initializeTab(tabVisualize);

	}

	// public Cube getCube() {
	// return cube;
	// }
	//
	// public void setCube(Cube cube) {
	// this.cube = cube;
	// }
	//
	// public String getDistributionModel() {
	// return distributionModel;
	// }
	//
	// public void setDistributionModel(String distributionModel) {
	// this.distributionModel = distributionModel;
	// }
	//
	// public Importer getImporter() {
	// return importer;
	// }
	//
	// public void setImporter(Importer importer) {
	// this.importer = importer;
	// }
	//
	// public XLog getLog() {
	// return log;
	// }
	//
	// public void setLog(XLog log) {
	// this.log = log;
	// }
	//
	// public ObservableList<MappingRow> getMappingRows() {
	// return mappingRows;
	// }
	//
	public void setMappingRows(ObservableList<MappingRow> attributeObjects) {
		this.mappingRows = attributeObjects;
	}

	//
	// public void setDimensions(ObservableList<Dimension> dim) {
	// this.dimensions = dim;
	// }
	//
	// public ObservableList<Dimension> getDimensions() {
	// return this.dimensions;
	// }
	//
	// public Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>>
	// getSelectedValues() {
	// return selectedValues;
	// }
	//
	// public void setSelectedValues(Map<Attribute,
	// ObservableList<CheckBoxTreeItem<XAttribute>>> selectedValues) {
	// this.selectedValues = selectedValues;
	// }
	//
	public void newCube() {

		final Stage wizard = new Stage();
		// wizard.initModality(Modality.APPLICATION_MODAL);
		wizard.initOwner(mainStage);
		CubeWizardController cubeController = new CubeWizardController();
		Scene dialogScene = new Scene(cubeController, 800, 600);
		wizard.setScene(dialogScene);
		wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
		wizard.setTitle("New Cube Wizard");
		wizard.showAndWait();
		cubeRepositoryController.addCube(new Cube(cubeController.getCubeStructure(), cubeController.getEventBase()));

		cubeRepositoryController.updateRepositoryList();
		// CubeStructure cube = cubeController.getCubeStructure();
		// for(Dimension dim : cube.getDimensions())
		// System.out.println("Dim: " + dim.getNameProperty().getValue());
		//
	}
	//
	// public void updateTabs() {
	// tabImportDataController.updateImage();
	// tabMappingController.updateImage();
	// tabDimensionsController.updateImage();
	// tabCubeController.updateImage();
	// tabMaterializeController.updateImage();
	// tabVisualizeController.updateImage();
	// }
	//
	// public void completeTriggered(String name) {
	// switch (name) {
	// case "importDataController":
	// selectTab(tabMapping);
	// tabMappingController.setEnabled(true);
	// tabMappingController.setCompleted(false);
	// tabDimensionsController.setEnabled(false);
	// tabDimensionsController.setCompleted(false);
	// tabCubeController.setEnabled(false);
	// tabCubeController.setCompleted(false);
	// tabMaterializeController.setEnabled(false);
	// tabMaterializeController.setCompleted(false);
	// tabVisualizeController.setEnabled(false);
	// tabVisualizeController.setCompleted(false);
	// break;
	// case "mappingController":
	// selectTab(tabDimensions);
	// tabDimensionsController.setEnabled(true);
	// tabDimensionsController.setCompleted(false);
	// tabCubeController.setEnabled(false);
	// tabCubeController.setCompleted(false);
	// tabMaterializeController.setEnabled(false);
	// tabMaterializeController.setCompleted(false);
	// tabVisualizeController.setEnabled(false);
	// tabVisualizeController.setCompleted(false);
	// break;
	// case "dimensionsController":
	// selectTab(tabCube);
	// tabCubeController.setEnabled(true);
	// tabCubeController.setCompleted(false);
	// tabMaterializeController.setEnabled(false);
	// tabMaterializeController.setCompleted(false);
	// tabVisualizeController.setEnabled(false);
	// tabVisualizeController.setCompleted(false);
	// break;
	// case "cubeController":
	// selectTab(tabMaterialize);
	// tabMaterializeController.setEnabled(true);
	// tabMaterializeController.setCompleted(false);
	// tabVisualizeController.setEnabled(false);
	// tabVisualizeController.setCompleted(false);
	// break;
	// case "materializeController":
	// selectTab(tabVisualize);
	// tabVisualizeController.setEnabled(true);
	// tabVisualizeController.setCompleted(false);
	// case "visualizeController":
	// // do nothing, all tabs are enabled
	// break;
	// }
	// updateTabs();
	// }
	//
	// private void selectTab(Tab input) {
	// tabbedPane.getSelectionModel().select(input);
	// }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
