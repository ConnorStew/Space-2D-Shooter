package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import database.ScoreDAO;

/**
 * This class is used to show the user their score and allow them to upload theirs.
 * @author Connor Stewart
 */
public class ScoreScreen implements Screen {
	
	/** The singleton instance of this class. */
	private final static ScoreScreen instance = new ScoreScreen();
	
	/** The stage to display elements. */
	private Stage stage;
	
	/** Used to display the score to the user. */
	private Label lblScore;
	
	/** Buttons. */
	private TextButton btnPlay, btnQuit;
	
	/** Used to enter your name for the score board. */
	private TextField txtName;
	
	/** {@link #getInstance()} should be used to obtain an instance of this class.  */
	private ScoreScreen(){};

	@Override
	public void show() {
		//font generator
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Vector Waves.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 100; //setting font size
		
		//creating font object
		BitmapFont font = generator.generateFont(parameter);
		
		//getting rid of the generator since it will no longer be used
		generator.dispose();
		
		//set style for buttons
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.font = font;
		
		//initialising the buttons
		btnPlay = new TextButton("Play Again", buttonStyle);
		btnPlay.setPosition(Gdx.graphics.getWidth() / 2 - btnPlay.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 100);
		btnQuit = new TextButton("Quit", buttonStyle);
		btnQuit.setPosition(btnPlay.getX() + btnQuit.getWidth() - 25, btnPlay.getY() - btnPlay.getHeight());
		
		//initialising the text field
		TextField.TextFieldStyle tfs = new TextFieldStyle();
		tfs.font = font;
		tfs.fontColor = Color.WHITE;
		Sprite s = new Sprite(new Texture(new Pixmap(2000, 50, Pixmap.Format.RGB888)));
		s.setColor(Color.WHITE);
		tfs.background = new SpriteDrawable(s);
		txtName = new TextField("", tfs);
		txtName.setBounds(btnPlay.getX(), btnPlay.getY() + 150, 500, 100);
		txtName.setMaxLength(3);
		txtName.setAlignment(Align.center);
		
		//initialising the stage which will stretch
		stage = new Stage(new StretchViewport(900, 700));
		
		//initialising the lose label
		lblScore = new Label("Score:" + GameScreen.getScore(), new Label.LabelStyle(font, Color.WHITE));
		lblScore.setPosition((Gdx.graphics.getWidth() / 2) - lblScore.getWidth() / 2, Gdx.graphics.getHeight() - 100);
		
		//allowing the stage to receive input events
		Gdx.input.setInputProcessor(stage);
		
		//adding actors to the stage
		stage.addActor(lblScore);
		stage.addActor(btnPlay);
		stage.addActor(btnQuit);
		stage.addActor(txtName);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta); //update actors
		stage.draw(); //draw actors
		
		//goto the game screen if the play button is pressed
		if (btnPlay.isPressed())
			MainGame.changeScreen(MainGame.GAME_SCREEN);
		
		if (btnQuit.isPressed()) {
			ScoreDAO sDAO = new ScoreDAO();
			sDAO.uploadeScore(txtName.getText(), GameScreen.getScore());
			Gdx.app.exit();
		}
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
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
