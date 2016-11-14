package application.operations;

import java.util.List;
import java.util.Optional;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.factory.XFactoryNaiveImpl;

import application.models.attribute.abstr.Attribute;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;

public class DialogUtils {

	public static Attribute askTraceId(List<Attribute> availableAttributes) {

		assert availableAttributes != null && !availableAttributes.isEmpty();

		ChoiceDialog<Attribute> dialog = new ChoiceDialog<Attribute>(availableAttributes.get(0), availableAttributes);
		dialog.setTitle("Case ID required");
		dialog.setHeaderText(
				"Please indicate the attribute that will be used " + "\n as Case ID to build the Event Log(s)");
		dialog.setContentText("Case ID:");
		Optional<Attribute> result = dialog.showAndWait();

		return result.get();
	}

	public static XFactory askXFactory() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Event Log Materialization");
		alert.setHeaderText("Event Log(s) will be materialized. \nPlease select a XFactory to do so.");
		alert.setContentText("Select \"In-Memory\" to materialize the event log(s) using your available memory. "
				+ "\nNote that your memory may not be enough to materialize large logs."
				+ "Select \"Disk-Buffered\" to materialize your event log(s) directly to disk,"
				+ " keeping only pointers in memory. This option is a bit slower but can handle large event logs.");

		ButtonType buttonTypeOne = new ButtonType("In-Memory");
		ButtonType buttonTypeTwo = new ButtonType("Disk-Buffered");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne) {
			return new XFactoryNaiveImpl();
		} else if (result.get() == buttonTypeTwo) {
			return new XFactoryBufferedImpl();
		} else {
			return null;
		}
	}

	public static Optional<Boolean> askIfMergeLogs() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Multiple cell selection detected");
		alert.setHeaderText("You have selected multiple cells.\n How do you want me to treat them?");
		alert.setContentText(
				"Select \"Merge\" if you want to merge the events in " + "all the selected cells into one event log.\n"
						+ "Select \"Separate\" if you want to have one " + "separate event log for each cell");

		ButtonType buttonTypeOne = new ButtonType("Merge");
		ButtonType buttonTypeTwo = new ButtonType("Separate");
		ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonData.CANCEL_CLOSE);

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo, buttonTypeCancel);

		Optional<ButtonType> result = alert.showAndWait();

		if (result.get() == buttonTypeOne) {
			return Optional.of(true);
		} else if (result.get() == buttonTypeTwo) {
			return Optional.of(false);
		} else {
			return null;
		}
	}
}
