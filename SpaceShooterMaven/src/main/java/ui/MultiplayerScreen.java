package ui;

import java.net.InetAddress;
import java.net.SocketException;

import javax.swing.JOptionPane;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import networking.NetworkUtils;
import networking.client.Client;
import networking.client.ClientListener;

/**
 * This screen allows the player to connect to other players and the server for multiplayer.
 * @author Connor Stewart
 */
public class MultiplayerScreen implements Screen, ClientListener {
	
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
	
	/** List to display required players for a room. */
	private List<String> playersList;
	
	/** The group that holds the lists. */
	private HorizontalGroup lists;
	
	/** The client. */
	private Client client;
	
	/** Scroll panel for viewing rooms. */
	private ScrollPane pnlScroll;
	
	/** {@link #getInstance()} should be used to obtain an instance of this class.  */
	private MultiplayerScreen() {}

	public void show() {
		//initialising the stage which will stretch
		stage = new Stage(new StretchViewport(900, 700));
		
		btnRoom = new TextButton("Create Room", UI.buttonStyle);
		btnRefresh = new TextButton("Refresh", UI.buttonStyle);
		
		//make background
		Image background = new Image(new Texture(Gdx.files.internal("space.png")));
		background.setFillParent(true);
		background.setPosition(0, 0);
		
		lists = new HorizontalGroup();
		
		//initialising room and player lists
		roomList = new List<String>(UI.lstStyle);
		playersList = new List<String>(UI.lstStyle);
		
		lists.addActor(roomList);
		lists.addActor(playersList);
		lists.space(50f);

		roomList.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				int index = roomList.getSelectedIndex();
				
				if (playersList.getItems().size > index)
					playersList.setSelectedIndex(index);
			}
		});
		
		//can't select with the players list
		playersList.setTouchable(Touchable.disabled);
		
		//initialising the scroll pane
		pnlScroll = new ScrollPane(lists, UI.scrStyle);
		pnlScroll.setBounds(20, 100, 850, 500);
		pnlScroll.debug();
		
		btnRefresh.setPosition(btnRoom.getX() + 500, btnRefresh.getY());
		
		Gdx.input.setInputProcessor(stage);
		
		stage.addActor(background);
		stage.addActor(pnlScroll);
		stage.addActor(btnRoom);
		stage.addActor(btnRefresh);
		
		String nickname = JOptionPane.showInputDialog(null, "Input your nickname.", "Nickname", JOptionPane.QUESTION_MESSAGE);
		
		try {
			client = new Client(nickname);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		client.addListener(this);
	}

	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		stage.act(delta); //update actors
		stage.draw(); //draw actors
		
		timeSinceButtonPressed = timeSinceButtonPressed + delta;
		
		if (btnRoom.isPressed() && timeSinceButtonPressed > BUTTON_COOLDOWN) {
			timeSinceButtonPressed = 0;
			
			String roomName = JOptionPane.showInputDialog(null, "Input your room's name.", "Room Name", JOptionPane.QUESTION_MESSAGE);
			String roomNum = JOptionPane.showInputDialog(null, "Input the number of players.", "Room Number", JOptionPane.QUESTION_MESSAGE);
			
			client.addRoom(roomName, roomNum);
		}
		
		if (btnRefresh.isPressed() && timeSinceButtonPressed > BUTTON_COOLDOWN) {
			timeSinceButtonPressed = 0;
			client.refreshRooms();
		}
		
		//join the game
		if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && timeSinceButtonPressed > BUTTON_COOLDOWN) {
			timeSinceButtonPressed = 0;
			client.joinRoom(roomList.getSelected());
		}
		
	}

	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}
	
	public void pause() {}

	public void resume() {}

	public void hide() {}

	public void dispose() {}
	
	/**
	 * @return singleton instance of this class
	 */
	public static Screen getInstance() {
		return instance;
	}

	/**
	 * Populates the rooms with a new set of rooms.
	 * @param roomNames the room names to add
	 */
	public static void populateRooms(String[] roomNames, String[] requiredPlayers) {
		MultiplayerScreen.instance.roomList.clearItems();
		MultiplayerScreen.instance.playersList.clearItems();
		
		MultiplayerScreen.instance.roomList.setItems(roomNames);
		MultiplayerScreen.instance.playersList.setItems(requiredPlayers);
	}

	public void messageReceived(String message, InetAddress address) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		//the server has sent a room update
		if (command.equals("RU")) {
			int roomNum = arguments.length;
			String[] roomNames = new String[roomNum / 2];
			String[] requiredPlayers = new String[roomNum / 2];
			
			//parse the arguments
			for (int i = 0; i < roomNum; i = i + 2) {
				roomNames[i / 2] = arguments[i];
				requiredPlayers[i / 2] = arguments[i + 1];
			}
			
			roomList.clearItems();
			roomList.setItems(roomNames);
			
			playersList.clearItems();
			playersList.setItems(requiredPlayers);
		}
		
	}
	
}
