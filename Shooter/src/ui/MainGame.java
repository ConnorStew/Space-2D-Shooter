package ui;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;

/**
 * Handles displaying the current screen in the game.
 * @author Connor Stewart
 */
public class MainGame extends Game {
	
	/** Screen code for the game screen. */
	public static final int GAME_SCREEN = 0;
	
	/** Screen code for the menu screen. */
	public static final int MENU_SCREEN = 1;
	
	/** Screen code for the score screen. */
	public static final int SCORE_SCREEN = 2;
	
	/** Singleton instance of the main game. */
	private static final MainGame instance = new MainGame();
	
	private MainGame(){};
	
	@Override
	public void create() {
		//default to the menu screen
		setScreen(MenuScreen.getInstance());
	}
	
	public void render() {
		getScreen().render(Gdx.graphics.getDeltaTime());
	}
	
	public void dispose() {
		getScreen().dispose();
	}
	
	/**
	 * Changes the screen currently being displayed by the game.
	 * @param screenCode a screen code
	 */
	public static void changeScreen(int screenCode) {
		switch (screenCode) {
			case 0:
				instance.setScreen(GameScreen.getInstance());
				break;
			case 1:
				instance.setScreen(MenuScreen.getInstance());
				break;
			case 2:
				instance.setScreen(ScoreScreen.getInstance());
				break;
		}
	}

	/**
	 * @return this classes singleton instance
	 */
	public static ApplicationListener getInstance() {
		return instance;
	}

}
