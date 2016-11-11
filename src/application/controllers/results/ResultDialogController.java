package application.controllers.results;

import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import org.deckfour.xes.model.XLog;

import application.operations.io.log.XESExporter;
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
		// wizard.initModality(Modality.APPLICATION_MODAL);
		wizard.initOwner(null);
		Scene dialogScene = new Scene(this, 800, 800);
		wizard.setScene(dialogScene);
		wizard.getIcons().add(new Image(getClass().getResourceAsStream("/images/cube_black.png")));
		wizard.setTitle("Results Viewer");
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
