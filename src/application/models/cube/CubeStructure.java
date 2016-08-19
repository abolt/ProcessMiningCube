package application.models.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import application.models.attribute.abstr.Attribute;
import application.models.dimension.DimensionImpl;
import application.models.eventbase.AbstrEventBase;
import javafx.collections.ObservableList;

public class CubeStructure implements Serializable {

	/**
	 * This class contains the structure of the cube: All the dimensions,and for
	 * each attribute within a dimension, it has the complete valueset.
	 */
	private ObservableList<DimensionImpl> dimensions;
	private static final long serialVersionUID = -1223520587771036396L;

	public CubeStructure(ObservableList<DimensionImpl> dimensions) {
		this.dimensions = dimensions;
	}

	public void populateValueSet(AbstrEventBase eb) {
		for (DimensionImpl d : dimensions)
			// if dimension is time, do something else, getting the range with
			// the root and filling out the other attributes.
			for (Attribute<?> a : d.getAttributes()) {
				Set<String> valueSet = eb.getValueSet(a.getLabel());
				a.getValueSet().clear();
				for (String s : valueSet)
					a.addValue(s);
			}
	}

	public void initializeComposedDimensions() {
		for (DimensionImpl d : dimensions)
			if (d.isTimeDimension())
				d.initializeTimeDimension();
	}

	public List<DimensionImpl> getDimensions() {
		List<DimensionImpl> result = new ArrayList<DimensionImpl>();
		result.addAll(dimensions);
		return result;
	}

}
