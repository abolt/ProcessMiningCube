package application.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import application.controllers.explorer.CubeExplorerController;
import application.controllers.mainview.CubeRepositoryController;
import application.controllers.menu.MenuBarController;
import application.controllers.wizard.CubeWizardController;
import application.models.cube.Cube;
import application.prom.PluginInitRapidProM;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainController extends BorderPane implements Initializable {

	private Stage mainStage;
	private CubeRepositoryController cubeRepositoryController;

	// menubar
	@FXML
	MenuBar menuBar;
	@FXML
	MenuBarController menuBarController;

	public MainController(Stage stage) {
		mainStage = stage;

		FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/application/views/main/MainView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		initialize();
	}

	@FXML
	public void initialize() {
		PluginInitRapidProM promInit = new PluginInitRapidProM();
		promInit.initPlugin();
		
		menuBarController.init(this);
		cubeRepositoryController = new CubeRepositoryController(this);
		this.setCenter(cubeRepositoryController);
		
	}

	public void newCube() {

		final Stage wizard = new Stage();
		// wizard.initModality(Modality.APPLICATION_MODAL);
		wizard.initOwner(mainStage);
		CubeWizardController cubeController = new CubeWizardController();
		Scene dialogScene = new Scene(cubeController, 800, 600);
		wizard.setScene(dialogScene);
		wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
		wizard.setTitle("New Cube Wizard");
		wizard.showAndWait();
		cubeRepositoryController.addCube(new Cube(cubeController.getCubeStructure(), cubeController.getEventBase()));

		cubeRepositoryController.updateRepositoryList();
	}

	public void exploreCube(Cube cube) {
		final Stage wizard = new Stage();
		// wizard.initModality(Modality.APPLICATION_MODAL);
		wizard.initOwner(mainStage);
		CubeExplorerController cubeController = new CubeExplorerController(cube);
		Scene dialogScene = new Scene(cubeController, 800, 800);
		wizard.setScene(dialogScene);
		wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
		wizard.setTitle("Process Cube Explorer");
		wizard.showAndWait();
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
