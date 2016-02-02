package application.controllers.mapping;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import application.controllers.AbstractTabController;
import application.operations.io.Importer;
import application.operations.io.log.CSVImporter;
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

	private ObservableList<MappingRow> attributeObjects;

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
		String caseId = null, activityId = null, timeStamp = null, timestampSample = null;
		for (MappingRow row : attributeObjects) {
			if (row.getUseAs().contentEquals(CASE_ID))
				if (case_id)
					canProceed = false; // two case ids are not allowed
				else {
					case_id = true;
					caseId = row.getAttributeName();
				}
			if (row.getUseAs().contentEquals(ACTIVITY_ID))
				if (activity_id)
					canProceed = false; // two case ids are not allowed
				else {
					activity_id = true;
					activityId = row.getAttributeName();
				}

			if (row.getUseAs().contentEquals(TIMESTAMP))
				if (timestamp)
					canProceed = false; // two case ids are not allowed
				else {
					timestamp = true;
					@SuppressWarnings("unchecked")
					Iterator<String> iter = (Iterator<String>) row.getValueSet().iterator();
					timestampSample = iter.next();
					timeStamp = row.getAttributeName();
				}

		}
		if (!(case_id && activity_id && timestamp)) // if any is missing, cannot
													// proceed to next step
			canProceed = false;

		if (canProceed) {
			Importer importer = mainController.getImporter();

			if (importer instanceof CSVImporter) {
				((CSVImporter) importer).setCase_id(caseId);
				((CSVImporter) importer).setActivity_id(activityId);
				((CSVImporter) importer).setTimestamp(timeStamp);
				((CSVImporter) importer).setTimestampFormat((SimpleDateFormat) detectTimestampParser(timestampSample));
			}
			mainController.setMappingRows(attributeObjects);
			mainController.setLog(importer.importFromFile());

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

	private void setTable() {

		attributeObjects = mainController.getMappingRows();
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

		dimensionColumn.setCellFactory(CheckBoxTableCell.forTableColumn(dimensionColumn));
		dimensionColumn.setEditable(true);
		dimensionColumn.setCellValueFactory(new PropertyValueFactory<MappingRow, Boolean>("createDimension"));
		
		
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
			setTable();
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
