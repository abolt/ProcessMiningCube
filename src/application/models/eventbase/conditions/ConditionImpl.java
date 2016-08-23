package application.models.eventbase.conditions;

import java.util.ArrayList;
import java.util.List;

import application.models.attribute.abstr.Attribute;

public class ConditionImpl implements Condition{

	public final static String EQUALS = " = ", BIGGER_THAN = " > ", BIGGER_THAN_EQUALS = " >= ", SMALLER_THAN = " < ",
			SMALLER_THAN_EQUALS = " <= ", LIKE = " like ";

	private final List<String> validComparators = new ArrayList<String>();

	private Attribute<?> attribute;
	private String comparator, value;

	private void initializeList() {
		validComparators.add(EQUALS);
		validComparators.add(BIGGER_THAN);
		validComparators.add(BIGGER_THAN_EQUALS);
		validComparators.add(SMALLER_THAN);
		validComparators.add(SMALLER_THAN_EQUALS);
		validComparators.add(LIKE);
	}

	public ConditionImpl(Attribute<?> attribute, String comparator, String value) {
		initializeList();
		this.attribute = attribute;
		this.value = value;
		if (validComparators.contains(comparator))
			this.comparator = comparator;
	}

	public Attribute<?> getAttribute() {
		return attribute;
	}

	public String getComparator() {
		return comparator;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String getAsQueryString() {
		return "\"" + attribute.getQueryString() + "\"" + comparator + "'" + value + "'";
	}
}
