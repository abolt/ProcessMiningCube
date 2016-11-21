package application.controllers.workers;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.rapidprom.ioobjects.XLogIOObject;

import com.rapidminer.Process;
import com.rapidminer.RapidMiner;
import com.rapidminer.operator.IOContainer;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.OperatorException;
import com.rapidminer.tools.XMLException;

import application.operations.dialogs.DirectoryChooserDialog;
import application.prom.RapidProMGlobalContext;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ProgressBar;

public class RapidMinerWorker extends AbstrWorker {

	public RapidMinerWorker() {
		super();
	}

	@Override
	protected void initialize() {

		DirectoryChooserDialog directoryDialog = new DirectoryChooserDialog("RapidMiner Installation Folder",
				"RapidMiner will be initialized. \nPlease select the folder where RapidMiner is installed.",
				"Note: RapidMiner and the RapidProM extension must be installed in your computer!");
		Optional<File> directory = directoryDialog.showAndWait();

		if (!directory.isPresent())
			return;
		System.setProperty("rapidminer.home", directory.get().getAbsolutePath());

		Dialog<Void> progress = new Dialog<Void>();
		progress.setTitle("Initializing RapidMiner");
		progress.setHeaderText("Please wait while I initialize RapidMiner. \nThis may take a few seconds.");
		progress.getDialogPane().setContent(new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS));

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				RapidMiner.setExecutionMode(RapidMiner.ExecutionMode.COMMAND_LINE);
				RapidMiner.init();
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

	}

	@SuppressWarnings("unchecked")
	@Override
	public void run(XLog log, Object... objects) {

		Optional<File> selectedFile = (Optional<File>) objects[0];

		if (!selectedFile.isPresent()) {
			return;
		}

		Dialog<Void> progress = new Dialog<Void>();
		progress.setTitle("Executing Workflow");
		progress.setHeaderText("Please wait while I execute the workflow:\n" + selectedFile.get().getName());
		progress.getDialogPane().setContent(new ProgressBar(ProgressBar.INDETERMINATE_PROGRESS));

		Task<Void> task = new Task<Void>() {
			@Override
			protected Void call() throws Exception {
				PluginContext pluginContext = RapidProMGlobalContext.instance().getRapidProMPluginContext();

				Process process = null;
				try {
					process = new Process(selectedFile.get());
					IOObject object = new XLogIOObject(log, pluginContext);
					IOContainer container = new IOContainer(object);
					process.run(container);
				} catch (IOException | XMLException e) {
					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Not a valid workflow!");
					alert.setContentText("The selected file is not a workflow. Please check that it really is.");
					alert.showAndWait();
					call();
				} catch (OperatorException e) {

					e.printStackTrace();
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("Error");
					alert.setHeaderText("Something is wrong in the workflow!");
					alert.setContentText(
							"Your workflow did not run properly. Please make sure that it runs correctly in RapidMiner.");
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

	}

}
