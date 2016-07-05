package application.models.eventbase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;

import application.models.cube.CubeStructure;
import application.models.dimension.Attribute;
import application.operations.io.log.XESImporter;

public class AbstrEventBase {

	private String dbPath;
	private String filePath;

	private Map<Long, XEvent> eventMap; // to stores all the event objects

	public AbstrEventBase(String filePath, String dbPath, List<Attribute> allAttributes) {
		this.dbPath = dbPath;
		this.filePath = filePath;
		eventMap = new HashMap<Long, XEvent>();

		if (filePath.endsWith(".csv")) // fill the eventMap
			fillDbFromCSV();
		else
			fillDbFromXES();
		
		populateDB(allAttributes);
	}

	private synchronized void populateDB(List<Attribute> attributes) {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			Statement s = c.createStatement();

			String sqlCreate = "CREATE TABLE EVENTS (ID INTEGER PRIMARY KEY NOT NULL";

			for (Attribute att : attributes)
				if (!att.getType().equals(Attribute.IGNORE))
					sqlCreate = sqlCreate + ", " + att.getAttributeName() + " " + att.getType();

			sqlCreate = sqlCreate + ")";
			s.executeQuery(sqlCreate);

			String sqlInsertHeader = "INSERT INTO EVENTS (ID";

			for (Attribute att : attributes)
				if (!att.getType().equals(Attribute.IGNORE))
					sqlInsertHeader = sqlInsertHeader + ", " + att.getAttributeName();

			sqlInsertHeader = sqlInsertHeader + ") VALUES ";

			String sqlInsertBatch = "";
			long size = eventMap.keySet().size();

			for (long index : eventMap.keySet()) {
				
				sqlInsertBatch = sqlInsertBatch + "(" + index;
				XEvent event = eventMap.get(index);
				
				for (Attribute att : attributes)
					if (!att.getType().equals(Attribute.IGNORE))
						sqlInsertBatch = sqlInsertBatch + ", "
								+ event.getAttributes().get(att.getAttributeName()).toString();
				sqlInsertBatch = sqlInsertBatch + ")";
				
				if (index < size) // all except the last that has a semicolon
					sqlInsertBatch = sqlInsertBatch + ", ";
			}
			sqlInsertBatch = sqlInsertBatch + ";";

			s.executeQuery(sqlInsertHeader + sqlInsertBatch);
			
			s.close();
			c.close();

			System.out.println(sqlInsertHeader);
			System.out.println(sqlInsertBatch);
			
		} catch (Exception e) {
			System.out.println("Error! oooops....");
			e.printStackTrace();
		}

	}

	public Collection<XEvent> getEvents(String query) {
		return null;
	}

	private void fillDbFromCSV() {
		
		
	}

	private void fillDbFromXES() {
		XESImporter importer = new XESImporter(new File(filePath));
		importer.importFromFile();
	}

}
