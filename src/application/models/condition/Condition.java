package application.models.condition;

/**
 * This interface defines the functions that any condition has to have
 * @author abolt
 *
 */
public interface Condition {
	
	public final static String EQUALS = " = ", BIGGER_THAN = " > ", BIGGER_THAN_EQUALS = " >= ", SMALLER_THAN = " < ",
			SMALLER_THAN_EQUALS = " <= ", IN = " IN ", NOT_IN = " NOT IN ", BETWEEN = " BETWEEN ";
	
	public String getAsQueryString();
	
}
