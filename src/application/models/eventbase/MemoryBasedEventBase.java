package application.models.eventbase;

import java.util.List;

import application.models.attribute.abstr.Attribute;

public class MemoryBasedEventBase extends AbstrEventBase {

	public MemoryBasedEventBase(String filePath, String dbName, List<Attribute<?>> allAttributes) {
		// db name is ignored, since it is not stored in disk
		super(filePath, "memory:", allAttributes);
	}

}
