package application.models.eventlog;

import org.deckfour.xes.model.XLog;

public class EventLog {

	private XLog xesLog = null;

	public EventLog(XLog log) {
		this.xesLog = (XLog) log;

	}

	public XLog getEventLog() {
		return xesLog;
	}

}
