package application.operations.io;

import java.io.File;

public abstract class Importer<T> {

	protected File file;
	
	protected Importer(File in){
		file = in;
	}
	public abstract boolean canParse();
	public abstract T importFromFile();
}
