package application.operations.io.cube;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Optional;

import application.models.cube.Cube;
import application.models.cube.SerializableCubeStructure;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;

public class CubeExporter {

	public static void exportCube(Cube cube) {

		try {
			DirectoryChooser dir = new DirectoryChooser();
			dir.setTitle("Select a Folder to export the cube");
			File f = dir.showDialog(null);
			String aux = f.getAbsolutePath() + File.separator + cube.getCubeInfo().getName() + ".cub";
			File file = new File(aux);

			try {
				Files.createFile(file.toPath());
			} catch (IOException ex) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Warning");
				alert.setHeaderText("The cube already exists!");
				alert.setContentText("Do you want to overwrite the existing file?");

				Optional<ButtonType> result = alert.showAndWait();
				if (result.get() != ButtonType.OK)
					return;
			}
			FileOutputStream fileOut = new FileOutputStream(file);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);

			out.writeObject(new SerializableCubeStructure(cube.getStructure()));
			out.close();
			fileOut.close();

			System.out.printf("Serialized data is saved");
		} catch (Exception e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error exporting the Cube");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			e.printStackTrace();
		}

	}

}
