package application.models.metric;

import application.models.attribute.abstr.Attribute;

public class Metric {

	public static final String eventCount = "# Events", caseCount = "# Cases",
			avgCaseLength = "Average Case Length (# Events)", avgCaseDuration = "Average Case Duration";

	private String name;
	private Attribute caseID, timeStamp;

	public Metric(String name) {
		this.name = name;
	}

	public Attribute getCaseID() {
		return caseID;
	}

	public void setCaseID(Attribute caseID) {
		this.caseID = caseID;
	}

	public Attribute getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Attribute timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return name;
	}

}
