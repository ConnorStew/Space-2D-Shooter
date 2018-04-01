package network.server;

import com.badlogic.gdx.utils.Array;

/**
 * This class represents a room that is being hosted on the server.
 * @author Connor Stewart
 */
public class Room {
	
	/** The rooms name. */
	private String name;
	
	/** The amount of players this room needs before a game can start. */
	private int requiredPlayers;
	
	/** Clients in the room. */
	private Array<ClientInfo> clients = new Array<ClientInfo>();

	public Room(String name, int requiredPlayers) {
		this.name = name;
		this.requiredPlayers = requiredPlayers;
	}

	public void addClient(ClientInfo info) {
		System.out.println(getClass().getSimpleName() + " >>> " + info.getNickname() + " joined room '" + name + "'.");
		clients.add(info);
		
		//game should start for all clients
		if (clients.size == requiredPlayers) {
			System.out.println(getClass().getSimpleName() + " >>> game ready to start.");
			ServerHandler.getInstance().startGame(this);
		}
	}
	
	public String getRoomName() {
		return name;
	}

	public Array<ClientInfo> getClients() {
		return clients;
	}

	public int getRequiredPlayers() {
		return requiredPlayers;
	}
	
}