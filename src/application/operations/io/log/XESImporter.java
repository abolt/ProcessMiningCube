package application.operations.io.log;

import java.io.File;
import java.util.Collection;

import org.deckfour.xes.in.XUniversalParser;
import org.deckfour.xes.model.XLog;

import application.operations.io.Importer;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class XESImporter extends Importer<XLog> {

	public XESImporter(File in) {
		super(in);
	}

	@Override
	public boolean canParse() {
		XUniversalParser parser = new XUniversalParser();
		if (parser.canParse(file))
			return true;
		else
			return false;
	}

	@Override
	public XLog importFromFile() {
		XLog log = null;
		XUniversalParser parser = new XUniversalParser();
		Collection<XLog> collection;
		try {
			collection = parser.parse(file);
		} catch (Exception e) {
			e.printStackTrace();
			logErrorMessage();
			return null;
		}
		log = !collection.isEmpty() ? collection.iterator().next() : null;
		return log;
	}
	
	protected void logErrorMessage() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Invalid Event Log");
		alert.setContentText(
				"The event log that you are trying to import is not valid. Please check that you are using the right one!");
		alert.showAndWait();
	}

}
