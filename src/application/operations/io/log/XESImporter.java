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
import org.deckfour.xes.model.XAttributeBoolean;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeBooleanImpl;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeIDImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

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

		List<XEvent> events = getEventList(-1,null); // get the full list

		Set<String> attributeNamesSet = new HashSet<String>();
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

		// defines the set of attributes
		for (XEvent event : events) {
			attributeNamesSet.addAll(event.getAttributes().keySet());
		}
		// create sample value sets and the corresponding MappingRows
		int counter = 0;
		for (String att : attributeNamesSet) {
			Set<String> values = new HashSet<String>();
			counter = 5;
			for (XEvent event : events) {
				if (counter == 0)
					break;
				if (event.getAttributes().containsKey(att) && !event.getAttributes().get(att).toString().isEmpty())
					if (!values.contains(event.getAttributes().get(att).toString())) {
						values.add(event.getAttributes().get(att).toString());
						counter--;
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
	@Override
	public List<XEvent> getEventList(long size, List<Attribute> a) {

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
			for (XEvent event : trace) {
				Collection<XAttribute> attMap = new ArrayList<XAttribute>();
				attMap.addAll(event.getAttributes().values());
				for (XAttribute att : attMap) {
					XAttribute newAtt = createAttributeWithPrefix("E:", att);
					event.getAttributes().remove(att.getKey());
					event.getAttributes().put(newAtt.getKey(), newAtt);
				}
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
		long counter = 0;
		for (XTrace trace : log)
			for (XEvent event : trace) {
				events.add(event);
				counter++;
				if (counter == size)
					return events;
			}
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
		XAttribute newAtt = null;

		if (attribute instanceof XAttributeBoolean)
			newAtt = new XAttributeBooleanImpl(prefix + attribute.getKey(), ((XAttributeBoolean) attribute).getValue());
		else if (attribute instanceof XAttributeLiteral)
			newAtt = new XAttributeLiteralImpl(prefix + attribute.getKey(), ((XAttributeLiteral) attribute).getValue());
		else if (attribute instanceof XAttributeContinuous)
			newAtt = new XAttributeContinuousImpl(prefix + attribute.getKey(),
					((XAttributeContinuous) attribute).getValue());
		else if (attribute instanceof XAttributeDiscrete)
			newAtt = new XAttributeDiscreteImpl(prefix + attribute.getKey(),
					((XAttributeDiscrete) attribute).getValue());
		else if (attribute instanceof XAttributeTimestamp)
			newAtt = new XAttributeTimestampImpl(prefix + attribute.getKey(),
					((XAttributeTimestamp) attribute).getValue());
		else if (attribute instanceof XAttributeID)
			newAtt = new XAttributeIDImpl(prefix + attribute.getKey(), ((XAttributeID) attribute).getValue());

		return newAtt;
	}

}
