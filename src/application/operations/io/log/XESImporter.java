package application.operations.io.log;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.in.XUniversalParser;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import application.controllers.mapping.MappingController;
import application.controllers.mapping.MappingRow;
import application.operations.io.Importer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class XESImporter extends Importer {

	public XESImporter(File in) {
		super(in);
	}

	@Override
	public boolean canParse() {
		XUniversalParser parser = new XUniversalParser();
		if (parser.canParse(file))
			return true;
		else
			return false;
	}

	@Override
	public XLog importFromFile() {
		XLog log = null;
		XUniversalParser parser = new XUniversalParser();
		Collection<XLog> collection;
		try {
			collection = parser.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
			logErrorMessage();
			return null;
		}
		log = !collection.isEmpty() ? collection.iterator().next() : null;
		return log;
	}

	protected void logErrorMessage() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid Event Log");
		alert.setContentText(
				"The event log that you are trying to import is not valid. Please check that you are using the right one!");
		alert.showAndWait();
	}

	@Override
	public ObservableList<MappingRow> getSampleList() {
		XLog log = importFromFile();
		Set<String> attributeNamesSet = new HashSet<String>();
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

		// defines the set of attributes
		for (XTrace trace : log) {
			for (XEvent event : trace) {
				attributeNamesSet.addAll(event.getAttributes().keySet());
			}
		}
		// create sample value sets and the corresponding MappingRows
		int counter = 0;
		for (String att : attributeNamesSet) {
			Set<String> values = new HashSet<String>();
			counter = 5;
			for (XTrace trace : log) {
				if (counter == 0)
					break;
				for (XEvent event : trace) {
					if (counter == 0)
						break;
					if (event.getAttributes().containsKey(att) && !event.getAttributes().get(att).toString().isEmpty())
						if (!values.contains(event.getAttributes().get(att).toString())) {
							values.add(event.getAttributes().get(att).toString());
							counter--;
						}
				}
			}
			attributes.put(att, values);
		}

		ObservableList<MappingRow> attributeObjects = FXCollections.observableArrayList();
		for (String att : attributes.keySet()) {
			attributeObjects.add(new MappingRow(att, attributes.get(att), MappingController.IGNORE, false));
		}
		return attributeObjects;
	}

}
