package application.controllers.wizard.steps;

import java.io.File;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.Set;

import application.controllers.wizard.CubeWizardController;
import application.controllers.wizard.abstr.AbstractWizardStepController;
import application.models.attribute.abstr.Attribute;
import application.models.wizard.MappingRow;
import application.operations.io.Importer;
import application.operations.io.log.CSVImporter;
import application.operations.io.log.XESImporter;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

public class MappingController extends AbstractWizardStepController {

	private static final String viewLocation = "/application/views/wizard/Mapping.fxml";
	private ObservableList<MappingRow> attributeObjects;

	public static final String[] categories = { Attribute.IGNORE, Attribute.TEXT, Attribute.DISCRETE,
			Attribute.CONTINUOUS, Attribute.DATE_TIME };

	@FXML
	private TableView<MappingRow> table;

	@FXML
	private TableColumn<MappingRow, String> colAttribute, colExampleValues, colAttributeType;

	public MappingController(CubeWizardController controller) {
		super(controller, viewLocation);
		initializeImporter();
		setTable();

	}

	public void initializeImporter() {
		Importer importer = null;
		File input = ((ImportDataController) (mainController.getNode(0))).getFile();
		if (input.getName().endsWith(ImportDataController.CSV))
			importer = new CSVImporter(input);
		else
			importer = new XESImporter(input);

		attributeObjects = importer.getSampleList();
		parseAttributes();
	}

	private void parseAttributes() {
		for (MappingRow row : attributeObjects) {

			boolean isParseable = false;
			/**
			 * First check if the value is a timestamp
			 */
			for (Object value : row.getValueSet()) {
				if (detectTimestampParser((String) value) != null)
					isParseable = true;
				else {
					isParseable = false;
					break;
				}
			}
			if (isParseable) {
				row.setUseAs(Attribute.DATE_TIME);
				continue;
			}

			/**
			 * Check if the value is a discrete number
			 */
			for (Object value : row.getValueSet()) {
				try {
					Integer.parseInt((String) value);
					isParseable = true;
				} catch (NumberFormatException e) {
					isParseable = false;
					break;
				}

			}
			if (isParseable) {
				row.setUseAs(Attribute.DISCRETE);
				continue;
			}

			/**
			 * Check if the value is a decimal number
			 */
			for (Object value : row.getValueSet()) {
				try {
					Double.parseDouble((String) value);
					isParseable = true;
				} catch (Exception e) {
					isParseable = false;
					break;
				}

			}
			if (isParseable) {
				row.setUseAs(Attribute.CONTINUOUS);
				continue;
			}

			/**
			 * If is not any of the above, we consider it as text
			 */
			row.setUseAs(Attribute.TEXT);
		}
	}

	private void setTable() {

		// setting up the columns
		colAttribute.setCellValueFactory(new PropertyValueFactory<MappingRow, String>("attributeName"));
		colAttribute.setEditable(false);

		colExampleValues.setCellValueFactory(new PropertyValueFactory<MappingRow, String>("valueSet"));
		colExampleValues.setEditable(false);

		colAttributeType.setCellValueFactory(new PropertyValueFactory<MappingRow, String>("useAs"));
		colAttributeType.setEditable(true);
		colAttributeType.setCellFactory(ComboBoxTableCell.forTableColumn(categories));
		colAttributeType.setOnEditCommit(new EventHandler<CellEditEvent<MappingRow, String>>() {
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

		// set the table
		table.setEditable(true);
		table.getSelectionModel().cellSelectionEnabledProperty().set(true);
		table.setItems(attributeObjects);
	}

	private boolean parseSample(String choice, Set<String> values) {

		if (choice.contentEquals(Attribute.DATE_TIME)) {
			Iterator<String> it = values.iterator();
			if (it.hasNext()) {
				DateFormat df = detectTimestampParser(it.next());
				while (it.hasNext()) {
					try {
						df.parse(it.next());
					} catch (Exception e) {
						return false;
					}
				}
				return true;
			}
			return false;

		} else if (choice.contentEquals(Attribute.CONTINUOUS)) {

		} else if (choice.contentEquals(Attribute.DISCRETE)) {

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

	public static DateFormat detectTimestampParser(String input) {
		DateFormat df;
		Date aux = null;
		try {
			df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("yyyy/MM/dd'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("dd/MM/yyyy'T'HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("dd-MM-yyyy HH:mm");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}

		try {
			df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			aux = df.parse(input);
			aux.getTime();
			return df;
		} catch (Exception e) {
		}
		return null;
	}

	@FXML
	protected void handleBackButton() {
		mainController.backStep();
	}

	@FXML
	protected void handleNextButton() {
		boolean isOK = false;
		for (MappingRow row : attributeObjects) {
			if (!row.getUseAs().equals(Attribute.IGNORE)) {
				isOK = true;
				break;
			}
		}
		if (isOK)
			mainController.nextStep();
		else
			selectionErrorMessage("No attributes selected",
					"No attributes are used, please choose to use at least one attribute (set to anything except \""
							+ Attribute.IGNORE + "\")");
	}

	public ObservableList<MappingRow> getMappingRows() {
		return attributeObjects;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}

}
