package application.models.xlog;

import org.deckfour.xes.factory.XFactory;

import application.models.attribute.abstr.Attribute;

public class XLogStructure{
	Attribute caseID, eventID, timestamp;
	XFactory factory;
	
	public XLogStructure(Attribute caseID, Attribute eventID, Attribute timestamp, XFactory factory){
		this.caseID = caseID;
		this.eventID = eventID;
		this.timestamp = timestamp;
		this.factory = factory;
	}

	public Attribute getCaseID() {
		return caseID;
	}

	public Attribute getEventID() {
		return eventID;
	}

	public Attribute getTimestamp() {
		return timestamp;
	}
	
	public XFactory getFactory() {
		return factory;
	}
}
