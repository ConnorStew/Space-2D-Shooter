package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Array;
import network.Message;
import network.Network;
import network.client.ClientHandler;

import javax.swing.*;

/**
 * This screen allows the player to connect to other players and the server for multiplayer.
 * @author Connor Stewart
 */
public class MultiplayerScreen extends UIScreen {

	/** The current instance of the this screen. */
	private static MultiplayerScreen INSTANCE;

	/** The button that allows the client to create a room. */
	private TextButton btnRoom;
	
	/** The button that refreshes rooms. */
	private TextButton btnRefresh;
	
	/** List to display available rooms. */
	private List<String> roomList;

	/** The client that controls the connection to the server. */
	private ClientHandler client;

	public MultiplayerScreen(ClientHandler client) {
		MultiplayerScreen.INSTANCE = this;
		this.client = client;
	}

	public void show() {
	    super.show();

		btnRoom = new TextButton("Create Room", buttonStyle);
		btnRefresh = new TextButton("Refresh", buttonStyle);
		
		//make background
		Image background = new Image(new Texture(Gdx.files.internal("backgrounds/hubble.jpg")));
		background.setFillParent(true);
		background.setPosition(0, 0);

		HorizontalGroup lists = new HorizontalGroup();
		
		//initialising room and player lists
		roomList = new List<String>(lstStyle);

		//check if a list of players is waiting in the queue
		if (client.getQueue().haveReceived(Network.RoomUpdate.class)) {
			Array<Message> messages = client.getQueue().getMessages(Network.RoomUpdate.class, true);
			//add the latest RoomPlayers message
			String[] playerNames = ((Network.RoomUpdate) messages.get(messages.size - 1).getMessage()).roomNames;
			populateRooms(playerNames);
		}
		
		lists.addActor(roomList);
		lists.space(50f);

		//initialising the scroll pane
		ScrollPane pnlScroll = new ScrollPane(lists, scrStyle);
		pnlScroll.setBounds(20, 100, 850, 500);
		pnlScroll.debug();
		
		btnRefresh.setPosition(btnRoom.getX() + 500, btnRefresh.getY());
		
		stage.addActor(background);
		stage.addActor(pnlScroll);
		stage.addActor(btnRoom);
		stage.addActor(btnRefresh);

		client.refreshRooms();
	}

	public void render(float delta) {
	    super.render(delta);

		if (btnRoom.isPressed() && validateButtonPress()) {
			String roomName = JOptionPane.showInputDialog(null, "Input your room's name.", "Room Name", JOptionPane.QUESTION_MESSAGE);
			client.addRoom(roomName);
		}
		
		if (btnRefresh.isPressed() && validateButtonPress())
			client.refreshRooms();

		if (Gdx.input.isKeyPressed(Input.Keys.ENTER) && validateButtonPress())
			client.joinRoom(roomList.getSelected());
	}

	/**
	 * Populates the rooms with a new set of rooms.
	 * @param roomNames the room names to add
	 */
	public void populateRooms(String[] roomNames) {
		roomList.clearItems();
		
		roomList.setItems(roomNames);
	}

	/**
	 * @return the current instance of this class
	 */
	public static MultiplayerScreen getInstance() {
		return INSTANCE;
	}
	
}
