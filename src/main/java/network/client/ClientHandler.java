package network.client;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import network.*;
import network.Network.*;
import ui.ControlGame;
import ui.LobbyScreen;
import ui.MPGame;
import ui.MultiplayerScreen;

import javax.swing.*;
import java.io.IOException;
import java.net.SocketException;

/**
 * This class is used to handle functions related to connecting to the server and sending/receiving messages to/from it.
 * @author Connor Stewart
 */
public class ClientHandler implements MessageQueueListener {

    /** The kyronet client object. */
	private final Client client = new Client();

	/** This clients nickname. */
	private String nickname;

	/** The queue for messages that have been received. */
	private MessageQueue queue;

	/** Whether this client has connected to the server. */
    private boolean connected;

    /**
     * Creates a new client and connect to the server.
     * @param usingNickname whether this client is connecting with a nickname
     */
	public ClientHandler(boolean usingNickname) {
	    if (usingNickname)
            updateNickname();

	    queue = new MessageQueue();
		queue.addListener(this);

        client.start();
        client.addListener(queue);

		Network.register(client);

		try {
			client.connect(5000, client.discoverHost(Network.UDP_PORT, 5000), Network.TCP_PORT, Network.UDP_PORT);
		    connected = true;
		} catch (IllegalArgumentException e1) {
		    JOptionPane.showMessageDialog(null, "Cannot connect to server.", "Connection Error", JOptionPane.ERROR_MESSAGE);
		    client.close();
		    return;
		} catch (SocketException e3) {
            return;
        } catch (IOException e2) {
		    return;
        }

        if (usingNickname) {
            UpdateNickname toSend = new UpdateNickname();
            toSend.nickname = this.nickname;
            client.sendTCP(toSend);
        }
	}

    /**
     * Gets a new nickname from the user.
     */
	private void updateNickname() {
        nickname = JOptionPane.showInputDialog(null, "Input your nickname.", "Nickname", JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public boolean received(Message message) {
	    Object object = message.getMessage();

	    //updates the room screen if the client is on it
        if(object instanceof RoomUpdate) {
            RoomUpdate msg = (RoomUpdate) object;
            if (ControlGame.getInstance().getScreen() instanceof MultiplayerScreen) {
                ((MultiplayerScreen) ControlGame.getInstance().getScreen()).populateRooms(msg.roomNames);
                return true;
            } else {
                return false;
            }
        }

        //start a multiplayer game
        if(object instanceof StartGame){
            Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new MPGame(client, nickname)));
            return true;
        }

        //goes to the lobby screen for a new game
        if(object instanceof Network.JoinLobby) {
            Network.JoinLobby msg = (Network.JoinLobby) object;
            Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new LobbyScreen(this, msg.leader)));
            return true;
        }

        //goes back to the multiplayer lobby
        if(object instanceof Network.LobbyClosed) {
            Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(MultiplayerScreen.getInstance()));
            return true;
        }

        //updates a lobby with a new set of players, if the screen is currently on the lobby
        if(object instanceof Network.LobbyPlayers) {
            if (ControlGame.getInstance().getScreen() instanceof LobbyScreen) {
                LobbyScreen lobby = (LobbyScreen) ControlGame.getInstance().getScreen();
                Gdx.app.postRunnable(() -> lobby.populatePlayers(((LobbyPlayers) object).players));
                return true;
            } else {
                return false;
            }
        }

        //displays an error message
        if (object instanceof Network.ErrorMessage)
            JOptionPane.showMessageDialog(null, ((ErrorMessage) object).message, "Error", JOptionPane.ERROR_MESSAGE, null);

        //display a confirmation message
        if (object instanceof Network.ConfirmationMessage)
            if (((ConfirmationMessage) object).type.equals(ConfirmType.ValidName))
                Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new MultiplayerScreen(this)));

        return false;
    }

    /**
     * Sends a message to add a new game room to the server.
     * @param roomName the rooms name
     */
    public void addRoom(String roomName) {
		AddRoom toSend = new AddRoom();
		toSend.roomName = roomName;
		client.sendTCP(toSend);
		refreshRooms();
	}

    /**
     * Sends a request to get a new list of rooms from the server.
     */
	public void refreshRooms() {
		client.sendTCP(new RefreshRooms());
	}

    /**
     * Sends a request to the server to join a room.
     * @param roomName the name of the room to join
     */
	public void joinRoom(String roomName) {
		JoinRoom toSend = new JoinRoom();
		toSend.roomName = roomName;
		client.sendTCP(toSend);
	}

    /**
     * @return this clients message queue
     */
    public MessageQueue getQueue() {
        return queue;
    }

    /**
     * @return this clients kyronet client object
     */
    public Client getKyroClient() {
	    return client;
    }

    /**
     * @return whether this client is connected to the server
     */
    public boolean isConnected() {
        return connected;
    }
}
