package application.controllers.visualize;

import application.controllers.AbstractTabController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VisualizeController extends AbstractTabController {

	@FXML
	private Tab tabVisualize;

	@FXML
	private ImageView image;

	@FXML
	protected void initialize() {
		name = "visualizeController";
	}

	@Override
	public void initializeTab(Tab input) {
		tabVisualize = input;
	}

	@Override
	protected void enableTab(boolean value) {
		tabVisualize.setDisable(!value);
		if (value)
			;
	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/eye_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/eye_green.png"));
		else
			image.setImage(new Image("images/eye_black.png"));
	}

	@FXML
	protected void handleVisualizeButton(ActionEvent event) {

		// do stuff

		setCompleted(true);
	}

}
