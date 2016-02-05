package application.models.cube;

import java.util.HashMap;
import java.util.Map;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Cell {

	private XLog log;
	private boolean hasTraces = false;
	private Map<String, XAttribute> dimensionalValues;

	private ObservableList<XEvent> events;

	public Cell(Map<String, XAttribute> dim, XLog log) {
		if (dim != null)
			dimensionalValues = dim;
		else
			dimensionalValues = new HashMap<String, XAttribute>();
		XFactory factory = new XFactoryBufferedImpl();
		this.log = factory.createLog(log.getAttributes());
		events = FXCollections.observableArrayList();
	}

	public XLog getLog() {
		return log;
	}

	public Map<String, XAttribute> getDimensionalValues() {
		return dimensionalValues;
	}

	public void setLog(XLog log) {
		this.log = log;
	}

	public void addValue(XAttribute att) {
		dimensionalValues.put(att.getKey(), att);
	}

	public boolean hasAttribute(String name) {
		return dimensionalValues.containsKey(name);
	}

	public XAttribute getAttribute(String name) {
		return dimensionalValues.get(name);
	}

	public void addElement(XEvent e) {
		if (isEventFitting(e))
			events.add(e);
	}

	public void addElement(XTrace t) {
		for (XEvent e : t)
			if (isEventFitting(e)) {
				log.add(t);
				hasTraces = true;
				break;
			}
	}

	public void addElement(XTrace t, XEvent firstEvent) {
		if (isEventFitting(firstEvent)) {
			log.add(t);
			hasTraces = true;
		}
	}

	public boolean hasTraces() {
		return hasTraces;
	}

	public ObservableList<XEvent> getEvents() {
		return events;
	}

	private boolean isEventFitting(XEvent e) {
		for (String attributeName : dimensionalValues.keySet())
			if (e.getAttributes().containsKey(attributeName)) {
				if (!e.getAttributes().get(attributeName).toString()
						.equals(dimensionalValues.get(attributeName).toString())) {
					return false;
				}
			} else {
				return false;
			}
		return true;
	}
}
