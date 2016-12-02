package application.models.dimension;

import java.util.HashMap;
import java.util.Map;

import application.models.attribute.abstr.Attribute;
import application.models.attribute.impl.DateTimeAttribute;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DimensionImpl {

	// sliced is a special kind of visibility:
	// it does show in the list of
	// selected dimensions, and its filters do apply
	private ObservableValue<String> dimensionName;

	private ObservableList<Attribute> attributes;
	private Map<String, Attribute> attributeMap;
	private boolean isTime;
	private Attribute root; // only used in composed dimensions

	public DimensionImpl(String name, boolean isComposed) {
		dimensionName = new SimpleStringProperty(name);
		attributes = FXCollections.observableArrayList();
		attributeMap = new HashMap<String, Attribute>();
		this.isTime = isComposed;
	}

	/**
	 * this method adds an attribute to a dimension
	 * 
	 * @param a
	 * @return true or false depending on if the attribute could be added to the
	 *         dimension
	 */
	public boolean addAttribute(Attribute a) {
		if (a instanceof DateTimeAttribute) {
			if (attributes.isEmpty()) {
				attributes.add(a);
				attributeMap.put(a.getLabel(), a);
				isTime = true;
				return true;
			} else
				return false;
		} else if (!isTime) {
			attributes.add(a);
			attributeMap.put(a.getLabel(), a);
			return true;
		}

		return false;
	}

	public void removeAttribute(Attribute a) {
		if (attributes.contains(a)) {
			attributes.remove(a);
			attributeMap.put(a.getLabel(), null);
		}
		if (attributes.isEmpty())
			isTime = false;
	}

	public ObservableList<Attribute> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return dimensionName.getValue();
	}

	public ObservableValue<String> getNameProperty() {
		return dimensionName;
	}

	public boolean hasAttribute(String name) {
		if (attributeMap.containsKey(name))
			return true;
		else
			return false;
	}

	public Attribute getAttribute(String name) {
		return attributeMap.get(name);
	}

	public boolean isTimeDimension() {
		return isTime;
	}

	public void initializeTimeDimension() {

		if (root == null)
			root = attributes.get(0);

		if (root instanceof DateTimeAttribute) {
			// add the attributes
			attributes.clear();
			attributeMap.clear();
			DateTimeAttribute time = (DateTimeAttribute) root;

			for (Attribute a : time.getChildren().values()) {
				attributes.add(a);
				attributeMap.put(a.getLabel(), a);
			}
		}
	}

	public Map<String, Attribute> getAttributeMap() {
		return attributeMap;
	}

	public Attribute getRoot() {
		return root;
	}

	public void setRoot(Attribute root) {
		this.root = root;
	}
}
