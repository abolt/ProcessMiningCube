package application.operations.io;

import java.io.File;

import org.deckfour.xes.model.XLog;

import application.controllers.mapping.MappingRow;
import javafx.collections.ObservableList;

public abstract class Importer {

	protected File file;
	
	protected Importer(File in){
		file = in;
	}
	public abstract boolean canParse();
	public abstract XLog importFromFile();
	public abstract ObservableList<MappingRow> getSampleList();
}
