package ui;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esotericsoftware.minlog.Log;
import database.ScoreDAO;
import network.server.ServerHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Driver {

	public static void main(String[] args) {
		Log.set(Log.LEVEL_DEBUG);

		if (args.length > 0 && args[0].equals("server")) {
			ServerHandler.getInstance();
		} else { //start the game
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "Space Defence";
			config.width = 900;
			config.height = 700;
			new LwjglApplication(ControlGame.getInstance(), config);
		}

	}
	
}
