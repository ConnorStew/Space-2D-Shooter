package ui;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.esotericsoftware.minlog.Log;
import database.ScoreStorage;
import network.server.ServerHandler;

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
