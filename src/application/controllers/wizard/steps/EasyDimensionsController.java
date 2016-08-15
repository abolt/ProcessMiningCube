package application.controllers.wizard.steps;

import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.wizard.CubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;

public class EasyDimensionsController extends AbstractWizardStepController {

	@FXML
	RadioButton yes;

	private static final String viewLocation = "/application/views/wizard/EasyDimensions.fxml";

	public EasyDimensionsController(CubeWizardController controller) {
		super(controller, viewLocation);
	}

	public boolean isCreationAutomatic() {
		return yes.isSelected();
	}

	@FXML
	protected void backButton() {
		mainController.backStep();
	}

	@FXML
	protected void nextButton() {
		mainController.nextStep();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
