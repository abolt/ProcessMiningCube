package application.controllers;

import application.controllers.dimensions.DimensionsController;
import application.controllers.importdata.ImportDataController;
import application.controllers.mapping.MappingController;
import application.controllers.mapping.Attribute;
import application.controllers.menu.MenuBarController;
import application.models.eventlog.EventLog;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Tab;

public class MainController {

	// bus
	private EventLog log;
	private ObservableList<Attribute> attributeObjects;

	// menubar
	@FXML
	MenuBar menuBar;
	@FXML
	MenuBarController menuBarController;

	// steps
	@FXML
	Tab tabImportData;
	@FXML
	ImportDataController tabImportDataController;
	@FXML
	Tab tabMapping;
	@FXML
	MappingController tabMappingController;
	@FXML
	Tab tabDimensions;
	@FXML
	DimensionsController tabDimensionsController;

	@FXML
	public void initialize() {
		menuBarController.init(this);

		tabImportDataController.init(this);
		tabImportDataController.initializeTab(tabImportData);

		tabMappingController.init(this);
		tabMappingController.initializeTab(tabMapping);

		tabDimensionsController.init(this);
		tabDimensionsController.initializeTab(tabDimensions);

	}

	public EventLog getLog() {
		return log;
	}

	public void setLog(EventLog log) {
		this.log = log;
	}

	public ObservableList<Attribute> getAttributeObjects() {
		return attributeObjects;
	}

	public void setAttributeObjects(ObservableList<Attribute> attributeObjects) {
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
	}

	public void completeTriggered(String name) {
		switch (name) {
		case "importDataController":
			tabMappingController.setEnabled(true);
			tabMappingController.setCompleted(false);
			tabDimensionsController.setEnabled(false);
			tabDimensionsController.setCompleted(false);
			break;
		case "mappingController":
			tabDimensionsController.setEnabled(true);
			tabDimensionsController.setCompleted(false);
			break;
		}
		updateTabs();
	}
}
