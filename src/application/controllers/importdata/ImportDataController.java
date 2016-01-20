package application.controllers.importdata;

import java.io.File;

import application.controllers.AbstractTabController;
import application.models.eventlog.EventLog;
import application.operations.io.Importer;
import application.operations.io.log.CSVImporter;
import application.operations.io.log.XESImporter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportDataController extends AbstractTabController {

	private Tab tabImportData;

	@FXML
	private TextField fileName, fieldSeparator, valueSeparator;

	@FXML
	private ImageView image;

	/*
	 * Listeners for checking if the separators are valid (only CSV files)
	 */

	@FXML
	public void initialize() {
		name = "importDataController";
		fieldSeparator.textProperty().addListener(new TextListener());
		valueSeparator.textProperty().addListener(new TextListener());

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

		Importer<?> importer = null;
		boolean correct = false;
		if (fileName.getText().endsWith(".csv")) {
			if (checkSeparators()) {
				importer = new CSVImporter(new File(fileName.getText()));
			}
		} else {
			importer = new XESImporter(new File(fileName.getText()));
		}

		if (importer != null && importer.canParse()) {
			mainController.setLog(new EventLog<>(importer.importFromFile()));
			correct = true;
		}
		// here i do the importing.
		setCompleted(correct);
	}

	protected boolean checkSeparators() {
		if (fileName.getText().endsWith(".csv"))
			if (!fieldSeparator.getText().isEmpty()) {
				if (!valueSeparator.getText().isEmpty())
					if (fieldSeparator.getText().compareTo(valueSeparator.getText()) == 0) {
						separatorAlertMessage();
						return false;
					}
			} else {
				separatorAlertMessage();
				return false;
			}
		return true;
	}

	protected void separatorAlertMessage() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("Invalid Separator(s)");
		alert.setContentText("Please check that the field and value separators are valid: "
				+ "they cannot not be equal (the field separator is mandatory)");
		alert.showAndWait();
	}

	protected class TextListener implements ChangeListener<String> {
		@Override
		public void changed(ObservableValue<? extends String> observableValue, String s, String s2) {
			checkSeparators();
		}
	}

	@Override
	protected void enableTab(boolean value) {
		tabImportData.setDisable(!value);
	}

	@Override
	public void initializeTab(Tab input) {
		tabImportData = input;
	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/import_black.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/import_green.png"));
		else
			image.setImage(new Image("images/import_black.png"));
	}
}
