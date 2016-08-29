package application.models.condition;

import application.models.condition.abstr.AbstrCondition;
import application.models.condition.abstr.Condition;

public class ConditionUtils {

	/**
	 * This method simplifies the current conditions (remove unnecessary
	 * conditions). For example, if we have " > 2" and " > 5 " we only keep the
	 * second one.
	 */
	@SuppressWarnings("rawtypes")
	public static void simplifyConditions(AbstrCondition condition) {

	}

	/**
	 * Adds a constraint to the tail of the condition
	 * 
	 * @param condition
	 * @param comparator
	 * @param value
	 */
	public static void addConditionToTail(Condition condition, String comparator, String value) {
		if (isValid(comparator, value)) {
			condition.getTail().addCondition(comparator, value);
		}
	}

	/**
	 * This method validates if a condition tail is valid
	 */
	private static boolean isValid(String comparator, String value) {
		assert value != null && !value.isEmpty();

		switch (comparator) {
		case Condition.BETWEEN:
		case Condition.BIGGER_THAN:
		case Condition.BIGGER_THAN_EQUALS:
		case Condition.EQUALS:
		case Condition.IN:
		case Condition.NOT_IN:
		case Condition.SMALLER_THAN:
		case Condition.SMALLER_THAN_EQUALS:
			break;
		default:
			return false;
		}
		return true;
	}
}
