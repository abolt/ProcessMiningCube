package application.controllers.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JComponent;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.processmining.alphaminer.plugins.AlphaMinerPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.log.ui.logdialog.SlickerOpenLogSettings;
import org.processmining.plugins.petrinet.PetriNetVisualization;
import org.processmining.processcomparator.plugins.ProcessComparatorPlugin;
import org.processmining.processcomparator.view.ComparatorPanel;

import application.controllers.results.ResultDialogController;
import application.models.eventbase.AbstrEventBase;
import application.models.xlog.XLogStructure;
import application.operations.DialogUtils;
import application.operations.LogUtils;
import application.operations.Utils;
import application.prom.RapidProMGlobalContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;

public class CellContextMenuController {

	ContextMenu newMenu;
	MenuItem visualizeEventLog;
	MenuItem visualizeProcessModel;
	MenuItem compare;
	MenuItem runRapidMinerWorkflow;
	MenuItem exportEventLogs;
	Grid grid;

	public CellContextMenuController(SpreadsheetView table, CubeExplorerController explorerController, Grid grid) {
		this.grid = grid;

		visualizeEventLog = new MenuItem("Event Log");
		visualizeEventLog.setOnAction(new EventLogListener(table, explorerController, grid));

		visualizeProcessModel = new MenuItem("Process Model");
		visualizeProcessModel.setOnAction(new EventLogListener(table, explorerController, grid));

		compare = new MenuItem("Compare Event Logs");
		compare.setOnAction(new EventLogListener(table, explorerController, grid));

		runRapidMinerWorkflow = new MenuItem("Run RapidMiner Workflow");
		runRapidMinerWorkflow.setOnAction(new EventLogListener(table, explorerController, grid));

		exportEventLogs = new MenuItem("Export Event Log");
		exportEventLogs.setOnAction(new EventLogListener(table, explorerController, grid));
		/**
		 * List Build
		 */
		ObservableList<MenuItem> newList = FXCollections.observableArrayList();
		Menu visualize = new Menu("Visualize as:");
		visualize.getItems().add(visualizeEventLog);
		visualize.getItems().add(visualizeProcessModel);
		newList.add(visualize);
		newList.add(compare);
		newList.add(runRapidMinerWorkflow);
		newList.add(exportEventLogs);
		newMenu = new ContextMenu(newList.toArray(new MenuItem[4]));
	}

	public ContextMenu getContextMenu() {
		return newMenu;
	}

	class EventLogListener implements EventHandler<ActionEvent> {

		SpreadsheetView table;
		CubeExplorerController explorerController;
		Grid grid;
		PluginContext pluginContext;

		EventLogListener(SpreadsheetView table, CubeExplorerController explorerController, Grid grid) {
			this.table = table;
			this.explorerController = explorerController;
			this.grid = grid;
			pluginContext = RapidProMGlobalContext.instance().getRapidProMPluginContext();

		}

