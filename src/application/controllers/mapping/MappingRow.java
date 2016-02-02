package application.controllers.mapping;

import java.util.Set;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

public class MappingRow {

	private String attributeName;

	private Set<?> valueSet;
	private String useAs;
	private ObservableValue<Boolean> createDimension;

	public MappingRow(String name, Set<?> values, String use, boolean create) {
		attributeName = name;
		valueSet = values;
		useAs = use;
		createDimension = new SimpleBooleanProperty(create);
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Set<?> getValueSet() {
		return valueSet;
	}

	public ObservableValue<Boolean> createDimensionProperty() {
		return createDimension;
	}

	public String getUseAs() {
		return useAs;
	}

	public void setUseAs(String useAs) {
		this.useAs = useAs;
	}
}
