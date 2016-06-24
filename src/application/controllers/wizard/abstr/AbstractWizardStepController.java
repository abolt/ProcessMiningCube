package application.controllers.wizard.abstr;

import java.io.IOException;

import application.controllers.wizard.CubeWizardController;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.BorderPane;

public abstract class AbstractWizardStepController extends BorderPane implements Initializable {

	protected CubeWizardController mainController;

	public AbstractWizardStepController(CubeWizardController controller, String viewLocation) {
		mainController = controller;

		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource(viewLocation));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	protected void goNext() {
		mainController.nextStep();
	}

	protected void goBack() {
		mainController.backStep();
	}
	
	protected void selectionErrorMessage(String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText(title);
		alert.setContentText(content);
		alert.showAndWait();
	}

}
