package application.controllers.visualize;

import application.controllers.AbstractTabController;
import application.models.cube.Cell;
import application.operations.miner.FastMiner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;

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
			algorithmSelection.getSelectionModel().select(DIRECTLY_FOLLOWS);
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
		for (Cell cell : mainController.getCube().getCells()) {
			if (cell.isSelected()) {
				final SwingNode node = new SwingNode();
				node.setContent(FastMiner.get_visual_results(FastMiner.execute(cell.getLog(), 0.2, 5)));
				@SuppressWarnings("rawtypes")
				Dialog dialog = new Dialog<>();

				// X button on the title bar
				dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
				Node closeButton = dialog.getDialogPane().lookupButton(ButtonType.CLOSE);
				closeButton.managedProperty().bind(closeButton.visibleProperty());
				closeButton.setVisible(false);

				// init the content
				dialog.getDialogPane().setPrefSize(600, 600);

				String title = "";
				for (String s : cell.getDimensionalValues().keySet())
					title = title + "(" + s + " = " + cell.getDimensionalValues().get(s).toString() + ")";
				dialog.setTitle(title);

				dialog.getDialogPane().setContent(node);
				dialog.initModality(Modality.NONE);
				dialog.setResizable(true);
				dialog.show();
			}
		}

		setCompleted(true);
	}

}
