//package application.controllers.materialize;
//
//import java.util.Comparator;
//import java.util.HashMap;
//
//import org.deckfour.xes.factory.XFactory;
//import org.deckfour.xes.factory.XFactoryBufferedImpl;
//import org.deckfour.xes.model.XAttribute;
//import org.deckfour.xes.model.XAttributeMap;
//import org.deckfour.xes.model.XEvent;
//import org.deckfour.xes.model.XLog;
//import org.deckfour.xes.model.XTrace;
//
//import application.controllers.AbstractTabController;
//import application.controllers.cube.CubeController;
//import application.controllers.mapping.MappingController;
//import application.controllers.materialize.miniviews.CaseDistributionController;
//import application.controllers.materialize.miniviews.DimensionValuesController;
//import application.controllers.materialize.miniviews.MiniViewControllerInterface;
//import application.models.cube.Cell;
//import application.models.cube.Cell.Metrics;
//import application.models.cube.Cube;
//import application.models.dimension.Attribute;
//import application.models.dimension.Dimension;
//import application.models.wizard.MappingRow;
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.scene.Node;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.control.CheckBox;
//import javafx.scene.control.CheckBoxTreeItem;
//import javafx.scene.control.ComboBox;
//import javafx.scene.control.Tab;
//import javafx.scene.control.TextField;
//import javafx.scene.image.Image;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.TilePane;
//
//public class MaterializeController extends AbstractTabController {
//
//	public static final String DIMENSIONAL_VALUES = "Dimensional Values", CASE_DISTRIBUTION = "Case Distribution",
//			LOG_METRICS = "Log Metrics";
//
//	public static final String NONE = "not sorted", CASES = "# of cases", EVENTS = "# of events",
//			CASE_SIZE = " case size (# ev)", CASE_DURATION = " case duration (sec)", ENTROPY = "log entropy";
//	@FXML
//	private Tab tabMaterialize;
//
//	private Cube cube;
//
//	@FXML
//	private ImageView image;
//
//	@FXML
//	private TilePane tilePane;
//
//	@FXML
//	private ComboBox<String> miniViewSelection, primarySort;
//
//	@FXML
//	private CheckBox showOnlySelected, showEmpty;
//
//	@FXML
//	private TextField selectionCount;
//
//	@FXML
//	protected void initialize() {
//		name = "materializeController";
//	}
//
//	@Override
//	public void initializeTab(Tab input) {
//		tabMaterialize = input;
//
//	}
//
//	@Override
//	protected void enableTab(boolean value) {
//		tabMaterialize.setDisable(!value);
//		if (value) {
//			showOnlySelected.setSelected(false);
//			showEmpty.setSelected(true);
//			materializeCells();
//
//			ObservableList<String> miniviewElements = FXCollections.observableArrayList();
//			miniviewElements.addAll(DIMENSIONAL_VALUES, CASE_DISTRIBUTION, LOG_METRICS);
//			miniViewSelection.setItems(miniviewElements);
//			miniViewSelection.getSelectionModel().select(DIMENSIONAL_VALUES);
//
//			ObservableList<String> sortElements = FXCollections.observableArrayList();
//			sortElements.addAll(NONE, CASES, EVENTS, CASE_SIZE, CASE_DURATION, ENTROPY);
//			primarySort.setItems(sortElements);
//			primarySort.getSelectionModel().select(NONE);
//
//			initializeListeners();
//
//			setCellVisualizers();
//
//		}
//
//	}
//
//	public void initializeListeners() {
//
//		miniViewSelection.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				setCellVisualizers();
//			}
//		});
//		primarySort.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
//			@Override
//			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
//				setCellVisualizers();
//			}
//		});
//		showOnlySelected.selectedProperty().addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//				setCellVisualizers();
//			}
//		});
//		showEmpty.selectedProperty().addListener(new ChangeListener<Boolean>() {
//			@Override
//			public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
//				setCellVisualizers();
//			}
//		});
//	}
//
//	@Override
//	public void updateImage() {
//		if (isEnabled() && !isCompleted())
//			image.setImage(new Image("images/gear_red.png"));
//		else if (isEnabled() && isCompleted())
//			image.setImage(new Image("images/gear_green.png"));
//		else
//			image.setImage(new Image("images/gear_black.png"));
//	}
//
//	@FXML
//	protected void handleVisualizeCellsButton(ActionEvent event) {
//
//		if (selectionCount.getText() != null && !selectionCount.getText().equals("0"))
//			setCompleted(true);
//		else
//			selectionMessage();
//	}
//
//	private void materializeCells() {
//		cube = new Cube(mainController.getDimensions(), mainController.getSelectedValues());
//
//		// find the combinations of selected values for the visible dimensions
//		createCells();
//
//		// assign the corresponding events or traces to it.
//		fillCells();
//
//		// gives the cube to the mainController
//		mainController.setCube(cube);
//
//	}
//
//	private void setCellVisualizers() {
//		tilePane.getChildren().clear();
//
//		// add content
//		String rendererName = miniViewSelection.getSelectionModel().getSelectedItem();
//		switch (rendererName) {
//		case DIMENSIONAL_VALUES:
//			for (Cell cell : cube.getCells()) {
//				if (isCellShowable(cell)) {
//					DimensionValuesController dimElement = new DimensionValuesController(cell, this);
//					dimElement.initializeValues();
//					tilePane.getChildren().add(dimElement);
//				}
//			}
//			break;
//		case CASE_DISTRIBUTION:
//			double upperBound = 0;
//			ObservableList<CaseDistributionController> caseElements = FXCollections.observableArrayList();
//			for (Cell cell : cube.getCells()) {
//				if (isCellShowable(cell)) {
//					CaseDistributionController element = new CaseDistributionController(cell, this);
//					element.initializeValues();
//					if (element.getUpperBound() > upperBound)
//						upperBound = element.getUpperBound();
//					caseElements.add(element);
//				}
//
//			}
//			for (CaseDistributionController element : caseElements) {
//				element.setRange(upperBound);
//				tilePane.getChildren().add(element);
//			}
//			break;
//		case LOG_METRICS:
//		}
//		// set the sorting
//		String sort = primarySort.getSelectionModel().getSelectedItem();
//		ObservableList<Node> nodeElements = FXCollections.observableArrayList();
//		nodeElements.addAll(tilePane.getChildren());
//		switch (sort) {
//		case CASES:
//			nodeElements.sort(new Comparator<Node>() {
//				@Override
//				public int compare(Node o1, Node o2) {
//					return Double.compare(((MiniViewControllerInterface) o1).getCell().getMetric(Metrics.CASES),
//							((MiniViewControllerInterface) o2).getCell().getMetric(Metrics.CASES));
//				}
//			});
//			break;
//		case EVENTS:
//			nodeElements.sort(new Comparator<Node>() {
//				@Override
//				public int compare(Node o1, Node o2) {
//					return Double.compare(((MiniViewControllerInterface) o1).getCell().getMetric(Metrics.EVENTS),
//							((MiniViewControllerInterface) o2).getCell().getMetric(Metrics.EVENTS));
//				}
//			});
//			break;
//		case CASE_SIZE:
//			nodeElements.sort(new Comparator<Node>() {
//				@Override
//				public int compare(Node o1, Node o2) {
//					return Double.compare(
//							((MiniViewControllerInterface) o1).getCell().getMetric(Metrics.EVENTS_PER_CASE),
//							((MiniViewControllerInterface) o2).getCell().getMetric(Metrics.EVENTS_PER_CASE));
//				}
//			});
//			break;
//		case CASE_DURATION:
//		case ENTROPY:
//		}
//		// horrible workaround because of Java bug that does not let you sort
//		// the getChildren() list directly
//		tilePane.getChildren().setAll(nodeElements);
//		tilePane.layout();
//	}
//
//	private boolean isCellShowable(Cell cell) {
//		return (isShowableBySelection(cell.isSelected()) && isShowableByEmptyness(cell.getLog().isEmpty()));
//	}
//
//	private boolean isShowableBySelection(boolean isSelected) {
//		if (showOnlySelected.isSelected() && isSelected)
//			return true;
//		else if (!showOnlySelected.isSelected())
//			return true;
//		else
//			return false;
//	}
//
//	private boolean isShowableByEmptyness(boolean isEmpty) {
//		if (!showEmpty.isSelected() && isEmpty)
//			return false;
//		else if (showEmpty.isSelected())
//			return true;
//		else
//			return true;
//	}
//
//	private void createCells() {
//		ObservableList<Dimension> dimensionAux = FXCollections.observableArrayList();
//		dimensionAux.addAll(cube.getDimensions());
//
//		ObservableList<Cell> cellAux = FXCollections.observableArrayList();
//		cellAux.addAll(cube.getCells());
//
//		cube.setCells(recursiveCellCreation(dimensionAux, cellAux));
//
//		cellAux.clear();
//	}
//
//	private ObservableList<Cell> recursiveCellCreation(ObservableList<Dimension> dimensions,
//			ObservableList<Cell> input) {
//		// only for diced dimensions
//
//		if (input == null)
//			input = FXCollections.observableArrayList();
//		if (dimensions.size() == 0)
//			return input;
//		else if (!dimensions.get(0).isVisible() || dimensions.get(0).isSliced()) {
//			dimensions.remove(0);
//			return recursiveCellCreation(dimensions, input);
//		} else {
//			ObservableList<Cell> output = FXCollections.observableArrayList();
//			if (input.isEmpty()) {
//				for (CheckBoxTreeItem<XAttribute> a : cube.getValueSelections()
//						.get(dimensions.get(0).getGranularity())) {
//					if (a.isSelected()) {
//						Cell cell = new Cell(null, mainController.getLog());
//						cell.addValue(a.getValue(), dimensions.get(0).toString());
//						output.add(cell);
//					}
//				}
//			} else {
//				for (Cell cell : input) {
//					for (CheckBoxTreeItem<XAttribute> a : cube.getValueSelections()
//							.get(dimensions.get(0).getGranularity())) {
//						if (a.isSelected()) {
//							if (cell.hasAttribute(a.getValue().getKey())) {
//								Cell c = new Cell(null, mainController.getLog());
//								for (XAttribute att : cell.getDimensionalValues().values())
//									c.addValue(att, cell.getDimension(att.getKey()));
//								c.addValue(a.getValue(), dimensions.get(0).toString());
//								output.add(c);
//							} else {
//								cell.addValue(a.getValue(), dimensions.get(0).toString());
//								output.add(cell);
//							}
//						}
//					}
//				}
//			}
//
//			dimensions.remove(0);
//			return recursiveCellCreation(dimensions, output);
//		}
//	}
//
//	private void fillCells() {
//		// get how XElements are distributed into cells
//		String selection = mainController.getDistributionModel();
//
//		XLog log = mainController.getLog();
//
//		ObservableList<Cell> cells = FXCollections.observableArrayList();
//		for (Cell cell : cube.getCells())
//			cells.add(cell);
//
//		// for each, obtain a list of events or traces to be distributed, check
//		// slice and dice filters
//
//		// then distribute those XElements into the cells
//		switch (selection) {
//		case CubeController.EVENTS:
//			// by event
//			for (XTrace trace : log)
//				for (XEvent event : trace)
//					if (!isFiltered(event))
//						for (Cell cell : cells)
//							cell.addElement(event);
//			// now for each cell, create traces if they werent there (only for
//			// event distribution
//			for (Cell cell : cells)
//				createTraces(cell);
//			break;
//		case CubeController.TRACES_FIRST:
//			// by trace (considering only first event)
//			for (XTrace trace : log) {
//				if (!trace.isEmpty()) {
//					XEvent event = trace.get(0);
//					if (!isFiltered(event))
//						for (Cell cell : cells)
//							cell.addElement(trace, event);
//				}
//			}
//
//			break;
//		case CubeController.TRACES_ANY:
//			// by trace (considering any event) can be slower
//			for (XTrace trace : log) {
//				for (XEvent event : trace)
//					if (!isFiltered(event))
//						for (Cell cell : cells) {
//							cell.addElement(trace);
//						}
//			}
//			break;
//		}
//
//		// initialize metrics
//		for (Cell cell : cells)
//			if (!cell.getLog().isEmpty())
//				cell.calculateMetrics();
//
//	}
//
//	private boolean isFiltered(XEvent event) {
//		for (XAttribute attribute : event.getAttributes().values())
//			for (Dimension dimension : cube.getDimensions())
//				if (dimension.isVisible() && dimension.hasAttribute(attribute.getKey())) {
//					Attribute a = dimension.getAttribute(attribute.getKey());
//					if (a.hasValue(attribute) && !a.isSelected(attribute.toString())) {
//						return true;
//					}
//				}
//		return false;
//	}
//
//	private void createTraces(Cell cell) {
//
//		XLog log = cell.getLog();
//		HashMap<String, XTrace> traces = new HashMap<String, XTrace>();
//		XFactory xfactory = new XFactoryBufferedImpl();
//
//		String case_id_name = null;
//		for (MappingRow row : mainController.getMappingRows())
//			if (row.getUseAs().equals(MappingController.CASE_ID))
//				case_id_name = row.getAttributeName();
//
//		XAttributeMap att;
//		for (XEvent e : cell.getEvents()) {
//			XAttribute a = e.getAttributes().get(case_id_name);
//			if (traces.containsKey(a.toString()))
//				traces.get(a.toString()).add(e);
//			else {
//				att = xfactory.createAttributeMap();
//				att.put(case_id_name, xfactory.createAttributeLiteral(case_id_name, a.toString(), null));
//				XTrace t = xfactory.createTrace(att);
//				t.add(e);
//				traces.put(a.toString(), t);
//			}
//		}
//
//		log.addAll(traces.values());
//	}
//
//	/**
//	 * @author abolt Button handlers
//	 */
//	@FXML
//	public void exportSelected(ActionEvent event) {
//
//	}
//
//	@FXML
//	public void selectAll(ActionEvent event) {
//		for (Node node : tilePane.getChildren())
//			if (node instanceof MiniViewControllerInterface)
//				((MiniViewControllerInterface) node).setSelected(true);
//		updateSelectionCount();
//	}
//
//	@FXML
//	public void selectNone(ActionEvent event) {
//		for (Node node : tilePane.getChildren())
//			if (node instanceof MiniViewControllerInterface)
//				((MiniViewControllerInterface) node).setSelected(false);
//		updateSelectionCount();
//	}
//
//	@FXML
//	public void invertSelection(ActionEvent event) {
//		for (Node node : tilePane.getChildren())
//			if (node instanceof MiniViewControllerInterface)
//				((MiniViewControllerInterface) node)
//						.setSelected(!((MiniViewControllerInterface) node).getCell().isSelected());
//		updateSelectionCount();
//	}
//
//	@FXML
//	public void updateCubeToMatchSelection() {
//		// does dicing to produce a cube that would only have the selected
//		// cells.
//	}
//
//	public void updateSelectionCount() {
//		int counter = 0;
//		for (Cell cell : cube.getCells())
//			if (cell.isSelected())
//				counter++;
//		selectionCount.setText(Integer.toString(counter));
//	}
//
//	protected void selectionMessage() {
//		Alert alert = new Alert(AlertType.WARNING);
//		alert.setTitle("Error");
//		alert.setHeaderText("No cells selected!");
//		alert.setContentText(
//				"You have to select at least one cell in order to proceed to the next step. Otherwise there is nothing for me to do.");
//		alert.showAndWait();
//	}
//}
