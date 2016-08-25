package application.models.attribute;

import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.attribute.abstr.Attribute;

public class ContinuousAttribute extends AbstrNumericalAttribute<Double> {

	private static final long serialVersionUID = 4371396611802758036L;

	public ContinuousAttribute(String name, String type, Attribute parent) {
		super(name, type, parent);
	}
	
}
