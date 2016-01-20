package application.controllers.materialize;

import application.controllers.AbstractTabController;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MaterializeController  extends AbstractTabController {

	@FXML
	private Tab tabMaterialize;

	@FXML
	private ImageView image;

	@FXML
	protected void initialize() {
		name = "materializeController";
	}	
	
	@Override
	public void initializeTab(Tab input) {
		tabMaterialize = input;
	}

	@Override
	protected void enableTab(boolean value) {
		tabMaterialize.setDisable(!value);
		if (value)
			;
	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/gear_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/gear_green.png"));
		else
			image.setImage(new Image("images/gear_black.png"));
	}

}
