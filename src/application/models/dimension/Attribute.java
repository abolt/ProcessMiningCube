package application.models.dimension;

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
	private Map<XAttribute, Boolean> valueSet;

	public Attribute(String name, Class typeClass) {
		attributeName = name;
		type = typeClass;
		valueSet = new HashMap<>();

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
			valueSet.put(value, true);
		} else if (value != null) {
			System.out.println("value " + value.toString() + " (class " + value.getClass() + ") does not match the attribute type " + type.getName());
		}
	}

	public Set<? extends XAttribute> getValueSet() {
		return valueSet.keySet();
	}

	public Set<?> getSelectedValues() {
		return null; // for now

		// here I should have a pre-built list of the selected values, this can
		// save shitloads of time
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

}
