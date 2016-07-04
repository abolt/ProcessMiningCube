package application.models.eventbase;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.log.csv.CSVFile;

public class FileBasedEventBase extends AbstrEventBase {

	public FileBasedEventBase(String filePath, String dbName) {
		super(filePath, System.getProperty("user.home") + File.separator + dbName);
	}
}
