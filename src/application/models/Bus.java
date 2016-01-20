package application.models;

import application.models.eventlog.EventLog;

public class Bus {
	
	private static EventLog log;

	public static EventLog getLog() {
		return log;
	}

	public static void setLog(EventLog input) {
		log = input;
	}

}
