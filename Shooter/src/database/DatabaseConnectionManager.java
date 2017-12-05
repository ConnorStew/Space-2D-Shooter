package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class manages connecting to the highscores database
 * @author Connor Stewart
 */
class DatabaseConnectionManager {

	/** The connection to the database. */
	private static Connection conn;

	/** Class should be accessed statically. */
	private DatabaseConnectionManager() {}

	/**
	 * Connects to the mySQL server.
	 */
	private static void connect() {
		try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/shooter?user=root&password=connor&useSSL=false");
			System.out.println("Established Database Connection!");
		} catch (SQLException e) {
			System.out.println("Error: Server offline.");
		}
	}

	/**
	 * Disconnect from the mySQL server.
	 */
	static void disconnect() {
		try {
			if (databaseOnline()) {
				conn.close();
				System.out.println("Disconnected from Database.");
			} else {
				System.out.println("Error: Cannot disconnect from database.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks if the database is reachable.
	 * @return whether the database can be reached
	 * @throws SQLException a generic SQL exception
	 */
	private static boolean databaseOnline() throws SQLException {
		if (conn == null)
			return false;
		else if (conn.isValid(2))
			return true;
		else
			return false;
	}
	
	/**
	 * Executes an update on the SQL database.
	 * @param sqlCommand the SQL command to execute
	 * @return if the update executed successfully
	 */
	static boolean exacuteSQLUpdate(String sqlCommand) {
		try {
			connect();
			if (databaseOnline()) {
				Statement s = conn.createStatement();
				s.executeUpdate(sqlCommand);
				disconnect();
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * Executes a query on the SQL database.
	 * @param sqlCommand the SQL command to execute
	 * @return the result set from the query or null if an error occured
	 */
	static ResultSet exacuteSQLQuery(String sqlCommand) {
		try {
			connect();
			if (databaseOnline()) {
				Statement s = conn.createStatement();
				ResultSet result = s.executeQuery(sqlCommand);
				return result;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}