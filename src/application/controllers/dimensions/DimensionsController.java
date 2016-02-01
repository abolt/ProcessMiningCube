package application.controllers.dimensions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;

import application.controllers.AbstractTabController;
import application.controllers.mapping.MappingController;
import application.controllers.mapping.MappingRow;
import application.models.dimension.Attribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class DimensionsController extends AbstractTabController {

	@FXML
	private Tab tabDimensions;

	@FXML
	private ImageView image;

	@FXML
	Button createDimensionsButton;

	@FXML
	private ListView<Attribute> unusedAttributes;

	private Map<Attribute, Boolean> attributes;

	@FXML
	protected void initialize() {
		name = "dimensionsController";
	}

	public void poplateLists() {

		// create attributes from the log
		createAttributesFromLog();

		// populate used (boolean) attributes list
		ObservableList<Attribute> unused = FXCollections.observableArrayList();
		for (Attribute attribute : attributes.keySet())
			if (attributes.get(attribute) == false)
				unused.add(attribute);

		// check the existing dimensions
		unusedAttributes.setItems(unused);

		// if dimensions had to be created, create them with their corresponding
		// attributes!

	}

	public void updateLists() {

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

	@FXML
	protected void handleCreateDimensionsButton(ActionEvent event) {
		// do stuff here: create dimensions, pass list of dimensions to the
		// mainController
		setCompleted(true);
	}

	private void createAttributesFromLog() {
		attributes = new HashMap<Attribute, Boolean>();
		XLog log = mainController.getLog();

		Set<String> classes = new HashSet<String>();

		for (MappingRow m : mainController.getMappingRows()) {
			if (!m.getUseAs().equals(MappingController.IGNORE)) {
				Attribute attribute = new Attribute(m.getAttributeName(), getAttributeClass(m));
				for (XTrace t : log)
					for (XEvent e : t) {
						attribute.addValue(e.getAttributes().get(m.getAttributeName()));
						if (e.getAttributes().get(m.getAttributeName()) != null)
							classes.add(e.getAttributes().get(m.getAttributeName()).getClass().getSimpleName());
					}
				attributes.put(attribute, false);
			}
		}
		System.out.println(classes);
	}

	@SuppressWarnings("rawtypes")
	private Class getAttributeClass(MappingRow row) {
		switch (row.getUseAs()) {

		case MappingController.ACTIVITY_ID:
		case MappingController.CASE_ID:
		case MappingController.TIMESTAMP:
			for (XTrace t : mainController.getLog())
				for (XEvent e : t)
					if (e.getAttributes().get(row.getAttributeName()) != null)
						return e.getAttributes().get(row.getAttributeName()).getClass();

		case MappingController.TEXT:
			return XAttributeLiteralImpl.class;
		case MappingController.CONTINUOUS:
			return XAttributeContinuousImpl.class;
		case MappingController.DISCRETE:
			return XAttributeDiscreteImpl.class;
		case MappingController.DATE_TIME:
			return XAttributeTimestampImpl.class;
		}
		return null;
	}
}
