package application.models.attribute;

import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;

public class ContinuousAttribute extends AbstrNumericalAttribute<Double> {

	private static final long serialVersionUID = 4371396611802758036L;

	public ContinuousAttribute(String name, String type, Attribute<?> parent) {
		super(name, type, parent);
	}

//	@Override
//	public boolean addValue(String value) {
//		if (value != null && !value.equalsIgnoreCase("null"))
//			return addValue(Double.parseDouble(value));
//		else
//			return false;
//	}
	
	public void setSelectedMin(Number newSelectedMin) {
		selectedMin = (Double) newSelectedMin;
	}
	
	public void setSelectedMax(Number newSelectedMax) {
		selectedMax = (Double) newSelectedMax;
	}

}
