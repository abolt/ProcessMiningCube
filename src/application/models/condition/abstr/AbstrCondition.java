package application.models.condition.abstr;

import application.models.attribute.abstr.Attribute;
import application.models.condition.Condition;

public abstract class AbstrCondition<T extends Attribute> implements Condition {

	protected T attribute;
	
	@Override
	public String getAsQueryString() {
		// TODO Auto-generated method stub
		return null;
	}

}
