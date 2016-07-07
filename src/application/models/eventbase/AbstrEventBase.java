package application.models.eventbase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;

import application.controllers.wizard.steps.MappingController;
import application.models.cube.CubeStructure;
import application.models.dimension.Attribute;
import application.operations.io.log.CSVImporter;
import application.operations.io.log.XESImporter;

public class AbstrEventBase {

	private String dbPath;
	private String filePath;
	private int numAttributes;

	private Map<Long, XEvent> eventMap; // to stores all the event objects

	public AbstrEventBase(String filePath, String dbPath, List<Attribute> allAttributes) {
		this.dbPath = dbPath;
		this.filePath = filePath;
		this.numAttributes = 0;
		eventMap = new HashMap<Long, XEvent>();

		if (filePath.endsWith(".csv")) // fill the eventMap
			fillDbFromCSV(allAttributes);
		else
			fillDbFromXES();

		populateDB(allAttributes);
	}

	private synchronized void populateDB(List<Attribute> attributes) {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

			c.setAutoCommit(false);
			Statement s = c.createStatement();
			// drop existing table if existed
			s.executeUpdate("DROP TABLE IF EXISTS EVENTS");
			c.commit();

			String sqlCreate = "CREATE TABLE EVENTS (ID INTEGER PRIMARY KEY NOT NULL";

			for (Attribute att : attributes)
				if (!att.getType().equals(Attribute.IGNORE)){
					sqlCreate = sqlCreate + ", \"" + att.getAttributeName() + "\" " + att.getType();
					numAttributes++;
				}

			sqlCreate = sqlCreate + ")";
			s.executeUpdate(sqlCreate);
			c.commit();

			String sqlInsertHeader = "INSERT INTO EVENTS (ID";

			for (Attribute att : attributes)
				if (!att.getType().equals(Attribute.IGNORE))
					sqlInsertHeader = sqlInsertHeader + ", \"" + att.getAttributeName() + "\"";

			sqlInsertHeader = sqlInsertHeader + ") VALUES ";

			String sqlInsertBatch = "";

			int batchSize = 100;// rows for the same insert
			long size = eventMap.keySet().size();

			for (long index : eventMap.keySet()) {

				sqlInsertBatch = sqlInsertBatch + "('" + index;
				XEvent event = eventMap.get(index);

				for (Attribute att : attributes) {
					if (!att.getType().equals(Attribute.IGNORE)
							&& event.getAttributes().containsKey(att.getAttributeName()))
						sqlInsertBatch = sqlInsertBatch + "', '"
								+ event.getAttributes().get(att.getAttributeName()).toString();
					else
						sqlInsertBatch = sqlInsertBatch + "', 'NULL";
				}
				sqlInsertBatch = sqlInsertBatch + "')";

				if ((index % 100 == 0 || index == size - 1) && index > 0) { // run
																			// the
																			// batch
					s.executeUpdate(sqlInsertHeader + sqlInsertBatch);
					c.commit();
					sqlInsertBatch = "";
				} else if (index < size)
					// all except the last that has a semicolon
					sqlInsertBatch = sqlInsertBatch + ", ";

			}
			s.close();
			c.close();
		} catch (Exception e) {
			System.out.println("Error! oooops.... \n");
			e.printStackTrace();
		}

	}

	public static boolean dbExists(String path) {
		File f = new File(path);
		return f.exists();
	}

	public Collection<XEvent> getEvents(String query) {
		//returns the events (pointers) that are the result of this query
		return null;
	}

	public void buildEvents() {
		try {

			Map<String, String> columns = new HashMap<String, String>();
			List<String> orderedAttributes = new ArrayList<String>();

			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			Statement s = c.createStatement();
			// drop existing table if existed
			ResultSet rs = s.executeQuery("SELECT * FROM EVENTS");

			ResultSetMetaData rsmd = rs.getMetaData();

			for (int i = 0; i < rsmd.getColumnCount(); i++) {
				columns.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
				orderedAttributes.add(rsmd.getColumnName(i));
			}
			List<DateFormat> df = new Vector<DateFormat>();
			while (rs.next()) {
				XAttributeMap attMap = new XAttributeMapImpl();
				for (int i = 1; i < orderedAttributes.size(); i++)
					//first column is always the index
					switch (columns.get(orderedAttributes.get(i))) {
					case Attribute.DISCRETE:
						attMap.put(orderedAttributes.get(i),
								new XAttributeDiscreteImpl(orderedAttributes.get(i), rs.getInt(i)));
						break;
					case Attribute.CONTINUOUS:
						attMap.put(orderedAttributes.get(i),
								new XAttributeContinuousImpl(orderedAttributes.get(i), rs.getDouble(i)));
						break;
					case Attribute.TEXT:
						attMap.put(orderedAttributes.get(i),
								new XAttributeLiteralImpl(orderedAttributes.get(i), rs.getString(i)));
						break;
					case Attribute.DATE_TIME:
						if (df.get(i) == null)
							df.add(i, MappingController.detectTimestampParser(rs.getString(i)));
						attMap.put(orderedAttributes.get(i), new XAttributeTimestampImpl(orderedAttributes.get(i),
								df.get(i).parse(rs.getString(i)).getTime()));
						break;
					}
				eventMap.put(rs.getLong(0), new XEventImpl(attMap));
			}
			s.close();
			c.close();
		} catch (Exception e) {
			System.out.println("Error! oooops.... \n");
			e.printStackTrace();
		}
	}

	private void fillDbFromCSV(List<Attribute> attributes) {
		CSVImporter importer = new CSVImporter(new File(filePath));
		fillMap(importer.getEventList(-1, attributes));

	}

	private void fillDbFromXES() {
		XESImporter importer = new XESImporter(new File(filePath));
		fillMap(importer.getEventList(-1, null));
	}

	private void fillMap(List<XEvent> events) {
		long i = 0;
		for (XEvent event : events) {
			eventMap.put(i, event);
			i++;
		}
	}
	
	public long getNumberofEvents(){
		return eventMap.size();
	}
	
	/**
	 * 
	 * @return The number of attributes of the first existing event
	 */
	public int getNumberOfAttributes(){
		return numAttributes;
	}
	public String getName(){
		return dbPath.substring(dbPath.lastIndexOf(File.separator)+1).replace(".db", "");
	}

}
