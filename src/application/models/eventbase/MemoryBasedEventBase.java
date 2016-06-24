package application.models.eventbase;

import org.deckfour.xes.model.XLog;
import org.processmining.log.csv.CSVFile;

public class MemoryBasedEventBase extends AbstrEventBase {

	public MemoryBasedEventBase(CSVFile file) {
		super("memory:", file);
	}

	public MemoryBasedEventBase(XLog file) {
		super("memory:", file);
	}
}
