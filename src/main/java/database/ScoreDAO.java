package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Stores functions to interact with the highscores database.
 * @author Connor Stewart
 */
public class ScoreDAO {
	
	/**
	 * Uploads a new score to the server.
	 * @param name a three letter name for the score
	 * @param score the score
	 */
	public void uploadScore(String name, int score) {
		if (DatabaseConnectionManager.exacuteSQLUpdate("INSERT INTO highscores (name, score) VALUES ('" + name + "', " + score + ");"))
			System.out.println("Score uploaded!");
	}
	
	/**
	 * Gets all scores from the score column in the database.
	 * @return the scores
	 */
	public ArrayList<Integer> getScores() {
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		ResultSet result = DatabaseConnectionManager.exacuteSQLQuery("SELECT * FROM highscores ORDER BY score DESC;");
		
		if (result == null)
			return null;
		
		try {
			result.next();
			while(!result.isAfterLast()) {
				scores.add(result.getInt(2));
				result.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DatabaseConnectionManager.disconnect();
		
		return scores;
	}

	/**
	 * Get all names from the name column in the database.
	 * @return the names
	 */
	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		
		ResultSet result = DatabaseConnectionManager.exacuteSQLQuery("SELECT * FROM highscores ORDER BY score DESC;");
		
		if (result == null)
			return null;
		
		try {
			result.next();
			
			while(!result.isAfterLast()) {
				names.add(result.getString(1));
				result.next();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		DatabaseConnectionManager.disconnect();
		
		return names;
	}

}
