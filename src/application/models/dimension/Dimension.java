package application.models.dimension;

import javafx.collections.ObservableList;

public class Dimension {

	private ObservableList<Attribute> attributes;

	// sliced is a special kind of visibility: 
	// it does show in the list of
	// selected dimensions, and its filters do apply
	private boolean visible, sliced;
	
	private Attribute granularity;
	
	
}
