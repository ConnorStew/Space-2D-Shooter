package networking.server;

import java.io.IOException;

import networking.NetworkUtils;
import networking.client.Client;

public class ServerGameThread extends Thread implements ServerListener {
	
	/** The room containing clients playing this game. */
	private Room room;
	
	public ServerGameThread(Room toHost) {
		room = toHost;
	}
	
	@Override
	public void run() {
		try {
			//get events from the server
			Server.getInstance().addListener(this);
			
			//tell the clients to open their game screens
			for (Client client : room.getClients()) {
				client.getOut().writeUTF("START_GAME");
				
				for (Client client2 : room.getClients()) {
					//add players for every other player
					client.getOut().writeUTF("ADDPLAYER/" + client2.getNickname());
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void messageReceived(Client client, String message) {
		String command = NetworkUtils.parseCommand(message);
		//String[] arguments = NetworkUtils.parseArguements(message);
		
		if (command.equals("W_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'w' key.");
			
			//tell all other clients to move that character
			for (Client aClient : room.getClients()) {
				try {
					aClient.getOut().writeUTF("MOVE/" + client.getNickname() + "/UP");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (command.equals("S_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 's' key.");
			
			//tell all other clients to move that character
			for (Client aClient : room.getClients()) {
				try {
					aClient.getOut().writeUTF("MOVE/" + client.getNickname() + "/DOWN");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (command.equals("R_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'r' key.");
			
			//tell all other clients to move that character
			for (Client aClient : room.getClients()) {
				try {
					aClient.getOut().writeUTF("MOVE/" + client.getNickname() + "/RIGHT");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (command.equals("L_PRESS")) {
			System.out.println(getClass().getName() + ">>>" + client.getNickname() + " has pressed the 'l' key.");
			
			//tell all other clients to move that character
			for (Client aClient : room.getClients()) {
				try {
					aClient.getOut().writeUTF("MOVE/" + client.getNickname() + "/LEFT");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}

}
