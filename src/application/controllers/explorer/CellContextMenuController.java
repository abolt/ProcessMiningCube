package application.controllers.explorer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
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
import org.processmining.log.csvexport.XesCsvSerializer;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.log.exporting.ExportLogXes;
import org.processmining.plugins.log.ui.logdialog.SlickerOpenLogSettings;
import org.processmining.plugins.petrinet.PetriNetVisualization;
import org.processmining.processcomparator.plugins.ProcessComparatorPlugin;
import org.processmining.processcomparator.view.ComparatorPanel;

import application.controllers.results.ResultDialogController;
import application.controllers.workers.RapidMinerWorker;
import application.controllers.workers.WorkerCatalog;
import application.models.eventbase.AbstrEventBase;
import application.models.xlog.XLogStructure;
import application.operations.DialogUtils;
import application.operations.LogUtils;
import application.operations.Utils;
import application.operations.dialogs.DirectoryChooserDialog;
import application.operations.dialogs.FileChooserDialog;
import application.prom.RapidProMGlobalContext;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceDialog;
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

			// general requirement
			XLogStructure structure = DialogUtils.askXLogStructure(explorerController.getValidAttributeList());
			// for running workflows
			RapidMinerWorker rmWorker = null;
			File processFile = null;
			// for exporting logs
			String exportFormat = null;
			File exportDirectory = null;

			if (structure == null)
				return;

			if (arg0.getSource().equals(runRapidMinerWorkflow)) {
				rmWorker = WorkerCatalog.getRapidMinerWorker();
				FileChooserDialog openProcess = new FileChooserDialog("Open Process File",
						"Please select a process file (.rmp) to be executed",
						"Note: make sure that your process starts with the \"Extract Event Log\" operator,"
								+ " and it is connected to the process input");
				processFile = openProcess.showAndWait().get();
				if (processFile == null)
					return;
			} else if (arg0.getSource().equals(exportEventLogs)) {

				DirectoryChooserDialog directoryDialog = new DirectoryChooserDialog("Export Event Log(s)",
						"Please select a Folder", "The selected event log(s) will be exported here.");
				Optional<File> directory = directoryDialog.showAndWait();

				if (!directory.isPresent())
					return;

				exportDirectory = directory.get();

				List<String> choices = new ArrayList<>();
				choices.add(".xes");
				choices.add(".xes.gz (compressed)");
				choices.add(".csv");

				ChoiceDialog<String> dialog = new ChoiceDialog<>(".xes", choices);
				dialog.setTitle("Export Event Log(s)");
				dialog.setHeaderText("Please select an export format.");
				dialog.setContentText("Available formats: .xes, .xes.gz (compressed), and .csv");

				// Traditional way to get the response value.
				Optional<String> result = dialog.showAndWait();
				if (!result.isPresent())
					return;
				exportFormat = result.get();
			}

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
							structure.getTimestamp(), structure.getFactory(), "merged event log");

					if (arg0.getSource().equals(visualizeEventLog)) {
						SlickerOpenLogSettings s = new SlickerOpenLogSettings();
						ResultDialogController dia = new ResultDialogController(log, s.showLogVis(pluginContext, log));

					} else if (arg0.getSource().equals(visualizeProcessModel)) {
						PetriNetVisualization visualizer = new PetriNetVisualization();
						JComponent result = visualizer.visualize(pluginContext, (Petrinet) AlphaMinerPlugin
								.applyAlphaPlus(pluginContext, log, new XEventNameClassifier())[0]);
						ResultDialogController dia = new ResultDialogController(log, result);

					} else if (arg0.getSource().equals(runRapidMinerWorkflow)) {
						rmWorker.run(log, processFile);
					} else if (arg0.getSource().equals(exportEventLogs)) {
						switch (exportFormat) {
						case ".xes": {
							String path = log.getAttributes().get("concept:name").toString().trim() + ".xes";
							path = path.replace(":", "_").replace("(", "").replace(")", "");
							path = exportDirectory.getAbsolutePath() + File.separator + path;
							File file = null;
							try {
								file = new File(path);
								Files.createFile(file.toPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								ExportLogXes.export(log, file);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
						case ".xes.gz (compressed)": {
							String path = log.getAttributes().get("concept:name").toString().trim() + ".xes.gz";
							path = path.replace(":", "_").replace("(", "").replace(")", "");
							path = exportDirectory.getAbsolutePath() + File.separator + path;
							File file = null;
							try {
								file = new File(path);
								Files.createFile(file.toPath());
							} catch (IOException e) {
								e.printStackTrace();
							}
							try {
								ExportLogXes.export(log, file);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
						case ".csv": {
							XesCsvSerializer csvSerializer = new XesCsvSerializer("dd-MM-yyyy HH:mm:ss.SSS");
							OutputStream output;
							try {
								String path = log.getAttributes().get("concept:name").toString().trim() + ".csv";
								path = path.replace(":", "_").replace("(", "").replace(")", "");
								path = exportDirectory.getAbsolutePath() + File.separator + path;
								output = new FileOutputStream(path);
								csvSerializer.serialize(log, output);
							} catch (FileNotFoundException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							break;
						}
						}
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
								structure.getTimestamp(), structure.getFactory(),
								getCellName(grid, cell.getRow(), cell.getColumn()));

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
							rmWorker.run(log, processFile);
						} else if (arg0.getSource().equals(exportEventLogs)) {
							switch (exportFormat) {
							case ".xes": {
								String path = log.getAttributes().get("concept:name").toString().trim() + ".xes";
								path = path.replace(":", "_").replace("(", "").replace(")", "");
								path = exportDirectory.getAbsolutePath() + File.separator + path;
								File file = null;
								try {
									file = new File(path);
									Files.createFile(file.toPath());
								} catch (IOException e) {
									e.printStackTrace();
								}
								try {
									ExportLogXes.export(log, file);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								break;
							}
							case ".xes.gz (compressed)": {
								String path = log.getAttributes().get("concept:name").toString().trim() + ".xes.gz";
								path = path.replace(":", "_").replace("(", "").replace(")", "");
								path = exportDirectory.getAbsolutePath() + File.separator + path;
								File file = null;
								try {
									file = new File(path);
									Files.createFile(file.toPath());
								} catch (IOException e) {
									e.printStackTrace();
								}
								try {
									ExportLogXes.export(log, file);
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
								break;
							}
							case ".csv": {
								XesCsvSerializer csvSerializer = new XesCsvSerializer("dd-MM-yyyy HH:mm:ss.SSS");
								OutputStream output;
								try {
									String path = log.getAttributes().get("concept:name").toString().trim() + ".csv";
									path = path.replace(":", "_").replace("(", "").replace(")", "");
									path = exportDirectory.getAbsolutePath() + File.separator + path;
									output = new FileOutputStream(path);
									csvSerializer.serialize(log, output);
								} catch (FileNotFoundException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								break;
							}
							}
						}
					}
				}
			} else {
				// we are comparing
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
