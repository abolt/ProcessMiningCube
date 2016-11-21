package application.operations.dialogs;

import java.io.File;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class FileChooserDialog extends Dialog<File> {

	public FileChooserDialog(String title, String header, String description) {
		setTitle(title);
		setHeaderText(header);
		setContentText(description);

		TextField folderPath = new TextField();
		folderPath.setEditable(false);

		Button dirChooser = new Button("Select file");
		dirChooser.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				FileChooser rapidMinerDirectory = new FileChooser();
				rapidMinerDirectory.setTitle(title);
				File directory = rapidMinerDirectory.showOpenDialog(null);

				folderPath.setText(directory.getAbsolutePath());
			}
		});

		VBox mainBox = new VBox();
		mainBox.setPadding(new Insets(10));
		mainBox.setSpacing(8);

		HBox box1 = new HBox();
		box1.setSpacing(8);
		box1.getChildren().add(new Label("Selected File: "));
		box1.getChildren().add(folderPath);
		box1.getChildren().add(dirChooser);
		mainBox.getChildren().add(box1);

		getDialogPane().setContent(mainBox);
		ButtonType ok = new ButtonType("Ok", ButtonData.OK_DONE);
		getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

		setResultConverter(dialogButton -> {
			if (dialogButton == ok) {
				File directory = new File(folderPath.getText());
				directory.getAbsolutePath();
				return directory;
			}
			return null;
		});
	}
}
