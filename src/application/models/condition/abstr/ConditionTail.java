package application.models.condition.abstr;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class ConditionTail {

	List<Pair<String, String>> conditions;

	public ConditionTail() {
		conditions = new ArrayList<Pair<String, String>>();
	}

	public List<Pair<String, String>> getTailConditions() {
		return conditions;
	}
	
	public void addCondition(String comparator, String value) {
		conditions.add(new ImmutablePair<String, String>(comparator, value));
	}

}
