package application.controllers.explorer;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.controlsfx.control.ToggleSwitch;
import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetView;

import application.controllers.workers.DBWorker;
import application.controllers.workers.WorkerCatalog;
import application.models.attribute.abstr.Attribute;
import application.models.metric.Metric;
import application.operations.DBUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class CubeTableViewController extends BorderPane implements Initializable {

	@FXML
	private VBox tableSettingsPanel;

	@FXML
	private ChoiceBox<Metric> metricSelector;
	@FXML
	private ToggleSwitch hideRows, hideColumns, mergeRows, mergeColumns, autoUpdate;

	private SpreadsheetView table;
	private CubeExplorerController explorerController;
	private Metric currentMetric;

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
		initializeMetricsList();
		// start without the table settings panel
		this.setTop(null);
		requestTableUpdate();

	}

	private void initializeMetricsList() {
		ObservableList<Metric> metricList = FXCollections.observableArrayList();
		metricList.add(new Metric(Metric.eventCount));
		metricList.add(new Metric(Metric.caseCount));
		metricList.add(new Metric(Metric.avgCaseLength));
		metricList.add(new Metric(Metric.avgCaseDuration));
		metricList.add(new Metric(Metric.list));

		metricSelector.setItems(metricList);
		metricSelector.getSelectionModel().select(0);

		currentMetric = metricSelector.getValue();

		metricSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Metric>() {
			@Override
			public void changed(ObservableValue<? extends Metric> observable, Metric oldValue, Metric newValue) {
				if (!oldValue.toString().equals(newValue.toString())) {
					currentMetric = newValue;
					if (!currentMetric.toString().equals(Metric.eventCount)) {
						ChoiceDialog<Attribute> attributeDialog = new ChoiceDialog<Attribute>(
								currentMetric.getCaseID() != null ? currentMetric.getCaseID()
										: explorerController.getValidAttributeList().get(0),
								explorerController.getValidAttributeList());
						attributeDialog.setTitle("Case ID Selection");
						attributeDialog.setHeaderText("This Metric needs a 'Case ID'");
						attributeDialog
								.setContentText("Please select an attribute to be used as 'Case ID' for this metric.");
						attributeDialog.showAndWait();
						currentMetric.setCaseID(attributeDialog.getSelectedItem());
					}
					requestTableUpdate();
				}
			}
		});
		tableSettingsPanel.layout();
	}

	public void requestTableUpdate() {
		if (autoUpdate.isSelected())
			updateTable();
	}

	public void updateTable() {
		
		System.out.println("table updated");

		Object[] inputs = new Object[5];
		inputs[0] = explorerController.getRows();
		inputs[1] = explorerController.getColumns();
		inputs[2] = explorerController.getFilters();
		inputs[3] = explorerController.getEventBase();
		inputs[4] = currentMetric;
		
		DBWorker dbWorker = WorkerCatalog.getDBWorker();
		Grid grid = dbWorker.run(null, inputs);
				
				/*
				 * DBUtils.getGrid(explorerController.getRows(), explorerController.getColumns(),
				explorerController.getFilters(), explorerController.getEventBase(), currentMetric);
				 */
		table = new SpreadsheetView(grid);

		inputs[4] = new Metric(Metric.list);
		Grid eventGrid = dbWorker.run(null, inputs);
				
//				DBUtils.getGrid(explorerController.getRows(), explorerController.getColumns(),
//				explorerController.getFilters(), explorerController.getEventBase(), newMetric);
		
		CellContextMenuController contextMenu = new CellContextMenuController(table, explorerController, eventGrid);
		table.setContextMenu(contextMenu.getContextMenu());

		table.setEditable(false);
		this.setCenter(table);
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
	}

	@FXML
	protected void clickOnMetricFilters() {
	}

	@FXML
	protected void clickOnUpdateTable() {
		if (!autoUpdate.isSelected())
			updateTable();
	}
}
