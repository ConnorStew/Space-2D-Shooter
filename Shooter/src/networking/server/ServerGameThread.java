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
	
	public ServerGameThread(Room toHost) {
		room = toHost;
	}
	
	@Override
	public void run() {
		//get events from the server
		Server.getInstance().addListener(this);
			
		for (Client client : room.getClients()) {
			//tell the clients to open their game screens
			client.sendMessage("START_GAME");
				
			//tell the clients to add the player characters to the game
			for (Client client2 : room.getClients())
				client.sendMessage("ADDPLAYER/" + client2.getNickname());
		}
	}

	@Override
	public void messageReceived(Client client, String message) {
		String command = NetworkUtils.parseCommand(message);
		//String[] arguments = NetworkUtils.parseArguements(message);
		
		if (command.equals("W_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'w' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients())
				aClient.sendMessage("MOVE/" + client.getNickname() + "/UP");
		}
		
		if (command.equals("S_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 's' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients())
				aClient.sendMessage("MOVE/" + client.getNickname() + "/DOWN");
		}
		
		if (command.equals("R_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'r' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients()) 
				aClient.sendMessage("MOVE/" + client.getNickname() + "/RIGHT");
		}
		
		if (command.equals("L_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'l' key.");
			
			//tell all clients to move that character
			for (Client aClient : room.getClients())
				aClient.sendMessage("MOVE/" + client.getNickname() + "/LEFT");
		}
		
		if (command.equals("LMB")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the left mouse button.");
			
			//tell all clients to let that player shoot
			for (Client aClient : room.getClients())
				aClient.sendMessage("SHOOTL/" + client.getNickname());
		}
		
		if (command.equals("RMB")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the right mouse button.");
			
			//tell all clients to let that player shoot
			for (Client aClient : room.getClients())
				aClient.sendMessage("SHOOTR/" + client.getNickname());
		}
		
	}

}
