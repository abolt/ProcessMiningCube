package application.models.eventbase;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.apache.commons.collections15.MapIterator;
import org.apache.commons.collections15.keyvalue.MultiKey;
import org.apache.commons.collections15.map.MultiKeyMap;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.impl.XAttributeContinuousImpl;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.deckfour.xes.model.impl.XEventImpl;

import application.controllers.wizard.steps.MappingController;
import application.models.attribute.abstr.Attribute;
import application.models.eventbase.conditions.Condition;
import application.models.metric.Metric;
import application.operations.io.log.CSVImporter;
import application.operations.io.log.XESImporter;

public class AbstrEventBase {

	private String dbPath;
	private String filePath;
	private int numAttributes;

	private Map<Long, XEvent> eventMap; // to stores all the event objects

	public AbstrEventBase(String filePath, String dbPath, List<Attribute<?>> allAttributes) {
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

	private synchronized void populateDB(List<Attribute<?>> attributes) {

		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

			c.setAutoCommit(false);
			Statement s = c.createStatement();
			// drop existing table if existed
			s.executeUpdate("DROP TABLE IF EXISTS EVENTS");
			c.commit();

			String sqlCreate = "CREATE TABLE EVENTS (ID INTEGER PRIMARY KEY NOT NULL";

			for (Attribute<?> att : attributes)
				if (!att.getType().equals(Attribute.IGNORE)) {
					sqlCreate = sqlCreate + ", \"" + att.getName() + "\" " + att.getType();
					numAttributes++;
				}

			sqlCreate = sqlCreate + ")";
			s.executeUpdate(sqlCreate);
			c.commit();

			String sqlInsertHeader = "INSERT INTO EVENTS (ID";

			for (Attribute<?> att : attributes)
				if (!att.getType().equals(Attribute.IGNORE))
					sqlInsertHeader = sqlInsertHeader + ", \"" + att.getName() + "\"";

			sqlInsertHeader = sqlInsertHeader + ") VALUES ";

			String sqlInsertBatch = "";

			int batchSize = 100;// rows for the same insert
			long size = eventMap.keySet().size();

			for (long index : eventMap.keySet()) {

				sqlInsertBatch = sqlInsertBatch + "('" + index;
				XEvent event = eventMap.get(index);

				for (Attribute<?> att : attributes) {
					if (!att.getType().equals(Attribute.IGNORE) && event.getAttributes().containsKey(att.getName()))
						sqlInsertBatch = sqlInsertBatch + "', '" + event.getAttributes().get(att.getName()).toString();
					else
						sqlInsertBatch = sqlInsertBatch + "', 'NULL";
				}
				sqlInsertBatch = sqlInsertBatch + "')";

				if ((index % batchSize == 0 || index == size - 1) && index > 0) { // run
					// the
					// batch
					s.executeUpdate(sqlInsertHeader + sqlInsertBatch);

					sqlInsertBatch = "";
				} else if (index < size)
					// all except the last that has a semicolon
					sqlInsertBatch = sqlInsertBatch + ", ";

			}
			c.commit();
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

	public List<XEvent> getEvents(List<Condition> conditions) {

		List<XEvent> result = new ArrayList<XEvent>();

		String sql = "SELECT ID FROM EVENTS WHERE ";
		Iterator<Condition> iterator = conditions.iterator();

		while (iterator.hasNext()) {
			Condition item = iterator.next();
			sql = sql + item.getConditionAsString();
			if (iterator.hasNext())
				sql = sql + " AND ";
		}

		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			Statement s = c.createStatement();
			// drop existing table if existed
			ResultSet rs = s.executeQuery(sql);
			while (rs.next()) {
				result.add(eventMap.get(rs.getString(1)));
			}
			s.close();
			c.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// returns the events (pointers) that are the result of this query

		return result;
	}

	/**
	 * This method gets a map where the multikey has the matrix coordinates and
	 * the value has the conditions that that cell has to satisfy.
	 * 
	 * @param conditionMatrix
	 * @return a map with, for each coordinate (multikey) gives the value that
	 *         should go to that cell
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public MultiKeyMap query(MultiKeyMap conditionMatrix, int numCells, int numConditionsPerCell, Metric metric) {

		MultiKeyMap result = new MultiKeyMap();

		int numConditions = numConditionsPerCell;
		int batchSize = 19;

		// initialize DB connection
		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);

			/**
			 * First we create a (multi-column) index on the table with the
			 * attributes that we will use.
			 * 
			 * 
			 */
			Statement createIndex = c.createStatement();
			createIndex.executeUpdate("DROP INDEX IF EXISTS \"index\"");
			String createIndexQuery = "CREATE INDEX \"index\" ON EVENTS(";
			for (int i = 0; i < numConditions; i++) {
				Attribute<?> attribute = ((List<Condition>) conditionMatrix.get(0, 0)).get(i).getAttribute();
				if (attribute.getParent() != null)
					createIndexQuery = createIndexQuery + attribute.getParent().getQueryString();
				else
					createIndexQuery = createIndexQuery + attribute.getQueryString();
				if (i < numConditions - 1)
					createIndexQuery = createIndexQuery + ", ";
			}
			createIndexQuery = createIndexQuery + ")";
			createIndex.executeUpdate(createIndexQuery);

			String whereSQL = " FROM EVENTS WHERE ";
			for (int i = 0; i < numConditions; i++) {
				whereSQL = whereSQL
						+ ((List<Condition>) conditionMatrix.get(0, 0)).get(i).getAttribute().getQueryString() + " = ?";
				if (i < numConditions - 1)
					whereSQL = whereSQL + " AND ";
			}

			// the string with the complete query

			String coreSql = "SELECT ?, ?, ";
			switch (metric.toString()) {
			case Metric.eventCount:
				coreSql = coreSql + "COUNT(ID)" + whereSQL;
				break;
			case Metric.caseCount:
				coreSql = coreSql + "COUNT(DISTINCT \"" + metric.getCaseID() + "\")" + whereSQL;
				break;
			case Metric.avgCaseLength:
				coreSql = coreSql + "AVG(counter) FROM (sElect COUNT(ID) AS counter FROM EVENTS WHERE \""
						+ metric.getCaseID() + "\" IN (seLect DISTINCT \"" + metric.getCaseID() + "\"" + whereSQL
						+ ") GROUP BY \"" + metric.getCaseID() + "\")";
				break;
			}
			// System.out.println(coreSql);

			PreparedStatement s = null;

			MapIterator it = conditionMatrix.mapIterator();
			int numQueries = 0;
			while (it.hasNext()) {
				it.next();
				numQueries++;
			}
			it = conditionMatrix.mapIterator();
			int counter = 0;
			int index = 1;

			if (numQueries < batchSize)
				batchSize = numQueries;
			s = c.prepareStatement(getFullSql(batchSize, coreSql));

			while (it.hasNext()) {
				it.next();
				counter++;
				MultiKey mk = (MultiKey) it.getKey();
				int i = (int) mk.getKey(0);
				int j = (int) mk.getKey(1);
				List<Condition> conditions = (List<Condition>) it.getValue();

				s.setInt(index++, i);
				s.setInt(index++, j);

				for (Condition condition : conditions)
					s.setString(index++, condition.getValue());

				if (counter % batchSize == 0) {// reached batch limit
					ResultSet rs = s.executeQuery();
					while (rs.next())
						result.put(rs.getInt(1), rs.getInt(2), rs.getInt(3));
					index = 1;// go back to the beginning
					s.clearParameters();
					counter = 0;
					numQueries = numQueries - batchSize;
					if (numQueries < batchSize && numQueries > 0) {
						batchSize = numQueries;
						s.close();
						s = c.prepareStatement(getFullSql(batchSize, coreSql));
					}

				}
				;
			}
			// if there is a remainder of the batches...
			if (counter > 0) {
				System.out.println("There were rows not processed!");
			}
			s.close();
			c.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// returns the events (pointers) that are the result of this query

		return result;
	}

	private String getFullSql(int batchSize, String coreSql) {
		String sqlQuery = "";
		boolean first = true;

		for (int n = 0; n < batchSize; n++) {

			if (!first)
				sqlQuery = sqlQuery + " UNION ";
			else
				first = false;

			sqlQuery = sqlQuery + coreSql;
		}
		return sqlQuery;
	}

	public Set<String> getValueSet(String attributeName) {
		Set<String> valueSet = new TreeSet<String>();

		try {
			Class.forName("org.sqlite.JDBC");
			Connection c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
			Statement s = c.createStatement();
			// if the attribute is a date, get the range (min and max)

			// if the attribute is numerical, get the range (min and max)

			// if the attribute is text, get the complete set of distinct
			// elements
			ResultSet rs = s.executeQuery("SELECT DISTINCT \"" + attributeName + "\" FROM EVENTS");
			while (rs.next()) {
				valueSet.add(rs.getString(1));
			}
			s.close();
			c.close();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return valueSet;
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

			for (int i = 1; i <= rsmd.getColumnCount(); i++) {
				columns.put(rsmd.getColumnName(i), rsmd.getColumnTypeName(i));
				orderedAttributes.add(rsmd.getColumnName(i));
			}
			List<DateFormat> df = new Vector<DateFormat>();
			while (rs.next()) {
				XAttributeMap attMap = new XAttributeMapImpl();
				for (int i = 1; i < orderedAttributes.size(); i++)
					// first column is always the index
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

	private void fillDbFromCSV(List<Attribute<?>> attributes) {
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

	public long getNumberofEvents() {
		return eventMap.size();
	}

	/**
	 * 
	 * @return The number of attributes of the first existing event
	 */
	public int getNumberOfAttributes() {
		return numAttributes;
	}

	public String getName() {
		return dbPath.substring(dbPath.lastIndexOf(File.separator) + 1).replace(".db", "");
	}

}
