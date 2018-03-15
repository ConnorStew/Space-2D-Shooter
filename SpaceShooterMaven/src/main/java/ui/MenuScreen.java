package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * This class is used to define the main menu layout.
 * @author Connor Stewart
 */
public class MenuScreen implements Screen {
	
	/** The singleton instance of this class. */
	private final static MenuScreen instance = new MenuScreen();
	
	/** The stage to display elements. */
	private Stage stage;
		
	/** Buttons. */
	private TextButton btnPlay, btnQuit, btnMultiplayer;
	
	/** {@link #getInstance()} should be used to obtain an instance of this class.  */
	private MenuScreen(){};

	public void show() {
		//make background
		Image background = new Image(new Texture(Gdx.files.internal("space.png")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		//initialising the buttons
		btnPlay = new TextButton("Play", UI.buttonStyle);
		btnPlay.setPosition(Gdx.graphics.getWidth() / 2 - btnPlay.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 20);
		btnQuit = new TextButton("Quit", UI.buttonStyle);
		btnQuit.setPosition((btnPlay.getX() + btnQuit.getWidth() / 8) - 10, btnPlay.getY() - btnPlay.getHeight() - 20);
		btnMultiplayer = new TextButton("Multiplayer", UI.buttonStyle);
		
		//initialising the stage which will stretch
		stage = new Stage(new StretchViewport(900, 700));
		
		//initialising the lose label
		Label lblTitle = new Label("Space Defence", UI.labelStyle);
		lblTitle.setPosition((Gdx.graphics.getWidth() / 2) - lblTitle.getWidth() / 2, Gdx.graphics.getHeight() - 100);
		
		//allowing the stage to receive input events
		Gdx.input.setInputProcessor(stage);
		
		//adding actors to the stage
		stage.addActor(background);
		stage.addActor(lblTitle);
		stage.addActor(btnPlay);
		stage.addActor(btnQuit);
		stage.addActor(btnMultiplayer);
	}

	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta); //update actors
		stage.draw(); //draw actors
		
		//goto the game screen if the play button is pressed
		if (btnPlay.isPressed())
			UI.getInstance().setScreen(new SPGame());
		
		//goto the multiplayer screen if the multiplayer button is pressed
		if (btnMultiplayer.isPressed()) 
			UI.getInstance().setScreen(MultiplayerScreen.getInstance());
			
		//quit when the quit button is pressed
		if (btnQuit.isPressed())
			Gdx.app.exit();
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	public void pause() {}

	public void resume() {}

	public void hide() {}

	public void dispose() {
		stage.dispose();
	}

	/**
	 * @return singleton instance of this class
	 */
	public static Screen getInstance() {
		return instance;
	}

}
