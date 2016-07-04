package application.models.wizard;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;

public class MappingRow {

	private String attributeName;

	private Set<String> valueSet;
	private String useAs;
	private ObservableValue<Boolean> createDimension;

	public MappingRow(String name, Set<String> values, String use, boolean create) {
		attributeName = name;
		valueSet = values;
		useAs = use;
		createDimension = new SimpleBooleanProperty(create);
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Set<String> getValueSet() {
		return valueSet;
	}

//	public Set<String> getSampleValueSet(int number) {
//		
//		Set<String> samples = new HashSet<String>();
//		Iterator<String> iterator = valueSet.iterator();
//		
//		for (int i = 0; i < number; i++) {
//			samples.add(iterator.next());
//		}
//		return samples;
//	}

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
