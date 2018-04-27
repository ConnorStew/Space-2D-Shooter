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

public class ClientHandler implements MessageQueueListener {
	
	private final Client client = new Client();

	private String nickname;

	private MessageQueue queue;

	public ClientHandler(boolean nickName) {
	    if (nickName)
            updateNickname();

	    queue = new MessageQueue();
		queue.addListener(this);

        client.start();
        client.addListener(queue);

		Network.register(client);

		try {
			client.connect(30000000, client.discoverHost(Network.UDP_PORT, 3000000), Network.TCP_PORT, Network.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (nickName) {
            UpdateNickname toSend = new UpdateNickname();
            toSend.nickname = nickname;
            client.sendTCP(toSend);
        }
	}

	private void updateNickname() {
        nickname = JOptionPane.showInputDialog(null, "Input your nickname.", "Nickname", JOptionPane.QUESTION_MESSAGE);
    }

    @Override
    public boolean received(Message message) {
	    Object object = message.getMessage();

        if(object instanceof RoomUpdate){
            RoomUpdate msg = (RoomUpdate) object;
            if (ControlGame.getInstance().getScreen() instanceof MultiplayerScreen) {
                ((MultiplayerScreen) ControlGame.getInstance().getScreen()).populateRooms(msg.roomNames);
                return true;
            } else {
                return false;
            }
        }

        if(object instanceof StartGame){
            Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new MPGame(client, nickname)));
            return true;
        }

        if(object instanceof Network.JoinLobby) {
            Network.JoinLobby msg = (Network.JoinLobby) object;
            Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new LobbyScreen(this, msg.leader)));
            return true;
        }

        if(object instanceof Network.LobbyClosed) {
            Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(MultiplayerScreen.getInstance()));
            return true;
        }

        if(object instanceof Network.LobbyPlayers) {
            if (ControlGame.getInstance().getScreen() instanceof LobbyScreen) {
                LobbyScreen lobby = (LobbyScreen) ControlGame.getInstance().getScreen();
                Gdx.app.postRunnable(() -> lobby.populatePlayers(((LobbyPlayers) object).players));
                return true;
            } else {
                return false;
            }
        }

        if (object instanceof Network.ErrorMessage) {
            JOptionPane.showMessageDialog(null, ((ErrorMessage) object).message, "Error", JOptionPane.ERROR_MESSAGE, null);
        }

        if (object instanceof Network.ConfirmationMessage)
            if (((ConfirmationMessage) object).type.equals(ConfirmType.ValidName))
                Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new MultiplayerScreen(this)));

        return false;
    }

    public void addRoom(String roomName) {
		AddRoom toSend = new AddRoom();
		toSend.roomName = roomName;
		client.sendTCP(toSend);
		refreshRooms();
	}

	public void refreshRooms() {
		client.sendTCP(new RefreshRooms());
	}

	public void joinRoom(String selected) {
		JoinRoom toSend = new JoinRoom();
		toSend.roomName = selected;
		client.sendTCP(toSend);
	}

    public MessageQueue getQueue() {
        return queue;
    }

    public Client getKyroClient() {
	    return client;
    }
}
