package application.models.eventlog;

import org.deckfour.xes.model.XLog;

public class EventLog {
	
	private final XLog eventlog;
	
	public EventLog(XLog log){
		this.eventlog = log;
	}
	
	public XLog getXLog () {		
		return eventlog;
	}
	
	
}
