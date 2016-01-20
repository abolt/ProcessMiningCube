package application.controllers.cube;

import application.controllers.AbstractTabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CubeController extends AbstractTabController {

	@FXML
	private Tab tabCube;

	@FXML
	private ImageView image;

	@FXML
	protected void initialize() {
		name = "cubeController";
	}

	@Override
	public void initializeTab(Tab input) {
		tabCube = input;

	}

	@Override
	protected void enableTab(boolean value) {
		tabCube.setDisable(!value);
		if (value)
			;
		// initialize the contents of the gui elements

	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/cube_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/cube_green.png"));
		else
			image.setImage(new Image("images/cube_black.png"));
	}
	
	@FXML protected void handleCreateCubeButton(ActionEvent event) {
		
		//do stuff
		setCompleted(true);
	}
}
