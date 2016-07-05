package application.operations.io.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.in.XUniversalParser;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import application.models.dimension.Attribute;
import application.models.wizard.MappingRow;
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
	public List<XEvent> importFromFile() {

		return null;
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
			attributeObjects.add(new MappingRow(att, attributes.get(att), Attribute.IGNORE, false));
		}
		return attributeObjects;
	}

	/**
	 * 
	 * @param size:
	 *            defines the max number of events in the list. -1 means
	 *            infinite.
	 * @return An ordered list of events as described in the input file.
	 */
	private List<XEvent> getEventList(long size) {

		List<XEvent> events = new ArrayList<XEvent>();

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

		// add an "E:" prefix to event-level attributes
		for (XTrace trace : log)
			for (XEvent event : trace)
				for (XAttribute att : event.getAttributes().values()) {
					XAttribute newAtt = createAttributeWithPrefix("E:", att);
					event.getAttributes().remove(att.getKey());
					event.getAttributes().put(newAtt.getKey(), newAtt);
				}

		// pass trace level attributes to events
		for (XTrace trace : log) {
			List<XAttribute> traceAttributes = new ArrayList<XAttribute>();
			for (XAttribute traceAtt : trace.getAttributes().values())
				traceAttributes.add(createAttributeWithPrefix("T:", traceAtt));

			for (XEvent event : trace)
				for (XAttribute att : traceAttributes)
					event.getAttributes().put(att.getKey(), att);
		}

		// now create the final list of events
		for (XTrace trace : log)
			events.addAll(trace);

		return events;
	}

	/**
	 * 
	 * @param prefix:
	 *            the string prefix that we want to add to an attribute
	 * @param attribute:
	 *            the attribute to be modeified
	 * @return the modified attribute
	 */
	private XAttribute createAttributeWithPrefix(String prefix, XAttribute attribute) {
		// can use it with E: or T:
		
		
		
		
		return null;
	}

}
