package application.models.condition.abstr;

import application.models.attribute.abstr.Attribute;

/**
 * This interface defines the functions that any condition has to have
 * 
 * @author abolt
 *
 */
public interface Condition {

	public final static String EQUALS = " = ", BIGGER_THAN = " > ", BIGGER_THAN_EQUALS = " >= ", SMALLER_THAN = " < ",
			SMALLER_THAN_EQUALS = " <= ", IN = " IN ", NOT_IN = " NOT IN ", BETWEEN = " BETWEEN ";

	public String getAsString();
	
	public String getAsStringwithQuestionMarks();
	
	public Attribute getAttribute();
	
	public ConditionTail getTail();
	
	public String getAsLabel();

}
