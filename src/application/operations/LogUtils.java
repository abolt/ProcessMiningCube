package application.operations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import application.models.attribute.abstr.Attribute;

public class LogUtils {

	public static final String unknown = "_unknown";

	public static XLog buildXLogFromEvents(List<XEvent> events, Attribute caseID, XFactory factory, String logName) {

		XAttributeMap logAttributes = factory.createAttributeMap();
		XAttribute attributeLogName = factory.createAttributeLiteral("concept:name", logName, null);
		logAttributes.put("concept:name", attributeLogName);

		XLog log = factory.createLog(logAttributes);

		Map<String, List<XEvent>> traceMap = new HashMap<String, List<XEvent>>();

		for (XEvent e : events) {
			XAttribute eventCase = e.getAttributes().get(caseID.getName());
			if (eventCase == null) {
				
				if (traceMap.get(unknown) == null)
					traceMap.put(unknown, new ArrayList<XEvent>());
				traceMap.get(unknown).add(e);
			} else {
				if (traceMap.get(eventCase.toString()) == null)
					traceMap.put(eventCase.toString(), new ArrayList<XEvent>());
				traceMap.get(eventCase.toString()).add(e);
			}
		}

		for (String s : traceMap.keySet()) {
			XAttributeMap traceAttributes = factory.createAttributeMap();
			XAttribute attributeTraceName = factory.createAttributeLiteral("concept:name", s, null);
			traceAttributes.put("concept:name", attributeTraceName);

			XTrace trace = factory.createTrace(traceAttributes);
			trace.addAll(traceMap.get(s));
			log.add(trace);
		}
		return log;
	}

}
