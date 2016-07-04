package application.operations.io;

import java.io.File;

import org.deckfour.xes.model.XLog;

import application.models.eventbase.AbstrEventBase;
import application.models.wizard.MappingRow;
import javafx.collections.ObservableList;

public abstract class Importer {

	protected File file;
	
	protected Importer(File in){
		file = in;
	}
	public abstract AbstrEventBase importFromFile();
	public abstract ObservableList<MappingRow> getSampleList();
}
