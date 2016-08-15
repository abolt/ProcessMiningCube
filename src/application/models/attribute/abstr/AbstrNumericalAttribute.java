package application.models.attribute.abstr;

public abstract class AbstrNumericalAttribute<T extends Comparable<T>> extends AbstrAttribute<T> {

	private static final long serialVersionUID = -3317234921454229049L;

	protected T min, max;

	public AbstrNumericalAttribute(String name, String type, Attribute<?> parent) {
		super(name, type, parent);
	}

	public T getMin() {
		if (!activeValueSet.isEmpty())
			return activeValueSet.first();
		else
			return null;
	}

	public T getMax() {
		if (!activeValueSet.isEmpty())
			return activeValueSet.last();
		else
			return null;
	}

}
