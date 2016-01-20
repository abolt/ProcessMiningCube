package application.models.eventlog;

import org.deckfour.xes.model.XLog;

public class EventLog<T> {

	private XLog xesLog = null;
	private CSVFile csvLog = null;

	public EventLog(T log) {
		if (log instanceof XLog)
			this.xesLog = (XLog) log;
		if(log instanceof CSVFile)
			this.csvLog = (CSVFile) log;
	}

	@SuppressWarnings("unchecked")
	public T getEventLog() {
		if(xesLog!= null)
			return (T) xesLog;
		else
			return (T) csvLog;
	}

}
