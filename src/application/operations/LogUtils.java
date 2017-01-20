package application.operations;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

import application.models.attribute.abstr.Attribute;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class LogUtils {

	public static final String unknown = "_unknown";

	public static XLog buildXLogFromEvents(List<XEvent> events, Attribute caseID, Attribute eventID,
			Attribute timestamp, XFactory factory, String logName) {

		XAttributeMap logAttributes = factory.createAttributeMap();
		XAttribute attributeLogName = factory.createAttributeLiteral("concept:name", logName, null);
		logAttributes.put("concept:name", attributeLogName);

		XLog log = factory.createLog(logAttributes);

		Map<String, List<XEvent>> traceMap = new HashMap<String, List<XEvent>>();

		for (XEvent e : events) {
			XEvent eNew = (XEvent) e.clone();
			
			//replace concept:name
			XAttribute oldEventID = eNew.getAttributes().get(eventID.getName());
			eNew.getAttributes().remove(oldEventID);
			eNew.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", oldEventID.toString()));
			
			//replace time:timestamp
			
			if(!(eNew.getAttributes().get(timestamp.getName()) instanceof XAttributeTimestamp)){
				Alert alert = new Alert(AlertType.ERROR,"The selected timestamp is not of type date time!");
				alert.showAndWait();
				return null;
			}
			XAttributeTimestamp oldTimestamp = (XAttributeTimestamp) eNew.getAttributes().get(timestamp.getName());
			eNew.getAttributes().remove(oldTimestamp);
			eNew.getAttributes().put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", oldTimestamp.getValue()));
			
			
			XAttribute eventCase = e.getAttributes().get(caseID.getName());
			if (eventCase == null) {
				if (traceMap.get(unknown) == null)
					traceMap.put(unknown, new ArrayList<XEvent>());
				traceMap.get(unknown).add(eNew);
			} else {
				if (traceMap.get(eventCase.toString()) == null)
					traceMap.put(eventCase.toString(), new ArrayList<XEvent>());
				traceMap.get(eventCase.toString()).add(eNew);
			}
		}

		for (String s : traceMap.keySet()) {
			XAttributeMap traceAttributes = factory.createAttributeMap();
			XAttribute attributeTraceName = factory.createAttributeLiteral("concept:name", s, null);
			traceAttributes.put("concept:name", attributeTraceName);

			XTrace trace = factory.createTrace(traceAttributes);
			trace.addAll(traceMap.get(s));
			
			trace.sort(new Comparator<XEvent>(){

				@Override
				public int compare(XEvent o1, XEvent o2) {
					return o1.getAttributes().get("ID").compareTo(o2.getAttributes().get("ID"));
				}
				
			});
			log.add(trace);
		}
		return log;
	}

}
