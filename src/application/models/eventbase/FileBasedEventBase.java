package application.models.eventbase;

import java.io.File;

import org.deckfour.xes.model.XLog;
import org.processmining.log.csv.CSVFile;

public class FileBasedEventBase extends AbstrEventBase {

	public FileBasedEventBase(CSVFile file, String name) {
		super(System.getProperty("user.home") + File.separator + name, file);
	}

	public FileBasedEventBase(XLog file, String name) {
		super(System.getProperty("user.home") + File.separator + name, file);
	}
	//
}
