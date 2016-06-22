package application.controllers.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import application.controllers.wizard.steps.ImportDataController;
import application.models.cube.Cube;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class NewCubeWizardController {

	@FXML
	private BorderPane wizardBorderPane;

	@FXML
	private ImageView step0, step1, step2, step3;
	private List<ImageView> stepImageList;

	private List<BorderPane> wizardSteps;

	private int stepNum;

	public NewCubeWizardController() {
		stepImageList = new ArrayList<ImageView>();
		stepImageList.add(step0);
		stepImageList.add(step1);
		stepImageList.add(step2);
		stepImageList.add(step3);

		wizardSteps = new ArrayList<BorderPane>();
		stepNum = 0;
	}

	public void nextStep() {
		int old_stepNum = stepNum;
		if (stepNum < 3) {
			stepNum++;
			if (old_stepNum != stepNum)
				try {
					createStep();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			updateContent();
		}
	}

	public void backStep() {
		if (stepNum > 0) {
			stepNum--;
			updateContent();
		}
	}

	private void updateContent() {
		// assumes that the step has been created before
		//wizardBorderPane.setCenter(wizardSteps.get(stepNum));

		for (int i = 0; i < stepImageList.size(); i++) {
			if(i == stepNum){
				int depth = 7;
				DropShadow borderGlow= new DropShadow();
				borderGlow.setOffsetY(0f);
				borderGlow.setOffsetX(0f);
				borderGlow.setColor(Color.BLUE);
				borderGlow.setWidth(depth);
				borderGlow.setHeight(depth);
				 
				stepImageList.get(i).setEffect(borderGlow);
			}
			else
				stepImageList.get(i).setEffect(null);
		}
	}

	private void createStep() throws IOException {
		// creates a borderpane with content based on the current step
		BorderPane newStep = null;

		switch (stepNum) {
		case 0: // import data
			newStep = FXMLLoader.load(ImportDataController.class.getResource("views/wizard/ImportData.fxml"));
		case 1: // mappings
		case 2: // dimensions
		case 3: // cube overview
		}
		wizardSteps.add(newStep);
	}

	public Cube createCube() {
		// create the cube object and return it.
		return null;
	}

}
