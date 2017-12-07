package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Stores functions to access the highscores table in the shooter database.
 * @author Connor Stewart
 */
public class ScoreDAO {
	
	/**
	 * Uploads a new score to the server. <br>
	 * <b>!!</b> Score will <u>NOT</u> be uploaded if the name parameter is above three characters! <b>!!</b> 
	 * @param name a three letter name for the score
	 * @param score the score
	 */
	public void uploadeScore(String name, int score) {
		if (name.length() > 3) {
			System.out.println("Name too long!");
			return;
		}
		
		if (DatabaseConnectionManager.exacuteSQLUpdate("INSERT INTO highscores (name, score) VALUES ('" + name + "', " + score + ");"))
			System.out.println("Score uploaded!");
	}
	
	public ArrayList<Integer> getScores() {
		ArrayList<Integer> scores = new ArrayList<Integer>();
		
		ResultSet result = DatabaseConnectionManager.exacuteSQLQuery("SELECT * FROM highscores;");
		
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

	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();
		
		ResultSet result = DatabaseConnectionManager.exacuteSQLQuery("SELECT * FROM highscores;");
		
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
