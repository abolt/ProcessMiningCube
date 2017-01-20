package application.models.condition.factory;

import application.models.attribute.abstr.Attribute;
import application.models.attribute.impl.ContinuousAttribute;
import application.models.attribute.impl.DiscreteAttribute;
import application.models.attribute.impl.TextAttribute;
import application.models.condition.abstr.Condition;
import application.models.condition.abstr.ConditionTail;
import application.models.condition.impl.NumericCondition;
import application.models.condition.impl.TextCondition;

public class ConditionFactory {

	public static Condition createCondition(Attribute attribute){
		Condition result = null;
		if(attribute instanceof TextAttribute)
			result = new TextCondition((TextAttribute) attribute, new ConditionTail());
		else if (attribute instanceof DiscreteAttribute)
			result = new NumericCondition((DiscreteAttribute) attribute, new ConditionTail());
		else if (attribute instanceof ContinuousAttribute)
			result = new NumericCondition((ContinuousAttribute) attribute, new ConditionTail());
		return result;
	}
}
