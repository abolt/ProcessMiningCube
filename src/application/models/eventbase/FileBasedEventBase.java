package application.models.eventbase;

import java.io.File;
import java.util.List;

import application.models.attribute.abstr.Attribute;

public class FileBasedEventBase extends AbstrEventBase {

	public FileBasedEventBase(String filePath, String dbName, List<Attribute<?>> allAttributes) {
		super(filePath, System.getProperty("user.home") + File.separator + dbName + ".db", allAttributes);
	}
}
