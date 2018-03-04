package networking;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.ArrayList;
import networking.client.Client;
import networking.client.ClientListener;
import networking.server.Server;
import networking.server.ServerListener;

public class MessageListener extends Thread {
	

	
	private boolean startedCommand = false;
	
	private boolean commandCompleted = false;
	
	private Socket toListen;
	
	private Client client;
	

	
	private boolean isServer;
	
	public MessageListener(Client toListen, Type type) {
		this.toListen = toListen.getSocket();
		this.client = toListen;
		
		if (type.equals(Type.Client)) {
			isServer = false;
			setName(toListen.getNickname() + " Message listening Thread");
		} else {
			isServer = true;
			setName("Server Message Listening Thread");
		}
		
		setDaemon(true);
	}
	
	public void run() {
		
		ArrayList<Character> commandBuffer = new ArrayList<Character>();
		
		while (true) {
			byte[] buffer = new byte[1000];
			
			try {
				toListen.getInputStream().read(buffer);
			} catch (IOException e) {
				e.printStackTrace();
			}
				
			
			String message = null;
				
			try {
				//wait for message
				message = new String(buffer, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
				
			message = message.trim();
			
			//add the message to the messageBuffer
			for (Character character : message.toCharArray()) {
				if (startedCommand == false) {
					//check if a command has started
					if (character.equals('<')) {
						startedCommand = true;
						commandCompleted = false;
					}
				} else { //command has been started
					if (!character.equals('>')) { //command content
						commandBuffer.add(character);
					} else {
						//the command has ended
						startedCommand = false;
						commandCompleted = true;
					}
				}
				
				if (commandCompleted) {
					//turn the command into a string
					String toSend = "";
						
					for (Character character2 : commandBuffer)
						toSend = toSend + character2;
						
					//empty the command array
					commandBuffer.clear();
						
					if (isServer) {
						for (ServerListener listener : Server.getInstance().getListeners())
							listener.messageReceived(client, toSend);
					} else {
						for (ClientListener listener : client.getListeners())
							listener.messageReceived(toSend);
					}

				}
			}
				

		}
	}
	
}
