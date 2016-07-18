package application.models.cube;

import java.util.Map;

import org.deckfour.xes.model.XAttribute;

import application.models.dimension.Attribute;
import application.models.dimension.Dimension;
import application.models.eventbase.AbstrEventBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBoxTreeItem;

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
	
	public CubeStructure getStructure(){
		return cubeStructure;
	}
	
	public AbstrEventBase getEventBase(){
		return eventBase;
	}
	
	
	
	
	
	

	// old stuff
	private ObservableList<Cell> cells;
	private Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>> valueSelections;
	private ObservableList<Dimension> dimensions;

	public Cube(ObservableList<Dimension> dimensions,
			Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>> map) {

		this.dimensions = dimensions;
		this.valueSelections = map;
		cells = FXCollections.observableArrayList();

	}

	public Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>> getValueSelections() {
		return valueSelections;
	}

	public void setValueSelections(Map<Attribute, ObservableList<CheckBoxTreeItem<XAttribute>>> valueSelections) {
		this.valueSelections = valueSelections;
	}

	public ObservableList<Dimension> getDimensions() {
		return dimensions;
	}

	public void setDimensions(ObservableList<Dimension> dimensions) {
		this.dimensions = dimensions;
	}

	public void setCells(ObservableList<Cell> cells) {
		this.cells = cells;
	}

	public ObservableList<Cell> getCells() {
		return cells;
	}
}
