package application.controllers.mapping;

import java.util.Set;

public class MappingRow {

	private String attributeName;

	private Set<?> valueSet;
	private String useAs;
	private boolean createDimension;

	public MappingRow(String name, Set<?> values, String use, boolean create) {
		attributeName = name;
		valueSet = values;
		useAs = use;
		createDimension = create;
	}

	public String getAttributeName() {
		return attributeName;
	}

	public Set<?> getValueSet() {
		return valueSet;
	}

	public boolean getCreateDimension() {
		return createDimension;
	}

	public String getUseAs() {
		return useAs;
	}

	public void setUseAs(String useAs) {
		this.useAs = useAs;
	}
}
