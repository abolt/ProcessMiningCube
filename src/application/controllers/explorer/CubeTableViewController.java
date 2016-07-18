package application.controllers.explorer;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.ResourceBundle;

import org.controlsfx.control.spreadsheet.GridBase;
import org.controlsfx.control.spreadsheet.SpreadsheetCell;
import org.controlsfx.control.spreadsheet.SpreadsheetCellType;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import application.models.dimension.Attribute;
import application.models.eventbase.AbstrEventBase;
import application.models.eventbase.ConditionSet;
import application.models.explorer.HeaderTree;
import application.models.explorer.HeaderTree.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class CubeTableViewController extends BorderPane implements Initializable {

	@FXML
	private VBox tableSettingsPanel;

	private SpreadsheetView table;
	private CubeExplorerController explorerController;

	public CubeTableViewController(CubeExplorerController controller) {

		explorerController = controller;
		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/explorer/CubeTableView.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		initializeContent();

	}

	public void initializeContent() {
		updateTable();
	}

	public void updateTable() {

		int rowCount, colCount;
		List<Attribute> rows = explorerController.getRows();
		List<Attribute> columns = explorerController.getColumns();
		AbstrEventBase eb = explorerController.getEventBase();

		HeaderTree rowHeaders = new HeaderTree(rows);
		HeaderTree colHeaders = new HeaderTree(columns);

		// List<Node> list = rowHeaders.getAccumulatedLeafs(-1);
		// System.out.println("Layer " + rows.size());
		// for (int j = 0; j < list.size(); j++) {
		// System.out.print("Node " + j + ": ");
		// for (Pair<Attribute, String> pair : list.get(j).values)
		// System.out.print(pair.getKey().getAttributeName() + " = " +
		// pair.getValue() + " AND ");
		// System.out.print("\n");
		// }

		rowCount = rowHeaders.getLeafs(-1).size();
		colCount = colHeaders.getLeafs(-1).size();

		GridBase grid = new GridBase(rowCount + columns.size(), colCount + rows.size());

		ObservableList<ObservableList<SpreadsheetCell>> cells = FXCollections.observableArrayList();

		// if (!rowHeaders.isEmpty()) {
		// grid.getRowHeaders().clear();
		// for (Pair<Attribute, String> row : rowHeaders)
		// grid.getRowHeaders().add(row.getKey().getAttributeName() + " = " +
		// row.getValue());
		// }
		// if (!colHeaders.isEmpty()) {
		// grid.getColumnHeaders().clear();
		// for (Pair<Attribute, String> col : colHeaders)
		// grid.getRowHeaders().add(col.getKey().getAttributeName() + " = " +
		// col.getValue());
		// }

		/**
		 * Create the empty cells with the headers
		 * First the rows, then the columns
		 */
		for (int i = 0; i < grid.getRowCount(); i++) {
			ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
			
			
		}
		
		for (int i = 0; i < grid.getRowCount(); i++) {
			ObservableList<SpreadsheetCell> row = FXCollections.observableArrayList();
			for (int j = 0; j < grid.getColumnCount(); j++) {
				if (i < columns.size() && j >= rows.size()) {
					// this is a column header
					
					

				} else{
					
				}
				ConditionSet conditions = new ConditionSet();
				if (!rowHeaders.isEmpty())
					conditions.addCondition(rowHeaders.get(i).getKey(), rowHeaders.get(i).getValue());
				if (!colHeaders.isEmpty())
					conditions.addCondition(colHeaders.get(j).getKey(), colHeaders.get(j).getValue());
				if (!rowHeaders.isEmpty() || !colHeaders.isEmpty())
					row.add(SpreadsheetCellType.STRING.createCell(i, j, 1, 1,
							Integer.toString(explorerController.getEventBase().getEvents(conditions).size())));
			}
			cells.add(row);
		}

		grid.setRows(cells);
		table = new SpreadsheetView(grid);
		table.setEditable(false);
		this.setCenter(table);
		this.setTop(null);
		this.layout();
	}

	protected void clickOnTableSettings() {
		if (this.getTop() == null)
			this.setTop(tableSettingsPanel);
		else
			this.setTop(null);
		this.layout();

	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO Auto-generated method stub

	}
}
