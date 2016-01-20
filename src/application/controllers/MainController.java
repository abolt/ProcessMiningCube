package application.controllers;

import application.controllers.cube.CubeController;
import application.controllers.dimensions.DimensionsController;
import application.controllers.importdata.ImportDataController;
import application.controllers.mapping.MappingController;
import application.controllers.mapping.MappingRow;
import application.controllers.materialize.MaterializeController;
import application.controllers.menu.MenuBarController;
import application.controllers.visualize.VisualizeController;
import application.models.eventlog.CSVFile;
import application.models.eventlog.EventLog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class MainController {

	// bus
	private EventLog log;
	private CSVFile csv;
	private ObservableList<MappingRow> attributeObjects;

	// menubar
	@FXML
	MenuBar menuBar;
	@FXML
	MenuBarController menuBarController;

	// tabbed pane (used for selection)
	@FXML
	TabPane tabbedPane;
	// steps
	@FXML
	Tab tabImportData, tabMapping, tabDimensions, tabCube, tabMaterialize, tabVisualize;
	@FXML
	ImportDataController tabImportDataController;
	@FXML
	MappingController tabMappingController;
	@FXML
	DimensionsController tabDimensionsController;
	@FXML
	CubeController tabCubeController;
	@FXML
	MaterializeController tabMaterializeController;
	@FXML
	VisualizeController tabVisualizeController;

	@FXML
	public void initialize() {
		menuBarController.init(this);

		tabImportDataController.init(this);
		tabImportDataController.initializeTab(tabImportData);

		tabMappingController.init(this);
		tabMappingController.initializeTab(tabMapping);

		tabDimensionsController.init(this);
		tabDimensionsController.initializeTab(tabDimensions);

		tabCubeController.init(this);
		tabCubeController.initializeTab(tabCube);

		tabMaterializeController.init(this);
		tabMaterializeController.initializeTab(tabMaterialize);

		tabVisualizeController.init(this);
		tabVisualizeController.initializeTab(tabVisualize);

	}

	public EventLog getLog() {
		return log;
	}

	public void setLog(EventLog log) {
		this.log = log;
	}

	public ObservableList<MappingRow> getAttributeObjects() {
		return attributeObjects;
	}

	public void setAttributeObjects(ObservableList<MappingRow> attributeObjects) {
		this.attributeObjects = attributeObjects;
	}

	public void newCube() {
		// reset or destroy all elements

		// enable the import tab and disable the rest
		tabImportDataController.setEnabled(true);
		updateTabs();
	}

	public void updateTabs() {
		tabImportDataController.updateImage();
		tabMappingController.updateImage();
		tabDimensionsController.updateImage();
		tabCubeController.updateImage();
		tabMaterializeController.updateImage();
		tabVisualizeController.updateImage();
	}

	public void completeTriggered(String name) {
		switch (name) {
		case "importDataController":
			selectTab(tabMapping);
			tabMappingController.setEnabled(true);
			tabMappingController.setCompleted(false);
			tabDimensionsController.setEnabled(false);
			tabDimensionsController.setCompleted(false);
			tabCubeController.setEnabled(false);
			tabCubeController.setCompleted(false);
			tabMaterializeController.setEnabled(false);
			tabMaterializeController.setCompleted(false);
			tabVisualizeController.setEnabled(false);
			tabVisualizeController.setCompleted(false);
			break;
		case "mappingController":
			selectTab(tabDimensions);
			tabDimensionsController.setEnabled(true);
			tabDimensionsController.setCompleted(false);
			tabCubeController.setEnabled(false);
			tabCubeController.setCompleted(false);
			tabMaterializeController.setEnabled(false);
			tabMaterializeController.setCompleted(false);
			tabVisualizeController.setEnabled(false);
			tabVisualizeController.setCompleted(false);
			break;
		case "dimensionsController":
			selectTab(tabCube);
			tabCubeController.setEnabled(true);
			tabCubeController.setCompleted(false);
			tabMaterializeController.setEnabled(false);
			tabMaterializeController.setCompleted(false);
			tabVisualizeController.setEnabled(false);
			tabVisualizeController.setCompleted(false);
			break;
		case "cubeController":
			selectTab(tabMaterialize);
			tabMaterializeController.setEnabled(true);
			tabMaterializeController.setCompleted(false);
			tabVisualizeController.setEnabled(false);
			tabVisualizeController.setCompleted(false);
			break;
		case "materializeController":
			selectTab(tabVisualize);
			tabVisualizeController.setEnabled(true);
			tabVisualizeController.setCompleted(false);
		case "visualizeController":
			// do nothing, all tabs are enabled
			break;
		}
		updateTabs();
	}

	private void selectTab(Tab input) {
		tabbedPane.getSelectionModel().select(input);
	}
}
