package application.controllers.wizard.steps;

import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.wizard.CubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class MappingController extends AbstractWizardStepController {

	private static final String viewLocation = "/application/views/wizard/Mapping.fxml";
	
	@FXML
	private TableView table;
	
	@FXML
	private TableColumn colAttribute, colExampleValues, colAttributeType, colCreateDimension;
	
	
	public MappingController(CubeWizardController controller) {
		super(controller, viewLocation);
		
		
	}

	
	
	
	
	
	
	
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub
		
	}

}
