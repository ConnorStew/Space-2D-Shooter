package ui;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import networking.server.Server;

public class Driver {
	
	public static void main(String[] args) {

		if (args.length > 0 && args[0].equals("server")) {
			Server.getInstance(); //start the server
		} else { //start the game
			LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
			config.title = "Space Defence";
			config.width = 900;
			config.height = 700;
			//config.fullscreen = true;
			new LwjglApplication(UI.getInstance(), config);
		}

	}
	
}
