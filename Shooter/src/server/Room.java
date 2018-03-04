package server;

/**
 * This class represents a room that is being hosted by a player on the server.
 * @author Connor Stewart
 */
public class Room {
	
	/** The rooms name. */
	private String name;

	public Room(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
}
