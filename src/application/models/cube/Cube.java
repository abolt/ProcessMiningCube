package application.models.cube;

import application.models.eventbase.AbstrEventBase;

public class Cube {

	private CubeStructure cubeStructure;
	private AbstrEventBase eventBase;
	private CubeInfo statistics;

	public Cube(CubeStructure cubeStructure, AbstrEventBase eventBase) {
		this.cubeStructure = cubeStructure;
		this.eventBase = eventBase;
		this.statistics = new CubeInfo(this.cubeStructure, this.eventBase);

	}

	public CubeInfo getCubeInfo() {
		return statistics;
	}

	public CubeStructure getStructure() {
		return cubeStructure;
	}

	public AbstrEventBase getEventBase() {
		return eventBase;
	}
}
