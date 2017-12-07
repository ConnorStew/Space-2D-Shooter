package ui;

import java.util.ArrayList;

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
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
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
	private TextButton btnOk;
	
	/** Used to enter your name for the score board. */
	private TextField txtName;
	
	/** {@link #getInstance()} should be used to obtain an instance of this class.  */
	private ScoreScreen(){};

	@Override
	public void show() {
		//make background
		Image background = new Image(new Texture(Gdx.files.internal("sb2.jpg")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		//font generator
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Star Trek Enterprise Future.ttf"));
		FreeTypeFontParameter parameter = new FreeTypeFontParameter();
		parameter.size = 100; //setting font size
		
		//creating font object
		BitmapFont font = generator.generateFont(parameter);
		
		//getting rid of the generator since it will no longer be used
		generator.dispose();
		
		//set style for buttons
		TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
		buttonStyle.font = font;
		
		//black background
		Sprite s = new Sprite(new Texture(new Pixmap(2000, 50, Pixmap.Format.RGB888)));
		s.setColor(Color.WHITE);
		
		//initialising list style
		List.ListStyle lstStyle = new List.ListStyle();
		lstStyle.font = font;
		lstStyle.selection = new SpriteDrawable(s);
		lstStyle.background = new SpriteDrawable(s);
		
		//initialising score list
		List<String> lstScores = new List<String>(lstStyle);

		
		//initialising scroll pane style
		ScrollPane.ScrollPaneStyle scrStyle = new ScrollPane.ScrollPaneStyle();
		
		//initialising the scroll pane
		ScrollPane pnlScroll = new ScrollPane(lstScores, scrStyle);
		pnlScroll.setBounds(0, 100, 400, 400);

		ScoreDAO sDAO = new ScoreDAO();
		
		ArrayList<Integer> scores = sDAO.getScores();
		ArrayList<String> names = sDAO.getNames();
		
		//scores and names
		String[] sNames = new String[names.size()];
		
		//populate sNames
		for (int i = 0; i < sNames.length; i++)
			sNames[i] = names.get(i) + ": " + scores.get(i);
		
		lstScores.setItems(sNames);
		
		//initialising the buttons
		btnOk = new TextButton("Ok", buttonStyle);
		btnOk.setPosition(Gdx.graphics.getWidth() / 2 - btnOk.getWidth() / 2, Gdx.graphics.getHeight() / 2 - 100);
		
		//initialising the text field
		TextField.TextFieldStyle tfs = new TextFieldStyle();
		tfs.font = font;
		tfs.fontColor = Color.WHITE;
		tfs.background = new SpriteDrawable(s);
		txtName = new TextField("", tfs);
		txtName.setBounds(btnOk.getX(), btnOk.getY() + 150, 500, 100);
		txtName.setMaxLength(3);
		txtName.setAlignment(Align.center);
		
		//initialising the stage which will stretch
		stage = new Stage(new StretchViewport(900, 700));
		
		//initialising the score label
		lblScore = new Label("Score:" + GameScreen.getScore(), new Label.LabelStyle(font, Color.WHITE));
		lblScore.setPosition((Gdx.graphics.getWidth() / 2) - lblScore.getWidth() / 2, Gdx.graphics.getHeight() - 110);
		
		//allowing the stage to receive input events
		Gdx.input.setInputProcessor(stage);
		
		//adding actors to the stage
		stage.addActor(background);
		stage.addActor(lblScore);
		stage.addActor(btnOk);
		stage.addActor(txtName);
		stage.addActor(pnlScroll);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta); //update actors
		stage.draw(); //draw actors
		
		//goto the game screen if the play button is pressed
		if (btnOk.isPressed()) {
			if (txtName.getText().length() != 3) {
				Window.WindowStyle wStyle = new Window.WindowStyle();
				//font generator
				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Star Trek Enterprise Future.ttf"));
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = 50; //setting font size
				
				//creating font object
				BitmapFont windowFont = generator.generateFont(parameter);
				
				wStyle.titleFont = windowFont;
				
				Dialog dialog = new Dialog("You must input a three letter name!", wStyle) {
				    public void result(Object obj) {
				        System.out.println("result "+obj);
				    }
				};
				
				dialog.show(stage);
				dialog.setBounds(100, 500, 800, 60);
				
				new Timer().scheduleTask(new Task(){
					@Override
					public void run() {
						dialog.hide();
					}
				},2);
				
			} else {
				ScoreDAO sDAO = new ScoreDAO();
				sDAO.uploadeScore(txtName.getText().toUpperCase(), GameScreen.getScore());
				MainGame.changeScreen(MainGame.MENU_SCREEN);
			}
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
