package ui;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import client.ClientConnection;

/**
 * This screen allows the player to connect to other players and the server for multiplayer.
 * @author Connor Stewart
 */
public class MultiplayerScreen implements Screen, TextInputListener {
	
	/** Singleton instance of this class. */
	private static final MultiplayerScreen instance = new MultiplayerScreen();
	
	/** Button cooldown in milliseconds. */
	private final double BUTTON_COOLDOWN = 0.5;
	
	/** The time since a button was pressed. */
	private double timeSinceButtonPressed = BUTTON_COOLDOWN;
	
	/** The stage to display elements. */
	private Stage stage;
	
	/** The button that allows the client to create a room. */
	private TextButton btnRoom;
	
	/** The button that refreshes rooms. */
	private TextButton btnRefresh;
	
	/** List to display available rooms. */
	private List<String> roomList;
	
	/** The client connection utilities. */
	private ClientConnection client;
	
	/** Scroll panel for viewing rooms. */
	private ScrollPane pnlScroll;

	/** This clients nickname. */
	private String nickname;
	
	/** {@link #getInstance()} should be used to obtain an instance of this class.  */
	private MultiplayerScreen() {}

	@Override
	public void show() {
		//initialising the stage which will stretch
		stage = new Stage(new StretchViewport(900, 700));
		
		btnRoom = new TextButton("Create Room", UI.buttonStyle);
		btnRefresh = new TextButton("Refresh", UI.buttonStyle);
		nickname = "test";
		
		//make background
		Image background = new Image(new Texture(Gdx.files.internal("res/space.png")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		//initialising room List
		roomList = new List<String>(UI.lstStyle);
		
		//initialising the scroll pane
		pnlScroll = new ScrollPane(roomList, UI.scrStyle);
		pnlScroll.setBounds(20, 100, 850, 500);
		
		pnlScroll.layout();
		
		btnRefresh.setPosition(btnRoom.getX() + 500, btnRefresh.getY());
		
		Gdx.input.setInputProcessor(stage);
		
		stage.addActor(background);
		stage.addActor(pnlScroll);
		stage.addActor(btnRoom);
		stage.addActor(btnRefresh);
		
		client = new ClientConnection(nickname);
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta); //update actors
		stage.draw(); //draw actors
		
		timeSinceButtonPressed = timeSinceButtonPressed + delta;
		
		if (btnRoom.isPressed() && timeSinceButtonPressed > BUTTON_COOLDOWN) {
			timeSinceButtonPressed = 0;
			Gdx.input.getTextInput(this, "Room Name", "e.g. connors room", null);
		}
		
		if (btnRefresh.isPressed() && timeSinceButtonPressed > BUTTON_COOLDOWN) {
			timeSinceButtonPressed = 0;
			client.refreshRooms();
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
	public void dispose() {}
	
	/**
	 * @return singleton instance of this class
	 */
	public static Screen getInstance() {
		return instance;
	}

	@Override
	public void input(String text) {
		client.addRoom(text);
	}

	@Override
	public void canceled() {}

	/**
	 * Populates the rooms with a new set of rooms.
	 * @param roomNames the room names to add
	 */
	public static void populateRooms(ArrayList<String> roomNames) {
		MultiplayerScreen.instance.roomList.clearItems();
		String[] roomNameArray = roomNames.toArray(new String[roomNames.size()]);
		MultiplayerScreen.instance.roomList.setItems(roomNameArray);
	}
	
}
