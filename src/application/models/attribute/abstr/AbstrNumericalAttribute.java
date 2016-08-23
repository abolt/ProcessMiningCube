package application.models.attribute.abstr;

public abstract class AbstrNumericalAttribute<T extends Comparable<T>> extends AbstrAttribute<T> {

	private static final long serialVersionUID = -3317234921454229049L;

	protected T selectedMin, selectedMax;

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

	public T getSelectedMin() {
		if (selectedMin != null)
			return selectedMin;
		else
			return getMin();
	}

	public T getSelectedMax() {
		if (selectedMax != null)
			return selectedMax;
		else
			return getMax();
	}
	
	public abstract void setSelectedMin(Number newSelectedMin);
	public abstract void setSelectedMax(Number newSelectedMax);
}
