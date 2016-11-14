package application.controllers.results;

import java.awt.Dimension;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.deckfour.xes.model.XLog;

import application.operations.io.log.XESExporter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ResultDialogController extends BorderPane {

	private XLog log;

	@FXML
	private ScrollPane scrollPane;

	public ResultDialogController(XLog log, JComponent result) {

		this.log = log;
		FXMLLoader fxmlLoader = new FXMLLoader(
				this.getClass().getResource("/application/views/results/ResultDialog.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		result.setPreferredSize(new Dimension(1000, 700));
		final SwingNode adapter = new SwingNode();
		createSwingContent(adapter, result);
		setCenter(adapter);

		final Stage wizard = new Stage();
		
		wizard.initOwner(null);
		Scene dialogScene = new Scene(this, 800, 800);
		wizard.setScene(dialogScene);
		wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
		wizard.setTitle("Results Viewer");
		
		final ChangeListener<Number> listener = new ChangeListener<Number>()
		{
		  final Timer timer = new Timer(); // uses a timer to call your resize method
		  TimerTask task = null; // task to execute after defined delay
		  final long delayTime = 200; // delay that has to pass in order to consider an operation done

		  @Override
		  public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue)
		  {
		    if (task != null)
		    { // there was already a task scheduled from the previous operation ...
		      task.cancel(); // cancel it, we have a new size to consider
		    }

		    task = new TimerTask() // create new task that calls your resize operation
		    {
		      @Override
		      public void run()
		      { 
		    	  result.doLayout();
		    	  result.repaint();
		      }
		    };
		    // schedule new task
		    timer.schedule(task, delayTime);
		  }
		};
		wizard.widthProperty().addListener(listener);
		wizard.heightProperty().addListener(listener);
		
		wizard.show();
		wizard.centerOnScreen();
		
		
		
		
	}

	@FXML
	protected void exportEventLog() {
		XESExporter exporter = new XESExporter();
		exporter.exportLog(log);
	}

	@FXML
	protected void exportImage() {

	}

	private void createSwingContent(final SwingNode swingNode, JComponent content) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				swingNode.setContent(content);
				swingNode.autosize();
				content.repaint();
			}
		});
	}
	
	
}
