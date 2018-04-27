package database;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Stores functions to interact with the highscores database.
 * @author Connor Stewart
 */
public class ScoreDAO {

	private static final String fileName = "scores.txt";
	
	/**
	 * Writes a new score to the text file.
	 * @param name a three letter name for the score
	 * @param score the score
	 */
	public void writeScore(String name, int score) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
			writer.append(name + ":" + score);
			writer.newLine();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets all scores from the score column in the database.
	 * @return the scores
	 */
	public ArrayList<Integer> getScores() {
		ArrayList<Integer> scores = new ArrayList<Integer>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while (reader.ready()) {
				String line = reader.readLine();
				scores.add(Integer.parseInt(line.substring(line.indexOf(":"))));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return scores;
	}

	/**
	 * Get all names from the name column in the database.
	 * @return the names
	 */
	public ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while (reader.ready()) {
				String line = reader.readLine();
				names.add(line.substring(0,line.indexOf(":")));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return names;
	}

}
