package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import database.ScoreDAO;
import network.ConfirmType;
import network.Network;
import network.client.ClientHandler;

import java.util.ArrayList;

/**
 * This class is used to show the user their score and allow them to upload theirs.
 * @author Connor Stewart
 */
public class ScoreScreen extends UIScreen {

	/** Buttons. */
	private TextButton btnUpload, btnBack;
	
	/** Used to enter your name for the score board. */
	private TextField txtName;

	/** The scores retrieved from the server. */
	private List<String> lstScores;

	/** The scroll panel to show the list of scores. */
	private ScrollPane pnlScroll;

	/** Whether the score has been uploaded. */
	private boolean uploaded;

	/** The players score. */
	private int score;

	/** The connection to the server for score upload. */
	private ClientHandler client;

	public ScoreScreen(int score){
		this.score = score;
		uploaded = false;

		//connect to the server
		client = new ClientHandler(false);
		client.getKyroClient().addListener(new ThreadedListener(new Listener(){
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof Network.ConfirmationMessage) {
					if (((Network.ConfirmationMessage) object).type.equals(ConfirmType.ScoreAdded)) {
						updateScores();
						txtName.setDisabled(true);
						txtName.setText("");
						uploaded = true;
					}
				}

				if (object instanceof Network.ScoreUpdate) {
					Network.ScoreUpdate msg = (Network.ScoreUpdate) object;

					ArrayList<Integer> scores = msg.scores;
					ArrayList<String> names = msg.names;

					String[] sNames;

					//scores and names
					if (names.isEmpty()) { //if the names are unavailable
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
			}
		}));
	}

	public void show() {
		super.show();

		//make background
		Image background = new Image(new Texture(Gdx.files.internal("backgrounds/hubble.jpg")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		//initialising score list
		lstScores = new List<String>(lstStyle);

		//initialising the scroll pane
		pnlScroll = new ScrollPane(lstScores, scrStyle);
		pnlScroll.setBounds(20, 100, 350, 400);
		
		//initialising the buttons
		btnUpload = new TextButton("Upload", buttonStyle);
		btnUpload.setPosition(Gdx.graphics.getWidth() / 2 - btnUpload.getWidth() / 2 + 100, Gdx.graphics.getHeight() / 2 - 100);
		
		btnBack = new TextButton("Back", buttonStyle);
		btnBack.setPosition(btnUpload.getX() + 270, btnUpload.getY());
		
		txtName = new TextField("", tfs);
		txtName.setBounds(btnUpload.getX(), btnUpload.getY() + 150, 420, 100);
		txtName.setMaxLength(3);
		txtName.setAlignment(Align.center);
		
		//initialising the score label
		Label lblScore = new Label("Score:" + score, labelStyle);
		lblScore.setPosition((Gdx.graphics.getWidth() / 2) - lblScore.getWidth() / 2, Gdx.graphics.getHeight() - 110);
		
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
		client.getKyroClient().sendTCP(new Network.RefreshScores());
	}

	public void render(float delta) {
		super.render(delta);

		//goto the game screen if the play button is pressed
		if (btnUpload.isPressed() && validateButtonPress() && !uploaded) {
			if (txtName.getText().length() != 3) {
				Window.WindowStyle wStyle = new Window.WindowStyle();

				wStyle.titleFont = starTrekFont;
				
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
				Network.UploadScore msg = new Network.UploadScore();
				msg.name = txtName.getText().toUpperCase();
				msg.score = score;
				client.getKyroClient().sendTCP(msg);
			}
		}
		
		if (btnBack.isPressed() && validateButtonPress())
			ControlGame.getInstance().setScreen(new MenuScreen());
	}

}