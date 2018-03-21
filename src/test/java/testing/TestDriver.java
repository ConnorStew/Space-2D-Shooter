package testing;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

public class TestDriver {

	public static void main(String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Space Defence";
		config.width = 900;
		config.height = 700;
		new LwjglApplication(new RectangleLOS(), config);
	}

}
