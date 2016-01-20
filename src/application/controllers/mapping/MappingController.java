package application.controllers.mapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import application.controllers.AbstractTabController;
import application.models.eventlog.CSVFile;
import application.models.eventlog.EventLog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

	private ObservableList<MappingRow> attributeObjects = FXCollections.observableArrayList();

	@FXML
	private TableView<MappingRow> mappingTable;

	@FXML
	private Tab tabMapping;

	@FXML
	private ImageView image;

	@FXML
	TableColumn<MappingRow, String> attributeColumn, valueColumn, useColumn;
	@FXML
	TableColumn<MappingRow, Boolean> dimensionColumn;

	@FXML
	public void initialize() {
		name = "mappingController";
	}

	@FXML
	protected void handleButton(ActionEvent event) {
		// we need at least a case id, an activity id and a timestamp.
		boolean case_id = false, activity_id = false, timestamp = false;
		boolean canProceed = true;
		for (MappingRow row : attributeObjects) {
			if (row.getUseAs().contentEquals(CASE_ID))
				if (case_id)
					canProceed = false; // two case ids are not allowed
				else
					case_id = true;
			if (row.getUseAs().contentEquals(ACTIVITY_ID))
				if (activity_id)
					canProceed = false; // two case ids are not allowed
				else
					activity_id = true;
			if (row.getUseAs().contentEquals(TIMESTAMP))
				if (timestamp)
					canProceed = false; // two case ids are not allowed
				else
					timestamp = true;
		}
		if (!(case_id && activity_id && timestamp)) // if any is missing, cannot
													// proceed to next step
			canProceed = false;

		if (canProceed) {
			mainController.setAttributeObjects(attributeObjects);
			setCompleted(true);
		} else {
			if (case_id)
				selectionErrorMessage("You cannot select two case ids!");
			else if (activity_id)
				selectionErrorMessage("You cannot select two activity ids!");
			else if (timestamp)
				selectionErrorMessage("You cannot select two timestamps!");
			else
				selectionErrorMessage("You need to select one:\n- case_id\n- activity_id\n- timestamp");
		}

	}

	private void updateTable() {

		// create a set of attribute names
		Set<String> attributeNamesSet = new HashSet<String>();
		Map<String, Set<String>> attributes = new HashMap<String, Set<String>>();

		@SuppressWarnings("rawtypes")
		EventLog eventLog = mainController.getLog();

		// if XES
		if (eventLog.getEventLog() instanceof XLog) {
			XLog log = (XLog) eventLog.getEventLog();
			// defines the set of attributes
			for (XTrace trace : log) {
				for (XEvent event : trace) {
					attributeNamesSet.addAll(event.getAttributes().keySet());
				}
			}
			// create sample value sets and the corresponding MappingRows
			int counter = 0;
			for (String att : attributeNamesSet) {
				Set<String> values = new HashSet<String>();
				counter = 5;
				for (XTrace trace : log) {
					if (counter == 0)
						break;
					for (XEvent event : trace) {
						if (counter == 0)
							break;
						if (event.getAttributes().containsKey(att)
								&& !event.getAttributes().get(att).toString().isEmpty())
							if (!values.contains(event.getAttributes().get(att).toString())) {
								values.add(event.getAttributes().get(att).toString());
								counter--;
							}
					}
				}
				attributes.put(att, values);
			}
		}
		// if CSV
		else if (eventLog.getEventLog() instanceof CSVFile) {
			
			//do here!!!!

		}
		// finally, create the MappingRows
		for (String att : attributes.keySet()) {
			attributeObjects.add(new MappingRow(att, attributes.get(att), "ignore", false));
		}
		setTable();
	}

	private void setTable() {
		// setting up the columns
		attributeColumn.setCellValueFactory(new PropertyValueFactory<MappingRow, String>("attributeName"));
		attributeColumn.setEditable(false);

		valueColumn.setCellValueFactory(new PropertyValueFactory<MappingRow, String>("valueSet"));
		valueColumn.setEditable(false);

		useColumn.setCellValueFactory(new PropertyValueFactory<MappingRow, String>("useAs"));
		useColumn.setEditable(true);
		useColumn.setCellFactory(ComboBoxTableCell.forTableColumn(categories));
		useColumn.setOnEditCommit(new EventHandler<CellEditEvent<MappingRow, String>>() {
			@Override
			public void handle(CellEditEvent<MappingRow, String> t) {
				// check if it is parseable, if not show an alert and do not
				MappingRow row = ((MappingRow) t.getTableView().getItems().get(t.getTablePosition().getRow()));
				if (parseSample(t.getNewValue(), row.getValueSet()))
					row.setUseAs(t.getNewValue());
				else {
					parseErrorMessage();
					// row.setUseAs(t.getOldValue());
					t.getTableView().refresh();
				}
			}
		});

		dimensionColumn.setCellValueFactory(new PropertyValueFactory<MappingRow, Boolean>("createDimension"));
		dimensionColumn.setEditable(true);
		dimensionColumn.setCellFactory(CheckBoxTableCell.forTableColumn(dimensionColumn));

		// set the table
		mappingTable.setEditable(true);
		mappingTable.setItems(attributeObjects);
	}

	public ObservableList<MappingRow> getAttributeObjects() {
		return attributeObjects;
	}

	@Override
	protected void enableTab(boolean value) {
		tabMapping.setDisable(!value);
		if (value)
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

	private boolean parseSample(String choice, Set<?> values) {

		if (choice.contentEquals(DATE_TIME) || choice.contentEquals(TIMESTAMP)) {
			Iterator<?> it = values.iterator();
			if (it.hasNext()) {
				DateFormat df = detectTimestampParser((String) it.next());
				while (it.hasNext()) {
					try {
						df.parse((String) it.next());
					} catch (Exception e) {
						return false;
					}
				}
				return true;
			}
			return false;

		} else if (choice.contentEquals(CONTINUOUS)) {

		} else if (choice.contentEquals(DISCRETE)) {

		}
		// if it is text, then allow always
		return true;

	}

	protected void selectionErrorMessage(String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Selection Error");
		alert.setContentText(content);
		alert.showAndWait();
	}

	protected void parseErrorMessage() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Parsing Error");
		alert.setContentText("The attribute cannot be parsed to this type!");
		alert.showAndWait();
	}

	private DateFormat detectTimestampParser(String input) {
		DateFormat df;
		Date aux = null;
		try {
			df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}

		try {
			df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (ParseException e) {
		}
		return null;
	}
}
