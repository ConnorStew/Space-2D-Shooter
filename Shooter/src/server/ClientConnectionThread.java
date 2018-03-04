package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This is a thread that is created when a new client connects to the server.
 * This classes purpose is to read information from the clients and distribute it.
 * @author Connor Stewart
 */
public class ClientConnectionThread extends Thread {
	
	/** The socket connected to the server. */
	private Socket clientSocket;
	
	/** The clients nickname. */
	private String nickname;
	
	public ClientConnectionThread(Socket clientSocket, String nickname) {
		super(nickname + " Connection Thread");
		this.clientSocket = clientSocket;
		this.nickname = nickname;
	}
	
	@Override
	public void run() {

		System.out.println(getClass().getName() + ">>>" + nickname  + " connected.");
		
		DataInputStream in = null;
		DataOutputStream out = null;
		
		try {
			in = new DataInputStream(clientSocket.getInputStream());
			out = new DataOutputStream(clientSocket.getOutputStream());

			while (true) {
				//wait for message
				String message = in.readUTF();
				
				System.out.println(getClass().getName() + ">>>Recived message from " + nickname + ", message: " + message);

				if (message.equals("ROOMS")) {
					//send all room names
					for (Room room : Server.rooms)
						out.writeUTF(room.getName());
					
					//end the transmission
					out.writeUTF("end");
				}
				
				if (message.equals("NEWROOM")) {
					//add the room to the server
					Server.rooms.add(new Room(in.readUTF()));
					
					for (Room room : Server.rooms)
						System.out.println(room.getName());
				}
			}
			
		} catch (IOException e) {
			if (e.getMessage().equals("Connection reset"))
				System.out.println(getClass().getName() + ">>>" + nickname + " disconnected.");
			else
				e.printStackTrace();
		}

	}

}
