package application.controllers.materialize;

import java.util.HashMap;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import application.controllers.AbstractTabController;
import application.controllers.cube.CubeController;
import application.controllers.mapping.MappingController;
import application.controllers.mapping.MappingRow;
import application.controllers.materialize.miniviews.CaseDistributionController;
import application.controllers.materialize.miniviews.DimensionValuesController;
import application.controllers.materialize.miniviews.MiniViewControllerInterface;
import application.models.cube.Cell;
import application.models.cube.Cube;
import application.models.dimension.Attribute;
import application.models.dimension.Dimension;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class MaterializeController extends AbstractTabController {

	public static final String DIMENSIONAL_VALUES = "Dimensional Values", CASE_DISTRIBUTION = "Case Distribution",
			LOG_METRICS = "Log Metrics";
	@FXML
	private Tab tabMaterialize;

	private Cube cube;

	@FXML
	private ImageView image;

	@FXML
	private TilePane tilePane;

	@FXML
	private ComboBox<String> miniViewSelection, primarySort, secondarySort;

	@FXML
	private TextField selectionCount;

	@FXML
	protected void initialize() {
		name = "materializeController";
	}

	@Override
	public void initializeTab(Tab input) {
		tabMaterialize = input;

	}

	@Override
	protected void enableTab(boolean value) {
		tabMaterialize.setDisable(!value);
		if (value) {
			materializeCells();

			ObservableList<String> elements = FXCollections.observableArrayList();
			elements.addAll(DIMENSIONAL_VALUES, CASE_DISTRIBUTION, LOG_METRICS);
			miniViewSelection.setItems(elements);
			miniViewSelection.getSelectionModel().select(DIMENSIONAL_VALUES);

			initializeListeners();

			setCellVisualizers(DIMENSIONAL_VALUES);

		}

	}

	public void initializeListeners() {

		miniViewSelection.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				setCellVisualizers(newValue);
			}
		});
	}

	@Override
	public void updateImage() {
		if (isEnabled() && !isCompleted())
			image.setImage(new Image("images/gear_red.png"));
		else if (isEnabled() && isCompleted())
			image.setImage(new Image("images/gear_green.png"));
		else
			image.setImage(new Image("images/gear_black.png"));
	}

	@FXML
	protected void handleVisualizeCellsButton(ActionEvent event) {

		if (selectionCount.getText() != null && !selectionCount.getText().equals("0"))
			setCompleted(true);
		else
			selectionMessage();
	}

	private void materializeCells() {
		cube = new Cube(mainController.getDimensions(), mainController.getSelectedValues());

		// find the combinations of selected values for the visible dimensions
		createCells();

		// assign the corresponding events or traces to it.
		fillCells();

		// gives the cube to the mainController
		mainController.setCube(cube);

	}

	private void setCellVisualizers(String rendererName) {
		tilePane.getChildren().clear();

		for (Cell cell : cube.getCells()) {
			switch (rendererName) {
			case DIMENSIONAL_VALUES:
				DimensionValuesController dimElement = new DimensionValuesController(cell, this);
				dimElement.initializeValues();
				tilePane.getChildren().add(dimElement);
				break;
			case CASE_DISTRIBUTION:
				CaseDistributionController caseElement = new CaseDistributionController(cell, this);
				caseElement.initializeValues();
				tilePane.getChildren().add(caseElement);
				break;
			case LOG_METRICS:
			}
		}
	}

	private void createCells() {
		ObservableList<Dimension> dimensionAux = FXCollections.observableArrayList();
		dimensionAux.addAll(cube.getDimensions());

		ObservableList<Cell> cellAux = FXCollections.observableArrayList();
		cellAux.addAll(cube.getCells());

		cube.setCells(recursiveCellCreation(dimensionAux, cellAux));

		cellAux.clear();
	}

	private ObservableList<Cell> recursiveCellCreation(ObservableList<Dimension> dimensions,
			ObservableList<Cell> input) {
		// only for diced dimensions

		if (input == null)
			input = FXCollections.observableArrayList();
		if (dimensions.size() == 0)
			return input;
		else if (!dimensions.get(0).isVisible() || dimensions.get(0).isSliced()) {
			dimensions.remove(0);
			return recursiveCellCreation(dimensions, input);
		} else {
			ObservableList<Cell> output = FXCollections.observableArrayList();
			if (input.isEmpty()) {
				for (CheckBoxTreeItem<XAttribute> a : cube.getValueSelections()
						.get(dimensions.get(0).getGranularity())) {
					if (a.isSelected()) {
						Cell cell = new Cell(null, mainController.getLog());
						cell.addValue(a.getValue(), dimensions.get(0).toString());
						output.add(cell);
					}
				}
			} else {
				for (Cell cell : input) {
					for (CheckBoxTreeItem<XAttribute> a : cube.getValueSelections()
							.get(dimensions.get(0).getGranularity())) {
						if (a.isSelected()) {
							if (cell.hasAttribute(a.getValue().getKey())) {
								Cell c = new Cell(null, mainController.getLog());
								for (XAttribute att : cell.getDimensionalValues().values())
									c.addValue(att, cell.getDimension(att.getKey()));
								c.addValue(a.getValue(), dimensions.get(0).toString());
								output.add(c);
							} else {
								cell.addValue(a.getValue(), dimensions.get(0).toString());
								output.add(cell);
							}
						}
					}
				}
			}

			dimensions.remove(0);
			return recursiveCellCreation(dimensions, output);
		}
	}

	private void fillCells() {
		// get how XElements are distributed into cells
		String selection = mainController.getDistributionModel();

		XLog log = mainController.getLog();

		ObservableList<Cell> cells = FXCollections.observableArrayList();
		for (Cell cell : cube.getCells())
			cells.add(cell);

		// for each, obtain a list of events or traces to be distributed, check
		// slice and dice filters

		// then distribute those XElements into the cells
		switch (selection) {
		case CubeController.EVENTS:
			// by event
			for (XTrace trace : log)
				for (XEvent event : trace)
					if (!isFiltered(event))
						for (Cell cell : cells) {
							cell.addElement(event);
							System.out.println("h");
						}
			// now for each cell, create traces if they werent there (only for
			// event distribution
			for (Cell cell : cells)
				createTraces(cell);
			break;
		case CubeController.TRACES_FIRST:
			// by trace (considering only first event)
			for (XTrace trace : log) {
				if (!trace.isEmpty()) {
					XEvent event = trace.get(0);
					if (!isFiltered(event))
						for (Cell cell : cells)
							cell.addElement(trace, event);
				}
			}

			break;
		case CubeController.TRACES_ANY:
			// by trace (considering any event)
			for (XTrace trace : log)
				for (XEvent event : trace)
					if (!isFiltered(event))
						for (Cell cell : cells) {
							cell.addElement(trace);
							break;
						}
			break;
		}

	}

	private boolean isFiltered(XEvent event) {
		for (XAttribute attribute : event.getAttributes().values())
			for (Dimension dimension : cube.getDimensions())
				if (dimension.isVisible() && dimension.hasAttribute(attribute.getKey())) {
					Attribute a = dimension.getAttribute(attribute.getKey());
					if (a.hasValue(attribute) && !a.isSelected(attribute.toString())) {
						return true;
					}
				}
		return false;
	}

	private void createTraces(Cell cell) {

		XLog log = cell.getLog();
		HashMap<String, XTrace> traces = new HashMap<String, XTrace>();
		XFactory xfactory = new XFactoryBufferedImpl();

		String case_id_name = null;
		for (MappingRow row : mainController.getMappingRows())
			if (row.getUseAs().equals(MappingController.CASE_ID))
				case_id_name = row.getAttributeName();

		XAttributeMap att;
		for (XEvent e : cell.getEvents()) {
			XAttribute a = e.getAttributes().get(case_id_name);
			if (traces.containsKey(a.toString()))
				traces.get(a.toString()).add(e);
			else {
				att = xfactory.createAttributeMap();
				att.put(case_id_name, xfactory.createAttributeLiteral(case_id_name, a.toString(), null));
				XTrace t = xfactory.createTrace(att);
				t.add(e);
				traces.put(a.toString(), t);
			}
		}

		log.addAll(traces.values());
	}

	/**
	 * @author abolt Button handlers
	 */
	@FXML
	public void exportSelected(ActionEvent event) {

	}

	@FXML
	public void selectAll(ActionEvent event) {
		for (Node node : tilePane.getChildren())
			if (node instanceof MiniViewControllerInterface)
				((MiniViewControllerInterface) node).setSelected(true);
		updateSelectionCount();
	}

	@FXML
	public void selectNone(ActionEvent event) {
		for (Node node : tilePane.getChildren())
			if (node instanceof MiniViewControllerInterface)
				((MiniViewControllerInterface) node).setSelected(false);
		updateSelectionCount();
	}

	@FXML
	public void invertSelection(ActionEvent event) {
		for (Node node : tilePane.getChildren())
			if (node instanceof MiniViewControllerInterface)
				((MiniViewControllerInterface) node)
						.setSelected(!((MiniViewControllerInterface) node).getCell().isSelected());
		updateSelectionCount();
	}

	public void updateSelectionCount() {
		int counter = 0;
		for (Cell cell : cube.getCells())
			if (cell.isSelected())
				counter++;
		selectionCount.setText(Integer.toString(counter));
	}

	protected void selectionMessage() {
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Error");
		alert.setHeaderText("No cells selected!");
		alert.setContentText("You have to select at least one cell in order to proceed to the next step.");
		alert.showAndWait();
	}
}
