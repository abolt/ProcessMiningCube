package application.operations.io.log;

import java.io.File;

import application.models.eventlog.CSVFile;
import application.operations.io.Importer;

public class CSVImporter extends Importer<CSVFile>{

	public CSVImporter(File in) {
		super(in);
	}

	@Override
	public boolean canParse() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CSVFile importFromFile() {
		// TODO Auto-generated method stub
		return null;
	}

}
