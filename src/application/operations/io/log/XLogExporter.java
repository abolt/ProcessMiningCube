package application.operations.io.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.deckfour.xes.model.XLog;
import org.processmining.log.csvexport.XesCsvSerializer;
import org.processmining.plugins.log.exporting.ExportLogXes;
import org.processmining.plugins.log.exporting.ExportLogXesGz;

import application.operations.dialogs.DirectoryChooserDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

public class XLogExporter {

	public enum Format {
		XES, XES_GZ, CSV
	}

	public static Format askFormat() {
		List<String> choices = new ArrayList<>();
		choices.add(".xes");
		choices.add(".xes.gz");
		choices.add(".csv");

		ChoiceDialog<String> dialog = new ChoiceDialog<>(".xes", choices);
		dialog.setTitle("Export Event Log(s)");
		dialog.setHeaderText("Please select an export format.");
		dialog.setContentText("Available formats: .xes, .xes.gz (compressed), and .csv");

		// Traditional way to get the response value.
		Optional<String> result = dialog.showAndWait();
		if (!result.isPresent())
			return null;

		if (result.get().equals(".xes"))
			return Format.XES;
		else if (result.get().equals(".csv"))
			return Format.CSV;
		else
			return Format.XES_GZ;

	}
	
	public static File askDirectory() {
		DirectoryChooserDialog directoryDialog = new DirectoryChooserDialog("Export Event Log(s)",
				"Please select a Folder", "The selected event log(s) will be exported here.");
		Optional<File> directory = directoryDialog.showAndWait();

		return directory.get();
	}

	public static void exportLog(XLog log, Format format, String folder) {

		String path = log.getAttributes().get("concept:name").toString().trim();
		switch (format) {
		case XES:
			path = path + ".xes";
			break;
		case XES_GZ:
			path = path + ".xes.gz";
			break;
		case CSV:
			path = path + ".csv";
			break;
		}

		path = path.replace(":", "_");
		path = folder + File.separator + path;

		File file = null;
		try {
			file = new File(path);
			Files.createFile(file.toPath());
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("The file already exists!");
			alert.setContentText("Do you want to overwrite the existing file?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() != ButtonType.OK)
				return;
		}

		try {
			switch (format) {
			case XES:
				ExportLogXes.export(log, file);
				break;

			case XES_GZ:
				ExportLogXesGz.export(log, file);
				break;

			case CSV:
				XesCsvSerializer csvSerializer = new XesCsvSerializer("dd-MM-yyyy HH:mm:ss.SSS");
				OutputStream output = new FileOutputStream(path);
				csvSerializer.serialize(log, output);
				break;
			}

		} catch (IOException e1) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("The Event Log(s) could not be exported...");
			alert.setContentText("An exception occurred during the export... Sorry!");

			// Create expandable Exception.
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);
			String exceptionText = sw.toString();

			Label label = new Label("The exception stacktrace was:");

			TextArea textArea = new TextArea(exceptionText);
			textArea.setEditable(false);
			textArea.setWrapText(true);

			textArea.setMaxWidth(Double.MAX_VALUE);
			textArea.setMaxHeight(Double.MAX_VALUE);
			GridPane.setVgrow(textArea, Priority.ALWAYS);
			GridPane.setHgrow(textArea, Priority.ALWAYS);

			GridPane expContent = new GridPane();
			expContent.setMaxWidth(Double.MAX_VALUE);
			expContent.add(label, 0, 0);
			expContent.add(textArea, 0, 1);

			// Set expandable Exception into the dialog pane.
			alert.getDialogPane().setExpandableContent(expContent);

			alert.showAndWait();

		}
	}
}
