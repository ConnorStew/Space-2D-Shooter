package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import network.client.ClientHandler;

/**
 * This class is used to define the main menu layout.
 * @author Connor Stewart
 */
public class MenuScreen extends UIScreen {
		
	/** Buttons. */
	private TextButton btnPlay, btnQuit, btnMultiplayer;

	public void show() {
		super.show();

		//make background
		Image background = new Image(new Texture(Gdx.files.internal("backgrounds/hubble.jpg")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		//initialising the buttons
		btnPlay = new TextButton("Play", buttonStyle);
		btnPlay.setPosition(Gdx.graphics.getWidth() / 2 - btnPlay.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 20);
		btnQuit = new TextButton("Quit", buttonStyle);
		btnQuit.setPosition((btnPlay.getX() + btnQuit.getWidth() / 8) - 10, btnPlay.getY() - btnPlay.getHeight() - 20);
		btnMultiplayer = new TextButton("Multiplayer", buttonStyle);

		//initialising the lose label
		Label lblTitle = new Label("Space Defence", labelStyle);
		lblTitle.setPosition((Gdx.graphics.getWidth() / 2) - lblTitle.getWidth() / 2, Gdx.graphics.getHeight() - 100);

		//adding actors to the stage
		stage.addActor(background);
		stage.addActor(lblTitle);
		stage.addActor(btnPlay);
		stage.addActor(btnQuit);
		stage.addActor(btnMultiplayer);
	}

	public void render(float delta) {
		super.render(delta);

		//goto the game screen if the play button is pressed
		if (btnPlay.isPressed() && validateButtonPress())
			ControlGame.getInstance().setScreen(new SPGame());
		
		//goto the multiplayer screen if the multiplayer button is pressed
		if (btnMultiplayer.isPressed() && validateButtonPress())
			new ClientHandler(); //attempt to connect to the server

			
		//quit when the quit button is pressed
		if (btnQuit.isPressed() && validateButtonPress())
			Gdx.app.exit();
	}
}
