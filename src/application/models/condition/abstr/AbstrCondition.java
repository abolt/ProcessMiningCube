package application.models.condition.abstr;

import java.util.Iterator;

import org.apache.commons.lang3.tuple.Pair;

import application.models.attribute.abstr.Attribute;

/**
 * This class is used to make sure that all conditions have an attribute.
 * 
 * @author abolt
 *
 * @param <T>:
 *            the type of attribute used to create the condition
 */
public abstract class AbstrCondition<T extends Attribute> implements Condition {

	protected T attribute;

	protected ConditionTail tail;

	protected AbstrCondition(T attribute, ConditionTail tail) {
		this.attribute = attribute;
		this.tail = tail;
	}

	@Override
	public T getAttribute() {
		return attribute;
	}

	public ConditionTail getTail() {
		return tail;
	}

	@Override
	public String getAsString() {
		String result = "(";
		Iterator<Pair<String, String>> iterator = tail.getTailConditions().iterator();
		while (iterator.hasNext()) {
			Pair<String, String> condition = iterator.next();
			result = result + attribute.getQueryString() + condition.getLeft() + condition.getRight();
			if (iterator.hasNext())
				result = result + " AND ";
		}
		result = result + ")";
		return result;
	}

	@Override
	public String getAsStringwithQuestionMarks() {
		String result = "(";
		Iterator<Pair<String, String>> iterator = tail.getTailConditions().iterator();
		while (iterator.hasNext()) {
			Pair<String, String> condition = iterator.next();
			result = result + attribute.getQueryString() + condition.getLeft() + "?";
			if (iterator.hasNext())
				result = result + " AND ";
		}
		result = result + ")";
		return result;
	}
	
	@Override
	public String getAsLabel(){
		String result = "(";
		Iterator<Pair<String, String>> iterator = tail.getTailConditions().iterator();
		while (iterator.hasNext()) {
			Pair<String, String> condition = iterator.next();
			result = result + attribute.toString() + condition.getLeft() + condition.getRight();
			if (iterator.hasNext())
				result = result + " AND ";
		}
		result = result + ")";
		return result;
	}

}
