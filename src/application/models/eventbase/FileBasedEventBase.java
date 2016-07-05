package application.models.eventbase;

import java.io.File;

import application.models.cube.CubeStructure;

public class FileBasedEventBase extends AbstrEventBase {

	public FileBasedEventBase(String filePath, String dbName, CubeStructure cube) {
		super(filePath, System.getProperty("user.home") + File.separator + dbName, cube);
	}
}
