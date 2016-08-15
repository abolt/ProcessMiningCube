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

	protected String name;
	protected String type;
	protected Attribute<?> parent;
	protected Map<String,Attribute<?>> children;
	protected TreeSet<T> activeValueSet;
	protected final TreeSet<T> finalValueSet;

	public AbstrAttribute(String name, String type, Attribute<?> parent) {
		this.name = name;
		this.type = type;
		this.parent = parent;
		this.children = new HashMap<String,Attribute<?>>();
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
	
	public void addChild(Attribute<?> newAtt){
		children.put(newAtt.getAttributeName(), newAtt);
	}

	@Override
	public String getAttributeName() {
		return name;
	}

	@Override
	public String getAttributeType() {
		return type;
	}
	
	public int getValueSetSize() {
		return activeValueSet.size();
	}

	@Override
	public Collection<T> getValueSet() {
		return activeValueSet;
	}
	
	@Override
	public boolean addValue(T value) {
		finalValueSet.add(value);
		return activeValueSet.add(value);
	}
	
	@Override
	public boolean removeValue(T value){
		return activeValueSet.remove(value);
	}

	@Override
	public boolean hasValue(T value){
		return activeValueSet.contains(value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void resetValueSet(){
		activeValueSet = (TreeSet<T>) finalValueSet.clone();
	}
	
	@Override
	public String toString(){
		return name;
	}

}
