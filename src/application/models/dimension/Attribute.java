package application.models.dimension;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Attribute {

	public static final String IGNORE = "Ignore", TEXT = "Text", DISCRETE = "Discrete Numbers (e.g., integers)",
			CONTINUOUS = "Continuous Numbers (e.g., decimals)", DATE_TIME = "Date/Time";

	private String attributeName;
	private String type;
	private Set<String> valueSet;
	private Map<String, Boolean> selections;

	public Attribute(String name, String typeClass) {
		attributeName = name;
		type = typeClass;
		valueSet = new HashSet<String>();
		selections = new HashMap<String, Boolean>();
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public String getType() {
		return type;
	}

	public void addValue(String value) {
		valueSet.add(value);
		selections.put(value.toString(), true);
	}

	public boolean hasValue(String value) {
		return valueSet.contains(value.toString());
	}

	public Set<String> getValueSet() {
		return valueSet;
	}

	@Override
	public String toString() {
		return attributeName;
	}

	public void setSelected(String value, boolean input) {
		if (valueSet.contains(value))
			selections.put(value, input);
	}

	public boolean isSelected(String value) {
		return selections.get(value);
	}

}
