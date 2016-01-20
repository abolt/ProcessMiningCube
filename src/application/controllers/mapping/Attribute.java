package application.controllers.mapping;

import java.util.Map;
import java.util.Set;

public class Attribute {

	private String attributeName;
	private Set<?> valueSetSample;
	private Map<?, Boolean> valueSet;

	private String useAs;
	private boolean createDimension;

	public Attribute(String name, Set<?> values, String use, boolean createDim) {
		this.attributeName = name;
		this.valueSetSample = values;
		this.useAs = use;
		this.createDimension = createDim;
	}

	public void setValueSet(Map<?, Boolean> valueSet) {
		this.valueSet = valueSet;
	}

	public Set<?> getSelectedValues() {
		return valueSetSample; // for now

		// here I should have a pre-built list of the selected values, this can
		// save shitloads of time
	}

	public String getAttributeName() {
		return attributeName;
	}

	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	public Set<?> getValueSet() {
		return valueSetSample;
	}

	public void setValueSet(Set<?> valueSet) {
		this.valueSetSample = valueSet;
	}

	public String getUseAs() {
		return useAs;
	}

	public void setUseAs(String useAs) {
		this.useAs = useAs;
	}

	public boolean isCreateDimension() {
		return createDimension;
	}

	public void setCreateDimension(boolean createDimension) {
		this.createDimension = createDimension;
	}

	@Override
	public String toString() {
		return attributeName;
	}

}
