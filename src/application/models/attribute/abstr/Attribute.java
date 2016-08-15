package application.models.attribute.abstr;

import java.io.Serializable;
import java.util.Collection;

public interface Attribute<T> extends Serializable {

	public static final String IGNORE = "IGNORE", TEXT = "TEXT", DISCRETE = "INTEGER", CONTINUOUS = "REAL",
			DATE_TIME = "DATETIME", DERIVED = "DERIVED";

	public Attribute<?> getParent();
	
	public Collection<Attribute<?>> getChildren();
	
	public String getAttributeName();

	public String getAttributeType();

	public boolean addValue(T value);
	
	public boolean addValue(String value);
	
	public boolean removeValue(T value);

	public boolean hasValue(T value);

	public Collection<T> getValueSet();

	public void resetValueSet();

	

}