		@SuppressWarnings("rawtypes")
		@Override
		public void handle(ActionEvent arg0) {

			XLogStructure structure = DialogUtils.askXLogStructure(explorerController.getValidAttributeList());
			if (structure == null)
				return;
			/*
			 * If we are not comparing, do the normal processing
			 */
			if (!arg0.getSource().equals(compare)) {

				// get the list of event ids of one selected cell (focused?)
				Optional<Boolean> forAll = Optional.of(false);
				if (table.getSelectionModel().getSelectedCells().size() > 1)
					forAll = DialogUtils.askIfMergeLogs();
				if (forAll == null)
					return;

				AbstrEventBase eb = explorerController.getEventBase();

				// treat all as one
				if (forAll.get()) {
					List<String> eventLists = new ArrayList<String>();
					for (TablePosition cell : table.getSelectionModel().getSelectedCells())
						eventLists.add((String) grid.getRows().get(cell.getRow()).get(cell.getColumn()).getItem());

					List<XEvent> events = eb.materializeEvents(Utils.parseEventIDs(eventLists), structure.getFactory());
					XLog log = LogUtils.buildXLogFromEvents(events, structure.getCaseID(), structure.getEventID(),
							structure.getTimestamp(), structure.getFactory(), eventLists.toString());

					if (arg0.getSource().equals(visualizeEventLog)) {
						SlickerOpenLogSettings s = new SlickerOpenLogSettings();
						ResultDialogController dia = new ResultDialogController(log, s.showLogVis(pluginContext, log));
					} else if (arg0.getSource().equals(visualizeProcessModel)) {
						PetriNetVisualization visualizer = new PetriNetVisualization();
						JComponent result = visualizer.visualize(pluginContext, (Petrinet) AlphaMinerPlugin
								.applyAlphaPlus(pluginContext, log, new XEventNameClassifier())[0]);
						ResultDialogController dia = new ResultDialogController(log, result);

					} else if (arg0.getSource().equals(runRapidMinerWorkflow)) {

					} else if (arg0.getSource().equals(exportEventLogs)) {

					}
				}
				// treat them individually
				else {

					for (TablePosition cell : table.getSelectionModel().getSelectedCells()) {
						List<String> eventLists = new ArrayList<String>();
						eventLists.add((String) grid.getRows().get(cell.getRow()).get(cell.getColumn()).getItem());

						List<XEvent> events = eb.materializeEvents(Utils.parseEventIDs(eventLists),
								structure.getFactory());
						XLog log = LogUtils.buildXLogFromEvents(events, structure.getCaseID(), structure.getEventID(),
								structure.getTimestamp(), structure.getFactory(), eventLists.toString());

						if (arg0.getSource().equals(visualizeEventLog)) {

							System.out.println(getCellName(grid, cell.getRow(), cell.getColumn()));
							SlickerOpenLogSettings s = new SlickerOpenLogSettings();
							ResultDialogController dia = new ResultDialogController(log,
									s.showLogVis(pluginContext, log));
						} else if (arg0.getSource().equals(visualizeProcessModel)) {

							PetriNetVisualization visualizer = new PetriNetVisualization();
							JComponent result = visualizer.visualize(pluginContext, (Petrinet) AlphaMinerPlugin
									.applyAlphaPlus(pluginContext, log, new XEventNameClassifier())[0]);
							ResultDialogController dia = new ResultDialogController(log, result);

						} else if (arg0.getSource().equals(runRapidMinerWorkflow)) {

						} else if (arg0.getSource().equals(exportEventLogs)) {

						}
					}
				}
			} else {

				AbstrEventBase eb = explorerController.getEventBase();
				List<XLog> logs = new ArrayList<XLog>();

				for (TablePosition cell : table.getSelectionModel().getSelectedCells()) {
					List<String> eventLists = new ArrayList<String>();
					eventLists.add((String) grid.getRows().get(cell.getRow()).get(cell.getColumn()).getItem());

					List<XEvent> events = eb.materializeEvents(Utils.parseEventIDs(eventLists), structure.getFactory());
					logs.add(LogUtils.buildXLogFromEvents(events, structure.getCaseID(), structure.getEventID(),
							structure.getTimestamp(), structure.getFactory(),
							getCellName(grid, cell.getRow(), cell.getColumn())));
				}
				ProcessComparatorPlugin pc = new ProcessComparatorPlugin();
				ComparatorPanel cp = pc.run(pluginContext, logs.toArray(new XLog[logs.size()]));
				ResultDialogController dia = new ResultDialogController(null, cp);

			}
		}
	}

	private static String getCellName(Grid grid, int row, int col) {

		String logName = "";
		for (int i = 0; i <= row; i++)
			if (isTextCell(grid.getRows().get(i).get(col).getText()))
				logName = logName + grid.getRows().get(i).get(col).getText() + " AND ";

		for (int i = 0; i <= col; i++)
			if (isTextCell(grid.getRows().get(row).get(i).getText()))
				logName = logName + grid.getRows().get(row).get(i).getText() + " AND ";

		logName = logName.trim();
		if (logName.endsWith("AND"))
			logName = logName.substring(0, logName.length() - 3);

		return logName;
	}

	private static boolean isTextCell(String content) {

		if (content.contains("("))
			return true;
		else
			return false;
	}
}
