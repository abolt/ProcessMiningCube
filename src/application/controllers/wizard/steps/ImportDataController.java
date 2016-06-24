package application.controllers.wizard.steps;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.wizard.CubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ImportDataController extends AbstractWizardStepController {

	private static final String viewLocation = "/application/views/wizard/ImportData.fxml";
	private File input;
	
	public static final String CSV = "CSV", XES = "XES";
	public String extension;

	@FXML
	private TextField fileName;

	public ImportDataController(CubeWizardController controller) {
		super(controller, viewLocation);
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

		input = null;
		if (fileName.getText().isEmpty())
			selectionErrorMessage("No input data specified!", "Please specify a valid data file (.xes, .xes.gz, .csv)");
		else if ((fileName.getText().endsWith(".csv") || fileName.getText().endsWith(".xes")
				|| fileName.getText().endsWith(".xes.gz"))) {
			
			input = new File(fileName.getText());
			if(fileName.getText().endsWith(".csv"))
				extension = CSV;
			else
				extension = XES;
		} else

			selectionErrorMessage("Wrong Format",
					"The selected file does not comply with the supported data formats (i.e., .xes, .xes.gz, .csv)");

		if (input != null)
			// the file is correct. next step will handle the
			goNext();
	}
	
	public File getFile(){
		return input;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
