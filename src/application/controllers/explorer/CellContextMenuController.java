package application.controllers.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.alphaminer.plugins.AlphaMinerPlugin;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMf;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.astar.petrinet.AbstractPetrinetReplayer;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithILP;
import org.processmining.plugins.astar.petrinet.PetrinetReplayerWithoutILP;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.inductiveminer2.mining.InductiveMiner;
import org.processmining.plugins.log.ui.logdialog.SlickerOpenLogSettings;
import org.processmining.plugins.petrinet.PetriNetVisualization;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.algorithms.costbasedcomplete.CostBasedCompleteParam;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.pnalignanalysis.visualization.projection.PNLogReplayProjectedVisPanel;
import org.processmining.processcomparator.plugins.ProcessComparatorPlugin;
import org.processmining.processcomparator.view.ComparatorPanel;

import com.rapidminer.parameter.UndefinedParameterError;

import application.controllers.results.ResultDialogController;
import application.controllers.workers.RapidMinerWorker;
import application.controllers.workers.WorkerCatalog;
import application.models.eventbase.AbstrEventBase;
import application.models.xlog.XLogStructure;
import application.operations.DialogUtils;
import application.operations.LogUtils;
import application.operations.Utils;
import application.operations.dialogs.FileChooserDialog;
import application.operations.io.log.XLogExporter;
import application.operations.io.log.XLogExporter.Format;
import application.prom.RapidProMGlobalContext;
import application.utils.XLogUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TablePosition;
import nl.tue.astar.AStarException;

public class CellContextMenuController {

	ContextMenu newMenu;
	MenuItem visualizeEventLog;
	MenuItem visualizeProcessModel;
	MenuItem compare;
	MenuItem runRapidMinerWorkflow;
	MenuItem exportEventLogs;
	MenuItem conformanceChecking;

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

		conformanceChecking = new MenuItem("Conformance Checking");
		conformanceChecking.setOnAction(new EventLogListener(table, explorerController, grid));

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
		newList.add(conformanceChecking);

		newMenu = new ContextMenu(newList.toArray(new MenuItem[5]));
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
			Format format = null;
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
				exportDirectory = XLogExporter.askDirectory();
				format = XLogExporter.askFormat();
			}

			/*
			 * If we are not comparing, do the normal processing
			 */
			if (arg0.getSource().equals(visualizeEventLog) || arg0.getSource().equals(visualizeProcessModel)) {

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
						XLogExporter.exportLog(log, format, exportDirectory.getAbsolutePath());
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
							XLogExporter.exportLog(log, format, exportDirectory.getAbsolutePath());
						}
					}
				}
			} else if (arg0.getSource().equals(compare)) {
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

			} else if (arg0.getSource().equals(conformanceChecking)) {
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

				Object[] model = IMPetriNet.minePetriNet(pluginContext, logs.get(0), new MiningParametersIMf());
				
				
				;
				Petrinet pn = (Petrinet) model[0];
				Marking initial = (Marking) model[1];
				Marking finalM = (Marking) model[2];

				for (int i = 2; i < logs.size(); i++)
					for (XTrace t : logs.get(i))
						logs.get(1).add(t);

				PNRepResult alignments;
				JPanel panel = null;
				try {
					XEventClassifier eventClassifier = new XEventNameClassifier();
					TransEvClassMapping mapping = constructMapping(pn, logs.get(1), eventClassifier);
					alignments = getAlignment(pluginContext, pn, logs.get(1), initial, finalM, mapping);
					panel = new PNLogReplayProjectedVisPanel(pluginContext, pn, initial, logs.get(1), mapping,
							alignments);
				} catch (UndefinedParameterError | ConnectionCannotBeObtained e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ResultDialogController dia = new ResultDialogController(logs.get(1), panel);
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

	public PNRepResult getAlignment(PluginContext pluginContext, PetrinetGraph net, XLog log, Marking initialMarking,
			Marking finalMarking, TransEvClassMapping mapping) throws UndefinedParameterError {

		Map<Transition, Integer> costMOS = constructMOSCostFunction(net);
		
		Map<XEventClass, Integer> costMOT = constructMOTCostFunction(net, log, mapping.getEventClassifier());

		AbstractPetrinetReplayer<?, ?> replayEngine = new PetrinetReplayerWithoutILP();

		IPNReplayParameter parameters = new CostBasedCompleteParam(costMOT, costMOS);
		parameters.setInitialMarking(initialMarking);
		parameters.setFinalMarkings(finalMarking);
		parameters.setGUIMode(false);
		parameters.setCreateConn(false);
		parameters.setNumThreads(2);
		((CostBasedCompleteParam) parameters).setMaxNumOfStates(200 * 1000);

		PNRepResult result = null;
		try {
			result = replayEngine.replayLog(pluginContext, net, log, mapping, parameters);

		} catch (AStarException e) {
			e.printStackTrace();
		}

		return result;
	}

	private static Map<Transition, Integer> constructMOSCostFunction(PetrinetGraph net) {
		Map<Transition, Integer> costMOS = new HashMap<Transition, Integer>();

		for (Transition t : net.getTransitions())
			if (t.isInvisible())
				costMOS.put(t, 0);
			else
				costMOS.put(t, 1);

		return costMOS;
	}

	private static Map<XEventClass, Integer> constructMOTCostFunction(PetrinetGraph net, XLog log,
			XEventClassifier eventClassifier) {
		Map<XEventClass, Integer> costMOT = new HashMap<XEventClass, Integer>();
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (XEventClass evClass : summary.getEventClasses().getClasses()) {
			costMOT.put(evClass, 1);
		}

		return costMOT;
	}

	private static TransEvClassMapping constructMapping(PetrinetGraph net, XLog log, XEventClassifier eventClassifier) {
		TransEvClassMapping mapping = new TransEvClassMapping(eventClassifier, new XEventClass("DUMMY", 99999));

		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);

		for (Transition t : net.getTransitions()) {
			for (XEventClass evClass : summary.getEventClasses().getClasses()) {
				String id = evClass.getId();

				if (t.getLabel().equals(id)) {
					mapping.put(t, evClass);
					break;
				}
			}

		}

		return mapping;
	}

	public static Marking getFinalMarking(Petrinet pn) {
		List<Place> places = new ArrayList<Place>();
		Iterator<Place> placesIt = pn.getPlaces().iterator();
		while (placesIt.hasNext()) {
			Place nextPlace = placesIt.next();
			Collection inEdges = pn.getOutEdges(nextPlace);
			if (inEdges.isEmpty()) {
				places.add(nextPlace);
			}
		}
		Marking finalMarking = new Marking();
		for (Place place : places) {
			finalMarking.add(place);
		}
		return finalMarking;
	}
}
