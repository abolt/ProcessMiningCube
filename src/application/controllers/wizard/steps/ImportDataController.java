package application.controllers.wizard.steps;

import java.io.File;

import application.controllers.AbstractTabController;
import application.controllers.wizard.NewCubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import application.operations.io.Importer;
import application.operations.io.log.CSVImporter;
import application.operations.io.log.XESImporter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportDataController extends AbstractWizardStepController {

	

	@FXML
	private BorderPane importData;

	@FXML
	private TextField fileName;

	/*
	 * Listeners for checking if the separators are valid (only CSV files)
	 */

	@FXML
	public void initialize() {
		//name = "importDataController";
	}
	
	public ImportDataController(NewCubeWizardController controller) {
		super(controller);
	}

	@FXML
	protected void openFileSelectorButton(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		fileChooser.getExtensionFilters()
				.add(new ExtensionFilter("XES or CSV files supported", "*.xes", "*.xes.gz", "*.csv"));
		File selectedFile = fileChooser.showOpenDialog(null);
		if (selectedFile != null) {
			fileName.setText(selectedFile.getPath());
		}
	}

	@FXML
	protected void handleImportDataButton(ActionEvent event) {

		Importer importer = null;
		boolean correct = false;
		if (fileName.getText().endsWith(".csv")) {
			importer = new CSVImporter(new File(fileName.getText()));
		} else {
			importer = new XESImporter(new File(fileName.getText()));
		}

//		if (importer != null && importer.canParse()) {
//			mainController.setImporter(importer);
//			mainController.setMappingRows(importer.getSampleList());
//			correct = true;
//		}
		//setCompleted(correct);
	}

}
