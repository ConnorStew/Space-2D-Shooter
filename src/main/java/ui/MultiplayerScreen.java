package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import network.client.ClientHandler;

import javax.swing.*;

/**
 * This screen allows the player to connect to other players and the server for multiplayer.
 * @author Connor Stewart
 */
public class MultiplayerScreen extends UIScreen {

	/** The button that allows the client to create a room. */
	private TextButton btnRoom;
	
	/** The button that refreshes rooms. */
	private TextButton btnRefresh;
	
	/** List to display available rooms. */
	private List<String> roomList;
	
	/** List to display required players for a room. */
	private List<String> playersList;
	
	/** The client that controls the connection to the server. */
	private ClientHandler client;

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
		playersList = new List<String>(lstStyle);
		
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
		ScrollPane pnlScroll = new ScrollPane(lists, scrStyle);
		pnlScroll.setBounds(20, 100, 850, 500);
		pnlScroll.debug();
		
		btnRefresh.setPosition(btnRoom.getX() + 500, btnRefresh.getY());
		
		stage.addActor(background);
		stage.addActor(pnlScroll);
		stage.addActor(btnRoom);
		stage.addActor(btnRefresh);
		
		String nickname = JOptionPane.showInputDialog(null, "Input your nickname.", "Nickname", JOptionPane.QUESTION_MESSAGE);
		
		client = new ClientHandler(nickname);
	}

	public void render(float delta) {
	    super.render(delta);

		if (btnRoom.isPressed() && validateButtonPress()) {
			String roomName = JOptionPane.showInputDialog(null, "Input your room's name.", "Room Name", JOptionPane.QUESTION_MESSAGE);
			String roomNum = JOptionPane.showInputDialog(null, "Input the number of players.", "Room Number", JOptionPane.QUESTION_MESSAGE);
			
			client.addRoom(roomName, roomNum);
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
	public void populateRooms(String[] roomNames, String[] requiredPlayers) {
		roomList.clearItems();
		playersList.clearItems();
		
		roomList.setItems(roomNames);
		playersList.setItems(requiredPlayers);
	}
	
}
