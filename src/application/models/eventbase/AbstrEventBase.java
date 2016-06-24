package application.models.eventbase;

import java.sql.Connection;
import java.sql.DriverManager;

import org.deckfour.xes.model.XLog;
import org.processmining.log.csv.CSVFile;

public class AbstrEventBase {

	private String dbPath;
	private Connection conn;

	public AbstrEventBase() {
	}

	public AbstrEventBase(String path, CSVFile data) {
		dbPath = path;
		conn = initializeDB();
	}

	public AbstrEventBase(String path, XLog data) {
		dbPath = path;
		conn = initializeDB();
	}

	private Connection initializeDB() {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:dbPath");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}

}
