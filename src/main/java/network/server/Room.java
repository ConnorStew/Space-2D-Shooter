package network.server;

import com.badlogic.gdx.utils.Array;
import network.Network;

/**
 * This class represents a room that is being hosted on the server.
 * @author Connor Stewart
 */
class Room {

	/** The maximum length of room's name. */
	static final int MAX_NAME_LEN = 20;

	/** The rooms name. */
	private String name;

	/** The client that started this room. */
	private ClientInfo leader;
	
	/** Clients in the room. */
	private Array<ClientInfo> clients = new Array<>();

	/**
	 * Creates a new room with a name and a leader.
	 * @param name the rooms name
	 * @param leader the client that owns the room
	 */
	Room(String name, ClientInfo leader) {
		this.name = name;
		this.leader = leader;
	}

	/**
	 * Adds a client to this room.
	 * @param info the clients information
	 */
	void addClient(ClientInfo info) {
		//don't allow duplicate clients in the same room
		if (clients.contains(info, false))
			return;

		clients.add(info);

		//tell the player to move to the lobby screen
		Network.JoinLobby msg = new Network.JoinLobby();

		msg.leader = info.equals(leader);

		info.getConnection().sendTCP(msg);

		//tell the players in thr room about the new player
		Network.LobbyPlayers playerListMessage = new Network.LobbyPlayers();
		String[] playerNames = new String[clients.size];

		for (int i = 0; i < clients.size; i++)
			playerNames[i] = clients.get(i).getNickname();

		playerListMessage.players = playerNames;

		for (ClientInfo client : clients)
			client.getConnection().sendTCP(playerListMessage);
	}

	/**
	 * @return this rooms name
	 */
	String getRoomName() {
		return name;
	}

	/**
	 * @return the clients that have joined this room
	 */
	Array<ClientInfo> getClients() {
		return clients;
	}

	/**
	 * Checks if a client has joined this room.
	 * @param client the client to check for
	 * @return whether the client is in this room
	 */
	boolean hasClient(ClientInfo client) {
		return clients.contains(client, false);
	}

	/**
	 * Removes a client from this room.
	 * @param client the client to remove
	 */
	void removeClient(ClientInfo client) {
		clients.removeIndex(clients.indexOf(client, false));

		if (client.equals(leader)) {
			for (ClientInfo roomClient : clients)
				roomClient.getConnection().sendTCP(new Network.LobbyClosed());

			ServerHandler.getInstance().closeRoom(this);
		} else {
			//tell the players in thr room about the new player
			Network.LobbyPlayers playerListMessage = new Network.LobbyPlayers();
			String[] playerNames = new String[clients.size];

			for (int i = 0; i < clients.size; i++)
				playerNames[i] = clients.get(i).getNickname();

			playerListMessage.players = playerNames;

			for (ClientInfo roomClient : clients)
				roomClient.getConnection().sendTCP(playerListMessage);
		}
	}

	/**
	 * @return the client that owns this game
	 */
	ClientInfo getLeader() {
		return leader;
	}
}