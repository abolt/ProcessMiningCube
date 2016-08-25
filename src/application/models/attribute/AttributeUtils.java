package application.models.attribute;

import application.models.attribute.abstr.Attribute;

public class AttributeUtils {

	@Override
	public void addChild(Attribute newAtt) {
		children.put(newAtt.getName(), newAtt);
	}
}
