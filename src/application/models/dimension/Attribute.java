package application.models.dimension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Attribute {

	public static final String IGNORE = "IGNORE", TEXT = "TEXT", DISCRETE = "INTEGER", CONTINUOUS = "REAL",
			DATE_TIME = "DATETIME";

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

	public List<String> getSelectedValueSet() {
		List<String> result = new ArrayList<String>();

		for (String s : valueSet)
			if (selections.get(s) == true)
				result.add(s);
		return result;
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
