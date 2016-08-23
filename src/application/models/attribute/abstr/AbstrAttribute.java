package application.models.attribute.abstr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

/**
 * This abstract class contains all the common variables that an attribute
 * should have, and basic functionalities for them as well.
 * 
 * @author abolt
 *
 * @param <T>
 *            (e.g., String, integer)
 */
public abstract class AbstrAttribute<T> implements Attribute<T> {

	private static final long serialVersionUID = -4511938041557436943L;

	// the name of the column in the event base that this attribute references
	protected String name;
	// the visible name that will be shown in the UI
	protected String label;
	// used to query from a DB (may contain function calls)
	protected String queryString;
	// type of attribute
	protected String type;

	// parent, if this attribute is derived
	protected Attribute<?> parent;

	// children attributes that depend on this one
	protected Map<String, Attribute<?>> children;

	// active value set with the values used to query in the DB
	protected TreeSet<T> activeValueSet;
	// read-only copy of the original valueset, used when resetting the active
	// valueset
	protected final TreeSet<T> finalValueSet;

	public AbstrAttribute(String name, String type, Attribute<?> parent) {
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.children = new HashMap<String, Attribute<?>>();
		this.activeValueSet = new TreeSet<T>();
		this.finalValueSet = new TreeSet<T>();
	}

	@Override
	public Attribute<?> getParent() {
		return parent;
	}

	@Override
	public Collection<Attribute<?>> getChildren() {
		return children.values();
	}

	public Attribute<?> getChildren(String name) {
		return children.get(name);
	}

	@Override
	public void addChild(Attribute<?> newAtt) {
		children.put(newAtt.getName(), newAtt);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getLabel() {
		String result = "";
		if (this.parent != null)
			result = this.parent.getLabel() + ":" + label;
		else
			result = label;
		return result;
	}

	@Override
	public void setLabel(String newLabel) {
		this.label = newLabel;
	}

	@Override
	public String getQueryString() {
		return queryString;
	}

	@Override
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public int getValueSetSize() {
		return activeValueSet.size();
	}

	@Override
	public Collection<T> getValueSet() {
		return finalValueSet;
	}
	
	@Override
	public Collection<T> getSelectedValueSet() {
		return activeValueSet;
	}

	@Override
	public boolean addValue(T value) {
		finalValueSet.add(value);
		return activeValueSet.add(value);
	}

	@Override
	public boolean removeValue(T value) {
		return activeValueSet.remove(value);
	}

	@Override
	public boolean hasValue(T value) {
		return activeValueSet.contains(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void resetValueSet() {
		activeValueSet = (TreeSet<T>) finalValueSet.clone();
	}

	@Override
	public String toString() {
		return label;
	}

}
