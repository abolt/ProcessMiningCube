package application.models.condition.impl;

import application.models.attribute.impl.TextAttribute;
import application.models.condition.abstr.AbstrCondition;
import application.models.condition.abstr.ConditionTail;

public class TextCondition extends AbstrCondition<TextAttribute>{

	public TextCondition(TextAttribute attribute, ConditionTail tail) {
		super(attribute, tail);
	}
}
