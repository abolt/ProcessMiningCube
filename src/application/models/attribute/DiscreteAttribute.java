package application.models.attribute;

import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;

public class DiscreteAttribute extends AbstrNumericalAttribute<Integer> {

	private static final long serialVersionUID = -3579913598266729578L;

	public DiscreteAttribute(String name, String type, Attribute<?> parent) {
		super(name, type, parent);
	}

	@Override
	public boolean addValue(String value) {
		if (value != null && !value.equalsIgnoreCase("null"))
			return addValue(Integer.parseInt(value));
		else
			return false;
	}

	public void setSelectedMin(Number newSelectedMin) {
		if (newSelectedMin instanceof Double)
			selectedMin = ((Double) newSelectedMin).intValue();
		else
			selectedMin = (int) newSelectedMin;
	}

	public void setSelectedMax(Number newSelectedMax) {
		if (newSelectedMax instanceof Double)
			selectedMax = ((Double) newSelectedMax).intValue();
		else
			selectedMax = (int) newSelectedMax;
	}
}
