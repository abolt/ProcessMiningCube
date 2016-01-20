package application.controllers.mapping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XTrace;

import application.controllers.AbstractTabController;
import application.models.dimension.Attribute;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MappingController extends AbstractTabController {

	public static final String IGNORE = "ignore", CASE_ID = "(event) case_id", ACTIVITY_ID = "(event) activity id",
			TIMESTAMP = "(event) timestamp", TEXT = "(other) text", DISCRETE = "(other) discrete number",
			CONTINUOUS = "(other) continuous number", DATE_TIME = "(other) date/time";

	public static final String[] categories = { IGNORE, CASE_ID, ACTIVITY_ID, TIMESTAMP, TEXT, DISCRETE, CONTINUOUS,
			DATE_TIME };

	private ObservableList<Attribute> attributeObjects = FXCollections.observableArrayList();

	@FXML
	private TableView<Attribute> mappingTable;

	@FXML
	private Tab tabMapping;

	@FXML
	private ImageView image;

	@FXML
	TableColumn<Attribute, String> attributeColumn, valueColumn, useColumn;
	@FXML
	TableColumn<Attribute, Boolean> dimensionColumn;

	@FXML
	public void initialize() {
		name = "mappingController";
	}

	@FXML
	protected void handleButton(ActionEvent event) {
		mainController.setAttributeObjects(attributeObjects);
		setCompleted(true);
	}

	private void updateTable() {
		// collecting data for the table
		Set<String> attributeSet = new HashSet<String>();
		for (XTrace trace : mainController.getLog().getXLog()) {
			for (XEvent event : trace) {
				attributeSet.addAll(event.getAttributes().keySet());
			}
		}

		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();
		for (String att : attributeSet) {

			Set<String> values = new HashSet<String>();
			int counter = 10;

			for (XTrace trace : mainController.getLog().getXLog()) {
				for (XEvent event : trace) {
					if (event.getAttributes().containsKey(att)
							&& !event.getAttributes().get(att).toString().isEmpty()) {
						// if it is a literal, number or date, parse accordingly
						values.add(event.getAttributes().get(att).toString());
						counter--;
					}
					if (counter == 0)
						break;
				}
				if (counter == 0)
					break;
			}
			attributes.put(att, values);
		}

		for (String att : attributes.keySet()) {
			attributeObjects.add(new Attribute(att, attributes.get(att), "ignore", false));
		}

		// setting up the columns
		attributeColumn.setCellValueFactory(new PropertyValueFactory<Attribute, String>("attributeName"));
		attributeColumn.setEditable(false);

		valueColumn.setCellValueFactory(new PropertyValueFactory<Attribute, String>("valueSet"));
		valueColumn.setEditable(false);

		useColumn.setCellValueFactory(new PropertyValueFactory<Attribute, String>("useAs"));
		useColumn.setEditable(true);
		useColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categories));
		useColumn.setOnEditCommit(new EventHandler<CellEditEvent<Attribute, String>>() {
			@Override
			public void handle(CellEditEvent<Attribute, String> t) {
				((Attribute) t.getTableView().getItems().get(t.getTablePosition().getRow())).setUseAs(t.getNewValue());
			}
		});

		dimensionColumn.setCellValueFactory(new PropertyValueFactory<Attribute, Boolean>("createDimension"));
		dimensionColumn.setEditable(true);
		dimensionColumn.setCellFactory(CheckBoxTableCell.forTableColumn(dimensionColumn));

		// set the table
		mappingTable.setEditable(true);
		mappingTable.setItems(attributeObjects);
	}

	public ObservableList<Attribute> getAttributeObjects() {
		return attributeObjects;
	}

	@Override
	protected void enableTab(boolean value) {
		tabMapping.setDisable(!value);
		if(value)
			updateTable();
	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/mapping_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/mapping_green.png"));
		else
			image.setImage(new Image("images/mapping_black.png"));

	}

	@Override
	public void initializeTab(Tab input) {
		tabMapping = input;
	}
}
