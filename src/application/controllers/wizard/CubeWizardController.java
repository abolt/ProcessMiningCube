package application.controllers.wizard;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

import application.controllers.wizard.abstr.AbstractWizardStepController;
import application.controllers.wizard.steps.CubeOverViewController;
import application.controllers.wizard.steps.DimensionsController;
import application.controllers.wizard.steps.EasyDimensionsController;
import application.controllers.wizard.steps.ImportDataController;
import application.controllers.wizard.steps.MappingController;
import application.models.cube.CubeStructure;
import application.models.eventbase.AbstrEventBase;
import application.models.eventbase.FileBasedEventBase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class CubeWizardController extends BorderPane implements Initializable {

	private CubeStructure cube;
	private AbstrEventBase eventBase;
	private boolean readMode;

	@FXML
	private ImageView imageStep0, imageStep1, imageStep2, imageStep3;

	private Map<Integer, AbstractWizardStepController> steps;
	private Map<Integer, ImageView> images;

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

		images = new HashMap<Integer, ImageView>();
		steps = new HashMap<Integer, AbstractWizardStepController>();

		images.put(0, imageStep0);
		images.put(1, imageStep1);
		images.put(2, imageStep2); // easy dimensions (same image)
		images.put(3, imageStep2); // dimensions config
		images.put(4, imageStep3);

		stepNum = 0;
		updateContent();
	}

	public AbstractWizardStepController getNode(int number) {
		if (number >= 0 && number < steps.size())
			if (steps.get(number) != null)
				return steps.get(number);
		return null;
	}

	public void nextStep() {
		if (stepNum < 4)
			stepNum++;
		updateContent();
	}

	public void backStep() {
		if (stepNum > 0)
			stepNum--;
		updateContent();
	}

	private void updateContent() {
		if (!readMode)
			createStep(stepNum);

		this.setCenter((Node) getNode(stepNum));

		for (int i = 0; i < images.size(); i++) {
			if (i == stepNum) {
				int depth = 50;
				DropShadow borderGlow = new DropShadow();
				borderGlow.setOffsetY(0.1f);
				borderGlow.setOffsetX(0.1f);
				borderGlow.setColor(Color.GREENYELLOW);
				borderGlow.setWidth(depth);
				borderGlow.setHeight(depth);

				images.get(i).setEffect(borderGlow);
			} else
				images.get(i).setEffect(null);
		}
	}

	private void createStep(int number) {
		switch (number) {
		case 0: // import data
			steps.put(number, new ImportDataController(this));
			break;
		case 1: // mappings
			steps.put(number, new MappingController(this));
			break;
		case 2: // dimensions
			steps.put(number, new EasyDimensionsController(this));
			break;
		case 3: // dimensions
			steps.put(number, new DimensionsController(this));
			break;
		case 4: // cube overview
			steps.put(number, new CubeOverViewController(this));
			break;
		}
	}

	public void createCube() throws Exception {

		boolean dbOk = false;
		String name = null;
		do {
			TextInputDialog dialog = new TextInputDialog();
			dialog.setTitle("Name your cube!");
			dialog.setHeaderText("Please enter the name of your new Cube.");
			dialog.showAndWait();
			name = dialog.getResult();
			if (!AbstrEventBase.dbExists(System.getProperty("user.home") + File.separator + name + ".db"))
				dbOk = true;
			else {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Warning!");
				alert.setHeaderText("Existing cube!");
				alert.setContentText("There is an existing cube with that name, would you like to overwrite it?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() == ButtonType.OK)
					dbOk = true;
			}
		} while (!dbOk);

		createEventBase(name);
		createCubeStructure();

	}

	private void createEventBase(String name) {
		eventBase = new FileBasedEventBase(((ImportDataController) steps.get(0)).getFileName(), name,
				((DimensionsController) steps.get(3)).getAllAttributes());
	}

	public void createCubeStructure() throws Exception {
		// create the cube object, and make it available for the visualizer.
		cube = new CubeStructure(((DimensionsController) steps.get(3)).getDimensions());
		cube.populateValueSet(eventBase);
		cube.initializeComposedDimensions();

		this.getScene().getWindow().hide();
	}

	public CubeStructure getCubeStructure() {
		return cube;
	}

	public AbstrEventBase getEventBase() {
		return eventBase;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

	public void setReadMode(boolean readMode) {
		this.readMode = readMode;
	}

}
