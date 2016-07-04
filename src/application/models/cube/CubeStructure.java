package application.models.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import application.models.dimension.Dimension;
import application.models.eventbase.AbstrEventBase;
import javafx.collections.ObservableList;

public class CubeStructure implements Serializable {

	/**
	 * This class contains the structure of the cube: All the dimensions,and for
	 * each attribute within a dimension, it has the complete valueset.
	 */
	private ObservableList<Dimension> dimensions;
	private static final long serialVersionUID = -1223520587771036396L;

	public CubeStructure(ObservableList<Dimension> dimensions, AbstrEventBase eventBase) {
		this.dimensions = dimensions;
	}

	public void populateValueSet() {

	}
	public List<Dimension> getDimensions(){
		List<Dimension> result = new ArrayList<Dimension>();
		result.addAll(dimensions);
		return result;
	}

}
