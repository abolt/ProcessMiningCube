package application.models.attribute;

import application.models.attribute.abstr.AbstrAttribute;
import application.models.attribute.abstr.Attribute;

public class TextAttribute extends AbstrAttribute<String>{

	private static final long serialVersionUID = 4335847962046185903L;

	public TextAttribute(String name, String type, Attribute<?> parent) {
		super(name, type, parent);
	}

}
