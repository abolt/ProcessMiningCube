package application.models.cube;

import application.models.eventbase.AbstrEventBase;

public class CubeInfo {

	/**
	 * here we put all the interesting data that characterizes a cube.
	 * This data will be visualized in a table so the user can open any cube based on this info.
	 */
	private String name;
	private long numEvents;
	private int numAttributes;
	private int numDimensions;
	
	public CubeInfo(CubeStructure structure, AbstrEventBase eventBase){
		numDimensions = structure.getDimensions().size();
		numAttributes = eventBase.getNumberOfAttributes();
		numEvents = eventBase.getNumberofEvents();
		name = eventBase.getName();
	}
	
	public String getName() {
		return name;
	}
	
	public long getNumEvents() {
		return numEvents;
	}
	
	public int getNumAttributes() {
		return numAttributes;
	}
	
	public int getNumDimensions() {
		return numDimensions;
	}
}
