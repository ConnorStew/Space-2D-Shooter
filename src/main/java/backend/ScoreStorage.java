package backend;

import java.io.*;
import java.util.ArrayList;

/**
 * Stores functions to interact with the highscores database.
 * @author Connor Stewart
 */
public class ScoreStorage {

	private static final String fileName = "scores.txt";
	
	/**
	 * Writes a new score to the text file.
	 * @param name a three letter name for the score
	 * @param score the score
	 */
	public void writeScore(String name, int score) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true));
			writer.newLine();
			writer.append(name + ":" + score);
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
			fileExists();
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			while (reader.ready()) {
				String line = reader.readLine();
				scores.add(Integer.parseInt(line.substring(line.indexOf(":") + 1)));
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
			fileExists();
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

	/**
	 * Checks if the text file exists and creates it if it doesn't.
	 */
	private void fileExists() throws IOException {
		File scoreFile = new File(fileName);
		if (!scoreFile.exists())
			scoreFile.createNewFile();
	}

}
