package application.controllers.explorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.JOptionPane;

import org.controlsfx.control.spreadsheet.Grid;
import org.controlsfx.control.spreadsheet.SpreadsheetView;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.plugins.log.ui.logdialog.SlickerOpenLogSettings;

import application.controllers.results.ResultDialogController;
import application.models.attribute.abstr.Attribute;
import application.models.eventbase.AbstrEventBase;
import application.operations.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
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
		visualizeEventLog.setOnAction(new EventHandler<ActionEvent>() {

			@SuppressWarnings({ "rawtypes" })
			@Override
			public void handle(ActionEvent event) {

				// This should go into a utilities class (reused by other
				// options)
				ChoiceDialog<Attribute> dialog = new ChoiceDialog<Attribute>(
						explorerController.getValidAttributeList().get(0), explorerController.getValidAttributeList());
				dialog.setTitle("Case ID required");
				dialog.setHeaderText(
						"Please indicate the attribute that will be used as Case ID to build the Event Log(s)");
				dialog.setContentText("Case ID:");
				Optional<Attribute> result = dialog.showAndWait();

				AbstrEventBase eb = explorerController.getEventBase();

				// get the list of event ids of one selected cell (focused?)
				Optional<Boolean> forAll = Optional.of(true);
				if (table.getSelectionModel().getSelectedCells().size() > 1) {
					forAll = getForAllTogether();
				}

				// treat all as one
				if (forAll.get()) {
					List<String> eventLists = new ArrayList<String>();
					for (TablePosition cell : table.getSelectionModel().getSelectedCells())
						eventLists.add((String) grid.getRows().get(cell.getRow()).get(cell.getColumn()).getItem());

					List<XEvent> events = eb.materializeEvents(Utils.parseEventIDs(eventLists),
							new XFactoryNaiveImpl());
					System.out.println("I got " + events.size() + " events back!");
					SlickerOpenLogSettings s = new SlickerOpenLogSettings();
					XFactory fac =  new XFactoryNaiveImpl();
					XTrace trace = fac.createTrace();
					trace.addAll(events);
					XLog log = fac.createLog();
					log.add(trace);
					
					ResultDialogController dia = new ResultDialogController(log, s.showLogVis(null, log));
					
					
				}
				// treat them individually
				else {

				}

				System.out.println("clicked");

				// merge or kept lists separated

				// materialize the events

				// build traces

				// eb.query(conditionMatrix, filters, numCells,
				// numConditionsPerCell, metric)

			}

		});

		visualizeProcessModel = new MenuItem("Process Model");
		visualizeProcessModel.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

			}

		});

		compare = new MenuItem("Compare Event Logs");
		compare.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

			}

		});

		runRapidMinerWorkflow = new MenuItem("Run RapidMiner Workflow");
		runRapidMinerWorkflow.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

			}

		});

		exportEventLogs = new MenuItem("Export Event Log");
		exportEventLogs.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// TODO Auto-generated method stub

			}

		});

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

	public Optional<Boolean> getForAllTogether() {

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Selection Scope");
		alert.setHeaderText("Multiple Selection Detected");
		alert.setContentText(
				" Do you want to consider all the selected cells as ONE event log? (Click \"No\" if you want each cell to be treated as an individual event log");

		ButtonType buttonTypeOne = new ButtonType("Yes");
		ButtonType buttonTypeTwo = new ButtonType("No");

		alert.getButtonTypes().setAll(buttonTypeOne, buttonTypeTwo);

		boolean returner = false;
		Optional<ButtonType> result = alert.showAndWait();
		if (result.get() == buttonTypeOne) {
			returner = true;
		} else if (result.get() == buttonTypeTwo) {
			returner = false;
		}
		return Optional.of(returner);

	}

}
