package application.models.dimension;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Dimension {

	// sliced is a special kind of visibility:
	// it does show in the list of
	// selected dimensions, and its filters do apply
	private ObservableValue<String> dimensionName;
	private boolean visible, sliced;
	private Attribute granularity;
	private ObservableList<Attribute> attributes;

	public Dimension(String name) {
		dimensionName = new SimpleStringProperty(name);
		attributes = FXCollections.observableArrayList();
	}

	public void addAttribute(Attribute a) {
		attributes.add(a);
	}

	public void removeAttribute(Attribute a) {
		if (attributes.contains(a))
			attributes.remove(a);
	}

	public void setGranularity(Attribute a) {
		granularity = a;
	}

	public Attribute getGranularity() {
		return granularity;
	}

	public void setVisible(boolean in) {
		visible = in;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setSliced(boolean in) {
		sliced = in;
	}

	public boolean isSliced() {
		return sliced;
	}

	public ObservableList<Attribute> getAttributes() {
		return attributes;
	}
	
	@Override
	public String toString() {
		return dimensionName.getValue();
	}
	
	public ObservableValue<String> getNameProperty(){
		return dimensionName;
	}

}
