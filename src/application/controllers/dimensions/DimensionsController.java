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
import application.models.dimension.Dimension;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

public class DimensionsController extends AbstractTabController {

	@FXML
	private Tab tabDimensions;

	@FXML
	private ImageView image;

	@FXML
	Button createDimensionsButton;

	@FXML
	private ListView<Attribute> unusedAttributes, dimensionAttributes;

	@FXML
	private ListView<Dimension> dimensionList;

	private Map<String, Attribute> attributes;

	@FXML
	protected void initialize() {
		name = "dimensionsController";
	}

	public void populateLists() {

		// create attributes and dimensions from the log and the mapping step
		createAttributesFromLog();
		createDimensions();
		updateUnusedList();
		enableDragDrop();
		// unused attributes go to this list

	}

	public void updateLists() {

	}

	public void enableDragDrop() {

		unusedAttributes.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (unusedAttributes.getSelectionModel().getSelectedItem() == null) {
					return;
				}

				Dragboard dragBoard = unusedAttributes.startDragAndDrop(TransferMode.MOVE);
				ClipboardContent content = new ClipboardContent();
				content.putString(unusedAttributes.getSelectionModel().getSelectedItem().getAttributeName());
				dragBoard.setContent(content);
			}
		});

		dimensionAttributes.setOnDragOver(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dragEvent.acceptTransferModes(TransferMode.MOVE);
			}
		});

		dimensionAttributes.setOnDragDropped(new EventHandler<DragEvent>() {
			@Override
			public void handle(DragEvent dragEvent) {
				dimensionAttributes.getItems().add(unusedAttributes.getSelectionModel().getSelectedItem());
				unusedAttributes.getItems().remove(unusedAttributes.getSelectionModel().getSelectedItem());
				dimensionAttributes.refresh();
				unusedAttributes.refresh();
			}
		});
	}

	private void updateUnusedList() {
		ObservableList<Attribute> used = FXCollections.observableArrayList();
		for (Attribute a : attributes.values())
			for (Dimension d : mainController.getDimensions())
				if (d.getAttributes().contains(a))
					used.add(a);
		ObservableList<Attribute> unused = FXCollections.observableArrayList();
		for (Attribute a : attributes.values())
			if (!used.contains(a))
				unused.add(a);
		unusedAttributes.setItems(unused);
	}

	@Override
	public void initializeTab(Tab input) {
		tabDimensions = input;
	}

	@Override
	protected void enableTab(boolean value) {
		tabDimensions.setDisable(!value);
		if (value)
			populateLists();
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
		attributes = new HashMap<String, Attribute>();
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
				attributes.put(attribute.getAttributeName(), attribute);
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

	private void createDimensions() {

		ObservableList<Dimension> dimensions = FXCollections.observableArrayList();

		for (MappingRow m : mainController.getMappingRows()) {
			if (m.createDimensionProperty().getValue() && !m.getUseAs().equals(MappingController.IGNORE)) {
				Dimension newDimension = new Dimension(m.getAttributeName());
				newDimension.addAttribute(attributes.get(m.getAttributeName()));
				dimensions.add(newDimension);
			}
		}
		mainController.setDimensions(dimensions);

		dimensionList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Dimension>() {
			@Override
			public void changed(ObservableValue<? extends Dimension> observable, Dimension oldValue,
					Dimension newValue) {
				if(newValue != null)
					dimensionAttributes.setItems(newValue.getAttributes());
				else
					dimensionAttributes.setItems(null);
			}
		});
		dimensionList.setItems(mainController.getDimensions());

	}
}
