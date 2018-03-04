package networking.server;

import networking.NetworkUtils;
import networking.client.Client;

/**
 * This thread hosts a game once a room of players has been assembled.
 * @author Connor Stewart
 */
public class ServerGameThread extends Thread implements ServerListener {
	
	/** The room containing clients playing this game. */
	private Room room;
	
	/** The last multiplayer ID assigned to an entity. */
	private int lastIDAssigned;
		
	public ServerGameThread(Room toHost) {
		room = toHost;
		lastIDAssigned = 0;
	}
	
	@Override
	public void run() {
		//get events from the server
		Server.getInstance().addListener(this);
			
		for (Client client : room.getClients()) {
			//tell the clients to open their game screens
			NetworkUtils.sendMessage("START_GAME", client.getOutputStream());
		}
		
		//tell the clients to add the player characters to the game
		for (Client client : room.getClients()) {
			for (Client client2 : room.getClients()) {
				NetworkUtils.sendMessage("ADDPLAYER/" + client2.getNickname() + "/" + (++lastIDAssigned), client.getOutputStream());
			}
			lastIDAssigned = 0;
		}
		
		lastIDAssigned = room.getClients().size();
	}

	@Override
	public void messageReceived(Client client, String message) {
		message = message.trim();
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		if (command.equals("W_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'w' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("MOVE/" + client.getNickname() + "/UP", aClient.getOutputStream());
		}
		
		if (command.equals("S_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 's' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("MOVE/" + client.getNickname() + "/DOWN", aClient.getOutputStream());
		}
		
		if (command.equals("R_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'r' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients()) 
				NetworkUtils.sendMessage("MOVE/" + client.getNickname() + "/RIGHT", aClient.getOutputStream());
		}
		
		if (command.equals("L_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'l' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("MOVE/" + client.getNickname() + "/LEFT", aClient.getOutputStream());
		}
		
		if (command.equals("LMB")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the left mouse button.");
			
			lastIDAssigned++;
			
			//tell all clients to let that player shoot
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("SHOOTL/" + client.getNickname() + "/" + lastIDAssigned, aClient.getOutputStream());
		}
		
		if (command.equals("RMB")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the right mouse button.");
			
			lastIDAssigned++;
			
			//tell all clients to let that player shoot
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("SHOOTR/" + client.getNickname() + "/" + lastIDAssigned, aClient.getOutputStream());
		}
		
		if (command.equals("ROTATE")) {
			//System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has moved their mouse.");
			
			//tell all clients to let rotate that player
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("ROTATE/" + client.getNickname() + "/" + arguments[0], aClient.getOutputStream());
		}
		
		if (command.equals("HIT")) {
			String playerID = arguments[0]; //id of the player
			String projectileID = arguments[1]; //id of the projectile
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has reported a collision between id " + playerID + " and id " + projectileID);
			
			//tell all clients to react to the collision
			for (Client aClient : room.getClients())
				NetworkUtils.sendMessage("RESOLVE_COLLISION/" + playerID + "/" + projectileID, aClient.getOutputStream());
		}
		
	}

}
