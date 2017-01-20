package application.models.condition.impl;

import application.models.attribute.abstr.AbstrNumericalAttribute;
import application.models.condition.abstr.AbstrCondition;
import application.models.condition.abstr.ConditionTail;

public class NumericCondition extends AbstrCondition<AbstrNumericalAttribute<?>>{

	public NumericCondition(AbstrNumericalAttribute<?> attribute, ConditionTail tail) {
		super(attribute, tail);
	}
}
