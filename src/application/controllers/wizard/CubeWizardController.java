package application.controllers.wizard;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import application.controllers.wizard.abstr.AbstractWizardStepController;
import application.controllers.wizard.steps.CubeOverViewController;
import application.controllers.wizard.steps.DimensionsController;
import application.controllers.wizard.steps.ImportDataController;
import application.controllers.wizard.steps.MappingController;
import application.models.cube.Cube;
import application.models.cube.CubeStructure;
import application.models.dimension.Dimension;
import application.models.eventbase.AbstrEventBase;
import application.models.eventbase.FileBasedEventBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class CubeWizardController extends BorderPane implements Initializable {

	private CubeStructure cube;
	private AbstrEventBase eventBase;
	
	@FXML
	private ImageView imageStep0, imageStep1, imageStep2, imageStep3;
	private List<ImageView> stepImageList;

	private List<AbstractWizardStepController> steps;

	private int stepNum;

	public CubeWizardController() {
		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/wizard/CubeWizard.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		initializeContent();
	}

	public void initializeContent() {
		stepImageList = new ArrayList<ImageView>();
		stepImageList.add(imageStep0);
		stepImageList.add(imageStep1);
		stepImageList.add(imageStep2);
		stepImageList.add(imageStep3);

		steps = new ArrayList<AbstractWizardStepController>();

		stepNum = 0;
		updateContent();
	}

	public AbstractWizardStepController getNode(int number) {
		if (number >= 0  && number < steps.size())
			if (steps.get(number) != null)
				return steps.get(number);
		return null;
	}

	public void nextStep() {
		if (stepNum < 3)
			stepNum++;
		updateContent();
	}

	public void backStep() {
		if (stepNum > 0)
			stepNum--;
		updateContent();
	}

	private void updateContent() {
		// assumes that the step has been created before
		if (getNode(stepNum) == null)
			createStep(stepNum);

		this.setCenter((Node)getNode(stepNum));

		for (int i = 0; i < stepImageList.size(); i++) {
			if (i == stepNum) {
				int depth = 40;
				DropShadow borderGlow = new DropShadow();
				borderGlow.setOffsetY(0.1f);
				borderGlow.setOffsetX(0.1f);
				borderGlow.setColor(Color.GREENYELLOW);
				borderGlow.setWidth(depth);
				borderGlow.setHeight(depth);

				stepImageList.get(i).setEffect(borderGlow);
			} else
				stepImageList.get(i).setEffect(null);
		}
	}

	private void createStep(int number) {
		switch (number) {
		case 0: // import data
			steps.add(new ImportDataController(this));
			break;
		case 1: // mappings
			steps.add(new MappingController(this));
			break;
		case 2: // dimensions
			steps.add(new DimensionsController(this));
			break;
		case 3: // cube overview
			steps.add(new CubeOverViewController(this));
			break;
		}
	}

	public void createCube(){
		
		TextInputDialog dialog = new TextInputDialog("Hooray!");
		dialog.setTitle("Name your cube!");
		dialog.setHeaderText("Please enter the name of your new Cube.");
		dialog.showAndWait();

		createEventBase(dialog.getResult());
		createCubeStructure();
		
	}
	private void createEventBase(String name) {
	
			eventBase = new FileBasedEventBase(((ImportDataController)steps.get(0)).getFileName(), name);
		
		
	}

	public void createCubeStructure() {
		// create the cube object, and make it available for the visualizer.
		
		cube = new CubeStructure(((DimensionsController)steps.get(2)).getDimensions(),eventBase);
		
		this.getScene().getWindow().hide();
	}
	
	public CubeStructure getCubeStructure(){
		return cube;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
