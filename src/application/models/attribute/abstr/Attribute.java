package application.models.attribute.abstr;

import java.io.Serializable;
import java.util.Collection;

public interface Attribute<T> extends Serializable {

	public static final String IGNORE = "IGNORE", TEXT = "TEXT", DISCRETE = "INTEGER", CONTINUOUS = "REAL",
			DATE_TIME = "DATETIME", DERIVED = "DERIVED";

	public Attribute<?> getParent();

	public Collection<Attribute<?>> getChildren();

	public String getName();

	public String getLabel();

	public void setLabel(String label);

	public String getQueryString();
	
	public void setQueryString(String queryString);

	public String getType();

	public boolean addValue(T value);

	public boolean addValue(String value);

	public boolean removeValue(T value);

	public boolean hasValue(T value);

	public Collection<T> getValueSet();
	
	public int getValueSetSize();

	public void resetValueSet();
	
	public void addChild(Attribute<?> newAtt);

}
