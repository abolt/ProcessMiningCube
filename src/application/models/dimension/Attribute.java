package application.models.dimension;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttribute;

@SuppressWarnings("rawtypes")
public class Attribute {

	private String attributeName;
	private Class type;
	private Map<String, XAttribute> valueSet;
	private Map<String, Boolean> selections;

	public Attribute(String name, Class typeClass) {
		attributeName = name;
		type = typeClass;
		valueSet = new HashMap<>();
		selections = new HashMap<String, Boolean>();
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Class getType() {
		return type;
	}

	public void addValue(XAttribute value) {
		if (type.isInstance(value)) {
			valueSet.put(value.toString(), value);
			selections.put(value.toString(), true);
		} else if (value != null) {
			System.out.println("value " + value.toString() + " (class " + value.getClass()
					+ ") does not match the attribute type " + type.getName());
		}
	}

	public boolean hasValue(XAttribute value) { // string value comparison for
												// now
		return valueSet.containsKey(value.toString());
	}

	public Collection<? extends XAttribute> getValueSet() {
		return valueSet.values();
	}

	public Set<?> getValueSetSample() { // returns the first 5 values
		Set<String> result = new HashSet<String>();
		Iterator<?> it = valueSet.entrySet().iterator();
		for (int i = 0; i < 5 && it.hasNext(); i++) {
			result.add(it.next().toString());
		}
		return result;
	}

	@Override
	public String toString() {
		return attributeName;
	}

	public void setSelected(String value, boolean input) {
		if (valueSet.containsKey(value))
			selections.put(value, input);
	}

	public boolean isSelected(String value) {
		return selections.get(value);
	}

}
