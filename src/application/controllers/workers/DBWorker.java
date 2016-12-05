package application.controllers.workers;

import java.util.List;

import org.controlsfx.control.spreadsheet.Grid;
import org.deckfour.xes.model.XLog;

import application.models.attribute.abstr.Attribute;
import application.models.eventbase.AbstrEventBase;
import application.models.eventbase.FileBasedEventBase;
import application.models.metric.Metric;
import application.operations.DBUtils;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;

public class DBWorker {

	public Grid run(XLog log, Object... objects) {

		final Grid[] grid = new Grid[1];
		Dialog<Void> progress = new Dialog<Void>();
		progress.setTitle("Retrieving Events");
		progress.setHeaderText("Please wait while I retrieve and organize the events...");
		progress.getDialogPane().setContent(new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS));

		Task<Void> task = new Task<Void>() {
			@SuppressWarnings("unchecked")
			@Override
			protected Void call() throws Exception {

				try {
					grid[0] = DBUtils.getGrid((List<Attribute>) objects[0], (List<Attribute>) objects[1],
							(List<Attribute>) objects[2], (AbstrEventBase) objects[3], (Metric) objects[4]);
					
				} catch (Exception e) {

					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Something occurred:");
					alert.setContentText(e.getMessage());
					alert.showAndWait();
				}
				return null;
			}

			@Override
			protected void done() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						progress.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
						progress.close();
					}
				});
			}
		};

		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();

		progress.showAndWait();
		
		return grid[0];

	}
	
	public AbstrEventBase createEventBase(String filePath, String dbName, List<Attribute> allAttributes) {

		final AbstrEventBase[] result = new AbstrEventBase[1];
		Dialog<Void> progress = new Dialog<Void>();
		progress.setTitle("Building DataBase");
		progress.setHeaderText("Please wait while I build and organize the database...");
		progress.getDialogPane().setContent(new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS));

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {

				try {
					result[0] = new FileBasedEventBase(filePath, dbName,allAttributes);
					
				} catch (Exception e) {

					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Something occurred:");
					alert.setContentText(e.getMessage());
					alert.showAndWait();
				}
				return null;
			}

			@Override
			protected void done() {
				Platform.runLater(new Runnable() {
					@Override
					public void run() {
						progress.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL);
						progress.close();
					}
				});
			}
		};

		Thread thread = new Thread(task);
		thread.setDaemon(true);
		thread.start();

		progress.showAndWait();
		
		return result[0];

	}

}
