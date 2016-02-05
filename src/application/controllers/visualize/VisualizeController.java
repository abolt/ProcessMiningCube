package application.controllers.visualize;

import application.controllers.AbstractTabController;
import application.models.cube.Cell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class VisualizeController extends AbstractTabController {

	public static final String ALPHA = "Alpha Miner", HEURISTIC = "Heuristics Miner", DOTTED = "Dotted Chart",
			INDUCTIVE = "Inductive Miner", DIRECTLY_FOLLOWS = "Directly Follows Graph (FAST)";

	private ObservableList<String> algorithmSelectionList;
	@FXML
	private Tab tabVisualize;
	@FXML
	private ComboBox<String> algorithmSelection;
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
		if (value) {
			algorithmSelectionList = FXCollections.observableArrayList();
			algorithmSelectionList.add(ALPHA);
			algorithmSelectionList.add(HEURISTIC);
			algorithmSelectionList.add(INDUCTIVE);
			algorithmSelectionList.add(DOTTED);
			algorithmSelectionList.add(DIRECTLY_FOLLOWS);
			algorithmSelection.setItems(algorithmSelectionList);
		}

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

		// for each cell build a new window popup with the JComponent as result
		for(Cell cell : mainController.getCube().getCells()){
			
		}

		setCompleted(true);
	}

}
