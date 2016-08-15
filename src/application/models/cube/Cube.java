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

	// old stuff
	// private ObservableList<Cell> cells;
	// private Map<AttributeImpl, ObservableList<CheckBoxTreeItem<XAttribute>>>
	// valueSelections;
	// private ObservableList<DimensionImpl> dimensions;
	//
	// public Cube(ObservableList<DimensionImpl> dimensions,
	// Map<AttributeImpl, ObservableList<CheckBoxTreeItem<XAttribute>>> map) {
	//
	// this.dimensions = dimensions;
	// this.valueSelections = map;
	// cells = FXCollections.observableArrayList();
	//
	// }
	//
	// public Map<AttributeImpl, ObservableList<CheckBoxTreeItem<XAttribute>>>
	// getValueSelections() {
	// return valueSelections;
	// }
	//
	// public void setValueSelections(Map<AttributeImpl,
	// ObservableList<CheckBoxTreeItem<XAttribute>>> valueSelections) {
	// this.valueSelections = valueSelections;
	// }
	//
	// public ObservableList<DimensionImpl> getDimensions() {
	// return dimensions;
	// }
	//
	// public void setDimensions(ObservableList<DimensionImpl> dimensions) {
	// this.dimensions = dimensions;
	// }
	//
	// public void setCells(ObservableList<Cell> cells) {
	// this.cells = cells;
	// }
	//
	// public ObservableList<Cell> getCells() {
	// return cells;
	// }
}
