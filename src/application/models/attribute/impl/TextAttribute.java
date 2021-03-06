package application.models.attribute.impl;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import application.models.attribute.abstr.AbstrAttribute;
import application.models.attribute.abstr.Attribute;

public class TextAttribute extends AbstrAttribute<String> {

	private static final long serialVersionUID = 4335847962046185903L;

	private TreeSet<String> selectedValueSet;

	public TextAttribute(String name, String type, Attribute parent) {
		super(name, type, parent);
		selectedValueSet = new TreeSet<String>();
	}

	public Set<String> getSelectedValueSet() {
		return Collections.unmodifiableSet(selectedValueSet);
	}

	public void addValueToSelectedValueset(String newValue) {
		selectedValueSet.add(newValue);
	}

	public void removeValueFromSelectedValueset(String newValue) {
		selectedValueSet.remove(newValue);
	}

	public void clearSelectedValueSet() {
		selectedValueSet.clear();
	}
}
