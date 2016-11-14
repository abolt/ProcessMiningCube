package application.controllers.results;



import java.util.List;

import org.deckfour.xes.model.XEvent;

public class LogResult {

	private List<XEvent> events;
	public LogResult(List<XEvent> events) {
		this.events = events;
		
	}
}
