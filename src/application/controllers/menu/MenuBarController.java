package application.controllers.menu;

import application.controllers.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;

public class MenuBarController {

	@FXML private MenuBar menuBar;
	MainController mainController;
	
	@FXML public void initialize() {
		
	}


	public void init(MainController mainControllerInput) {
		mainController = mainControllerInput;
	}
	
	@FXML protected void newProcessCube(ActionEvent event) {
		mainController.newCube();
		
	}

}
