package network.server;

import com.badlogic.gdx.utils.Array;
import network.Network;

/**
 * This class represents a room that is being hosted on the server.
 * @author Connor Stewart
 */
class Room {

	/** The maximum length of room's name. */
	public static final int MAX_NAME_LEN = 30;

	/** The rooms name. */
	private String name;

	/** The client that started this room. */
	private ClientInfo leader;
	
	/** Clients in the room. */
	private Array<ClientInfo> clients = new Array<>();

	Room(String name, ClientInfo leader) {
		this.name = name;
		this.leader = leader;
	}

	/**
	 * Adds a client to this room.
	 * @param info the clients information
	 */
	void addClient(ClientInfo info) {
		if (clients.contains(info, false)) {
			System.out.println(getClass().getSimpleName() + " >>> " + info.getNickname() + " has already joined room '" + name + "', ignoring request.");
			return;
		}

		System.out.println(getClass().getSimpleName() + " >>> " + info.getNickname() + " joined room '" + name + "'.");
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

	ClientInfo getLeader() {
		return leader;
	}
}