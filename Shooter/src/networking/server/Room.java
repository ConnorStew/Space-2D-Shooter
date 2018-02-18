package networking.server;

import java.util.concurrent.CopyOnWriteArrayList;

import networking.client.Client;

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
	private CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<Client>();

	public Room(String name, int requiredPlayers) {
		this.name = name;
		this.requiredPlayers = requiredPlayers;
	}

	public String getRoomName() {
		return name;
	}

	public void addClient(Client client) {
		System.out.println(getClass().getName() + ">>>" + client.getNickname() + " joined room '" + name + "'");
		clients.add(client);
		
		//game should start for all clients
		if (clients.size() == requiredPlayers) {
			System.out.println(getClass().getName() + ">>> game ready to start.");
			Server.getInstance().startGame(this);
		}
	}

	public CopyOnWriteArrayList<Client> getClients() {
		return clients;
	}

	
}
