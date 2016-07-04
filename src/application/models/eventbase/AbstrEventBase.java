package application.models.eventbase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Collection;

import org.deckfour.xes.model.XEvent;

public class AbstrEventBase {

	private String dbPath;
	private String filePath;
	private Connection conn;

	public AbstrEventBase() {
	}

	public AbstrEventBase(String filePath, String dbPath) {
		this.dbPath = dbPath;
		this.filePath = filePath;
		conn = initializeDB(dbPath);

		if (filePath.endsWith(".csv"))
			fillDbFromCSV();
		else
			fillDbFromXES();
	}

	private Connection initializeDB(String dbPath) {
		Connection c = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return c;
	}
	
	public Collection<XEvent> getEvents(String query){
		return null;
	}

	private void fillDbFromCSV() {

	}

	private void fillDbFromXES() {
		
	}

}
