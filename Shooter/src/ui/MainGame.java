package ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/**
 * Sets GameScreen as the main screen for the game and renders it.
 * @author Connor Stewart
 */
public class MainGame extends Game {
	
	@Override
	public void create() {
		setScreen(GameScreen.getInstance());
	}
	
	public void render() {
		getScreen().render(Gdx.graphics.getDeltaTime());
	}
	
	public void dispose() {
		getScreen().dispose();
	}

}
