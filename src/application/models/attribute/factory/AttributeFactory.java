package application.models.attribute.factory;

import application.models.attribute.ContinuousAttribute;
import application.models.attribute.DateTimeAttribute;
import application.models.attribute.DiscreteAttribute;
import application.models.attribute.TextAttribute;
import application.models.attribute.abstr.Attribute;

public class AttributeFactory {

	public static Attribute<?> createAtttribute(String name, String type) {
		
		if (type.equals(Attribute.CONTINUOUS))
			return new ContinuousAttribute(name, type, null);
		
		else if (type.equals(Attribute.DISCRETE))
			return new DiscreteAttribute(name, type, null);
		
		else if (type.equals(Attribute.DATE_TIME))
			return new DateTimeAttribute(name, type, null);
		
		else
			// if(type.equals(Attribute.TEXT))
			return new TextAttribute(name, type, null);
	}

}
