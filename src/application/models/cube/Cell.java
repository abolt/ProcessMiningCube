package application.models.cube;

import java.util.EnumMap;
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

	public enum Metrics {
		CASES, EVENTS, EVENTS_PER_CASE, CASE_DURATION, ENTROPY
	}

	private XLog log;
	private boolean isSelected = false;
	private boolean hasTraces = false;
	private Map<String, XAttribute> dimensionalValues;
	private Map<String, String> dimensions;

	private Map<Metrics, Double> metrics;

	private ObservableList<XEvent> events;

	public Cell(Map<String, XAttribute> dim, XLog log) {
		if (dim != null)
			dimensionalValues = dim;
		else {
			dimensionalValues = new HashMap<String, XAttribute>();
			dimensions = new HashMap<String, String>();
		}
		XFactory factory = new XFactoryBufferedImpl();
		this.log = factory.createLog(log.getAttributes());
		events = FXCollections.observableArrayList();
	}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
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

	public void addValue(XAttribute att, String dimensionName) {
		dimensionalValues.put(att.getKey(), att);
		dimensions.put(att.getKey(), dimensionName);
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
		if (!log.contains(t))
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

	public String getDimension(String attributeName) {
		return dimensions.get(attributeName);
	}

	public void calculateMetrics() { // assuming the log has something
		/*
		 * TO-DO: implement an entropy metric and the case duration :)
		 */
		metrics = new EnumMap<Metrics, Double>(Metrics.class);
		metrics.put(Metrics.CASES, Double.valueOf(log.size()));
		double numberOfEvents = 0;
		// double totalCaseDuration = 0;
		// double entropy = 0;

		for (XTrace t : log) {
			numberOfEvents = numberOfEvents + t.size();
			// if(t.size() > 0){
			// XEvent first = t.get(0);
			// XEvent last = t.get(t.size()-1);
			// }

		}
		metrics.put(Metrics.EVENTS, numberOfEvents);
		metrics.put(Metrics.EVENTS_PER_CASE, numberOfEvents / metrics.get(Metrics.CASES));

	}

	public double getMetric(Metrics metric) {
		return metrics.get(metric);
	}
}
