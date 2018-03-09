package networking.server;

import java.io.IOException;
import java.net.Socket;

/**
 * This class listens for TCP messages from a single client.
 * @author Connor Stewart
 */
public class ServerTCPMessageListener extends Thread {
	
	/** The socket this thread listens for messages on. */
	private Socket toListen;
	
	/** The client this thread listens to. */
	private ClientInfo client;
	
	public ServerTCPMessageListener(ClientInfo toListen) {
		client = toListen;
		this.toListen = toListen.getSocket();
		
		setName("Server Message Listening Thread");		
		setDaemon(true);
	}
	
	public void run() {
		while (Server.isRunning()) {
			byte[] buffer = new byte[1000];
			
			try {
				toListen.getInputStream().read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			String message = new String(buffer).trim();
			
			System.out.println(getClass().getSimpleName() + " >>> Recieved message from client on " + 
			client.getIpAddress() + ":" + "'" + message + "'.");
			
			for (ServerListener listener : Server.getInstance().getListeners())
				listener.messageReceived(client, message);
		}
	}
	
}
