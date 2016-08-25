package application.models.condition.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import application.models.condition.Condition;

/**
 * This class is used for conditions with multiple possible values e.g., ranges.
 * CompositeConditions have at least one condition.
 * 
 * All the conditions are united through OR clauses.
 * 
 * @author abolt
 *
 */
public class CompositeCondition implements Condition {

	List<ConditionImpl> conditionList;

	public CompositeCondition() {
		conditionList = new ArrayList<ConditionImpl>();
	}
	
	public void addCondition(ConditionImpl condition){
		conditionList.add(condition);
	}
	
	public List<ConditionImpl> getConditions(){
		return conditionList;
	}

	@Override
	public String getAsQueryString() {
		String result = "(";
		Iterator<ConditionImpl> iterator = conditionList.iterator();
		while (iterator.hasNext()) {
			Condition c = iterator.next();
			result = result + c.getAsQueryString();
			if(iterator.hasNext())
				result = result + " OR ";
		}
		result = result + ")";
		return result;
	}

}
