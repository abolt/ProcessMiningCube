package application.operations.io;

import java.io.File;
import java.util.List;

import org.deckfour.xes.model.XEvent;

import application.models.attribute.abstr.Attribute;
import application.models.wizard.MappingRow;
import javafx.collections.ObservableList;

public abstract class Importer {

	protected File file;

	protected Importer(File in) {
		file = in;
	}

	public abstract ObservableList<MappingRow> getSampleList();

	public abstract List<XEvent> getEventList(long size, List<Attribute> attributes);
}
