package application.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import application.models.attribute.abstr.Attribute;
import application.models.xlog.XLogStructure;
import application.operations.dialogs.XLogStructureDialog;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;

public class DialogUtils {

	public static XLogStructure askXLogStructure(List<Attribute> availableAttributes) {

		assert availableAttributes != null && !availableAttributes.isEmpty();

		List<Attribute> allAttributes = new ArrayList<Attribute>();

		// add the parents
		for (Attribute a : availableAttributes)
			if (a.getParent() != null && !allAttributes.contains(a.getParent()))
				allAttributes.add(a.getParent());

		// add the child
		allAttributes.addAll(availableAttributes);

		XLogStructureDialog dialog = new XLogStructureDialog(allAttributes);
		Optional<XLogStructure> result = dialog.showAndWait();

		return result.get();
	}

	public static Optional<Boolean> askIfMergeLogs() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Multiple cell selection detected");
		alert.setHeaderText("You have selected multiple cells. How do you want me to treat them?");
		alert.setContentText(
				"Select \"Merge\" if you want to merge the events in all the selected cells into one event log.\n"
						+ "Select \"Separate\" if you want to have one separate event log for each cell");

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
