package application.models.cube;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import application.models.attribute.abstr.Attribute;
import application.models.dimension.DimensionImpl;

public class SerializableCubeStructure implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7626706859159117768L;

	private List<SerializableDimensionImpl> dimensions;

	public SerializableCubeStructure(CubeStructure structure) {
		transformDimensions(structure.getDimensions());

	}

	private void transformDimensions(List<DimensionImpl> list) {
		dimensions = new ArrayList<SerializableDimensionImpl>();
		for (DimensionImpl d : list)
			dimensions.add(new SerializableDimensionImpl(d));
	}

	public List<SerializableDimensionImpl> getDimensions() {
		return dimensions;
	}

}

class SerializableDimensionImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7171667098615549041L;

	private String dimensionName;

	private List<Attribute> attributes;
	private Map<String, Attribute> attributeMap;
	private boolean isTime;
	private Attribute root;

	public SerializableDimensionImpl(DimensionImpl d) {
		dimensionName = d.getNameProperty().getValue();
		attributeMap = d.getAttributeMap();
		isTime = d.isTimeDimension();
		root = d.getRoot();
	}

	public String getDimensionName() {
		return dimensionName;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public Map<String, Attribute> getAttributeMap() {
		return attributeMap;
	}

	public boolean isTime() {
		return isTime;
	}

	public Attribute getRoot() {
		return root;
	}

}
