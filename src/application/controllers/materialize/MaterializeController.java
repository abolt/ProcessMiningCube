package application.controllers.materialize;

import java.io.IOException;
import java.util.HashMap;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import application.PMCLauncher;
import application.controllers.AbstractTabController;
import application.controllers.cube.CubeController;
import application.controllers.mapping.MappingController;
import application.controllers.mapping.MappingRow;
import application.models.cube.Cell;
import application.models.cube.Cube;
import application.models.dimension.Attribute;
import application.models.dimension.Dimension;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;

public class MaterializeController extends AbstractTabController {

	@FXML
	private Tab tabMaterialize;

	private Cube cube;

	@FXML
	private ImageView image;

	@FXML
	private TilePane tilePane;

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
			setCellVisualizers();

		}

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

		// do stuff
		setCompleted(true);
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

	private void setCellVisualizers() {
		for (Cell cell : cube.getCells())
			try {
				
				tilePane.getChildren().add(FXMLLoader.load(PMCLauncher.class.getResource("views/MiniView_DimensionValues.fxml")));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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
						cell.addValue(a.getValue());
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
									c.addValue(att);
								c.addValue(a.getValue());
								output.add(c);
							} else {
								cell.addValue(a.getValue());
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

}
