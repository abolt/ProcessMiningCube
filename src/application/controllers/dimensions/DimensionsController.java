package application.controllers.dimensions;

import java.util.HashMap;
import java.util.Map;

import application.controllers.AbstractTabController;
import application.controllers.mapping.Attribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DimensionsController extends AbstractTabController {

	@FXML
	private Tab tabDimensions;

	@FXML
	private ImageView image;
	
	@FXML private ListView<Attribute> unusedAttributes;
	
	private Map<Attribute,Boolean> usedAttMap;

	@FXML
	protected void initialize() {
		name = "dimensionsController";
	}

	public void poplateLists() {
		
		//populate unused attributes
		usedAttMap = new HashMap<Attribute,Boolean>();
		for (Attribute attribute : mainController.getAttributeObjects())
			usedAttMap.put(attribute, false);
		
		ObservableList<Attribute> unused = FXCollections.observableArrayList();
		
		//check the existing dimensions
		unusedAttributes.setItems(unused);
		
		//if dimensions had to be created, create them with their corresponding attributes!
		
	}
	
	public void updateLists(){
		
	}

	@Override
	public void initializeTab(Tab input) {
		tabDimensions = input;
	}

	@Override
	protected void enableTab(boolean value) {
		tabDimensions.setDisable(!value);
		if (value)
			poplateLists();
	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/dimension_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/dimension_green.png"));
		else
			image.setImage(new Image("images/dimension_black.png"));

	}

}
