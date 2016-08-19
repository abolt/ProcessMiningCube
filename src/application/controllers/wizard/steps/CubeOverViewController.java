package application.controllers.wizard.steps;

import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.wizard.CubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import application.models.attribute.abstr.Attribute;
import application.models.dimension.DimensionImpl;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class CubeOverViewController extends AbstractWizardStepController {

	private static final String viewLocation = "/application/views/wizard/CubeOverview.fxml";
	private ObservableList<DimensionImpl> dimensions;

	@FXML
	private TextArea textArea;

	public CubeOverViewController(CubeWizardController controller) {
		super(controller, viewLocation);
		dimensions = ((DimensionsController) controller.getNode(3)).getDimensions();
		initializeComponent();
	}

	private void initializeComponent() {
		String summary = "Cube Summary:\n";
		for (DimensionImpl dim : dimensions) {
			summary = summary.concat("\nDimension: " + dim.getNameProperty().getValue() + "\n");
			int attCounter = 1;
			for (Attribute<?> att : dim.getAttributes()) {
				summary = summary
						.concat("\tAttribute " + attCounter + ": " + att.getLabel() + "\t(" + att.getType() + ")\n");
				attCounter++;
			}
		}
		textArea.setText(summary);
		textArea.setEditable(false);
	}

	@FXML
	protected void backButton() {
		mainController.backStep();
	}

	@FXML
	protected void nextButton() {
		mainController.createCube();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
