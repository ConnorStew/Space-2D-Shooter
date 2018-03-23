package ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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
	
	/** The stage to display elements. */
	private Stage stage;
	
	/** Used to display the score to the user. */
	private Label lblScore;
	
	/** Buttons. */
	private TextButton btnUpload, btnBack;
	
	/** Used to enter your name for the score board. */
	private TextField txtName;
	
	private List<String> lstScores;
	
	private ScrollPane pnlScroll;
	
	private boolean uploaded;
	
	private int score;
	
	public ScoreScreen(int score){
		this.score = score;
	};

	public void show() {
		uploaded = false;
		
		//make background
		Image background = new Image(new Texture(Gdx.files.internal("backgrounds/hubble.jpg")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		//initialising score list
		lstScores = new List<String>(UI.lstStyle);

		//initialising the scroll pane
		pnlScroll = new ScrollPane(lstScores, UI.scrStyle);
		pnlScroll.setBounds(20, 100, 350, 400);
		
		//initialising the buttons
		btnUpload = new TextButton("Upload", UI.buttonStyle);
		btnUpload.setPosition(Gdx.graphics.getWidth() / 2 - btnUpload.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 2 - 100);
		
		btnBack = new TextButton("Back", UI.buttonStyle);
		btnBack.setPosition(btnUpload.getX() + 270, btnUpload.getY());
		
		txtName = new TextField("", UI.tfs);
		txtName.setBounds(btnUpload.getX(), btnUpload.getY() + 150, 420, 100);
		txtName.setMaxLength(3);
		txtName.setAlignment(Align.center);
		
		//initialising the stage which will stretch
		stage = new Stage(new StretchViewport(900, 700));
		
		//initialising the score label
		lblScore = new Label("Score:" + score, UI.labelStyle);
		lblScore.setPosition((Gdx.graphics.getWidth() / 2) - lblScore.getWidth() / 2, Gdx.graphics.getHeight() - 110);
		
		//allowing the stage to receive input events
		Gdx.input.setInputProcessor(stage);
		
		//adding actors to the stage
		stage.addActor(background);
		stage.addActor(lblScore);
		stage.addActor(btnUpload);
		stage.addActor(txtName);
		stage.addActor(pnlScroll);
		stage.addActor(btnBack);
		
		updateScores();
	}
	
	private void updateScores() {
		ScoreDAO sDAO = new ScoreDAO();
		
		ArrayList<Integer> scores = sDAO.getScores();
		ArrayList<String> names = sDAO.getNames();
		
		String[] sNames;
		
		//scores and names
		if (names == null) { //if the names are unavailable
			//change screen settings to display no scores
			sNames = new String[]{"Scores Unavailible"};
			txtName.setDisabled(true);
			txtName.setVisible(false);
			pnlScroll.setWidth(700);
			btnUpload.setDisabled(true);
			btnUpload.setVisible(false);
			btnBack.setX(btnBack.getX() + 30);
		} else {
			 sNames = new String[names.size()];
			
			//populate sNames
			for (int i = 0; i < sNames.length; i++)
				sNames[i] = names.get(i) + ": " + scores.get(i);
		}
		
		lstScores.clearItems();
		lstScores.setItems(sNames);
	}

	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta); //update actors
		stage.draw(); //draw actors
		
		//goto the game screen if the play button is pressed
		if (btnUpload.isPressed() && uploaded == false) {
			if (txtName.getText().length() != 3) {
				Window.WindowStyle wStyle = new Window.WindowStyle();
				//font generator
				FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Star Trek Enterprise Future.ttf"));
				FreeTypeFontParameter parameter = new FreeTypeFontParameter();
				parameter.size = 50; //setting font size
				
				//creating font object
				BitmapFont windowFont = generator.generateFont(parameter);
				
				wStyle.titleFont = windowFont;
				
				final Dialog dialog = new Dialog("You must input a three letter name!", wStyle) {
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
				sDAO.uploadScore(txtName.getText().toUpperCase(), score);
				updateScores();
				txtName.setDisabled(true);
				txtName.setText("");
				uploaded = true;
			}
			
		}
		
		if (btnBack.isPressed()) {
			UI.getInstance().setScreen(MenuScreen.getInstance());
		}

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

}
