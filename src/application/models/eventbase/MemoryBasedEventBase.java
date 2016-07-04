package application.models.eventbase;

public class MemoryBasedEventBase extends AbstrEventBase {

	public MemoryBasedEventBase(String filePath, String dbName) {
		// db name is ignored, since it is not stored in disk
		super(filePath, "memory:");
	}

}
