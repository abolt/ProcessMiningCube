package application.models.attribute.abstr;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public interface Attribute extends Serializable, Comparable<Attribute>{

	public static final String IGNORE = "IGNORE", TEXT = "TEXT", DISCRETE = "INTEGER", CONTINUOUS = "REAL",
			DATE_TIME = "DATETIME", DERIVED = "DERIVED";

	Attribute getParent();

	Map<String, Attribute> getChildren();

	public String getName();

	public String getLabel();

	public String getQueryString();

	public String getType();

	void setLabel(String label);

	void setQueryString(String queryString);

	public Set<?> getValueSet();

}
