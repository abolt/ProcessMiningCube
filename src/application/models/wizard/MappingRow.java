package application.models.wizard;

import java.util.Set;

public class MappingRow {

	private String attributeName;

	private Set<String> valueSet;
	private String useAs;

	public MappingRow(String name, Set<String> values, String use) {
		attributeName = name;
		valueSet = values;
		useAs = use;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Set<String> getValueSet() {
		return valueSet;
	}

	public String getUseAs() {
		return useAs;
	}

	public void setUseAs(String useAs) {
		this.useAs = useAs;
	}
}
