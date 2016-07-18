package application.models.eventbase;

import java.util.ArrayList;
import java.util.List;

import application.models.dimension.Attribute;
import javafx.util.Pair;

public class ConditionSet {

	private List<Pair<Attribute, String>> conditions;

	public ConditionSet() {
		conditions = new ArrayList<Pair<Attribute, String>>();
	}

	public ConditionSet(List<Pair<Attribute, String>> conditions) {
		this.conditions = conditions;
	}
	
	public void addCondition(Attribute attribute, String value){
		conditions.add(new Pair<Attribute,String>(attribute,value));
	}
	
	public List<Pair<Attribute, String>> getConditions(){
		return conditions;
	}

}