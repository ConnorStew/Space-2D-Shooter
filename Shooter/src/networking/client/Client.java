package networking.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;

import backend.ClientEngine;
import networking.NetworkUtils;
import networking.server.Server;
import ui.ClientGameScreen;
import ui.MultiplayerScreen;
import ui.UI;

/**
 * This class represents a player who controllers a character on a multiplayer game.
 * @author Connor Stewart
 */
public class Client implements ClientListener {
	
	/** The socket that connects this client to the server. */
	private Socket socket;
	
	/** The players nickname. */
	private String nickname;
	
	/** The clients output to the server. */
	private DataOutputStream out;
	
	/** The clients input from the server. */
	private DataInputStream in;
	
	/** A list of ClientListeners that wish to receive events from this client. */
	private CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<ClientListener>();

	/**
	 * This constructor should be used to create a new client and connect to the server.<br>
	 * <u>This should be used on the client side.</u>
	 * @param nickname the clients nickname
	 */
	public Client(String nickname) {
		listeners.add(this); //listen for new messages
		this.nickname = nickname;
		try {
			//connect to the server
			socket = new Socket(getServerIP(), Server.port);
			
			System.out.println(getClass().getName() + ">>>Connected to the server.");
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			//send nickname through when connected
			out.writeUTF(nickname);
			System.out.println(getClass().getName() + ">>>Sent nickname.");
			
			//start listening for messages from the server
			Thread messageListener = new Thread() {
				public void run() {
					while(true) {
						try {
							String message = in.readUTF();
							
							//send the message to all the clients on the list
							for (ClientListener listener : listeners)
								listener.messageReceived(message);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				};
			};
			
			messageListener.setName(nickname + "'s Message Listening Thread");
			messageListener.setDaemon(true);
			messageListener.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This constructor should be used to store a client that has already connected.
	 * <u>This should be used on the server side.</u>
	 * @param socket the socket the client is connected on
	 * @param nickname the clients nickname
	 */
	public Client(Socket socket, String nickname) {
		this.nickname = nickname;
		this.socket = socket;
		
		try {
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void messageReceived(String message) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		System.out.println(getClass().getName() + ">>>Recived command from server: " + command);
		
		//server letting the client know that the game is ready to begin
		if (command.equals("START_GAME")) {
			Client client = this;
			ClientEngine engine = new ClientEngine(client);
			
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					UI.getInstance().setScreen(new ClientGameScreen(client, engine));
				}
			});
		}
		
		//the server sending the client a list of room names
		if (command.equals("ROOMS")) {
			int roomNum = arguments.length;
			String[] roomNames = new String[roomNum / 2];
			String[] requiredPlayers = new String[roomNum / 2];
			
			//parse the arguments
			for (int i = 0; i < roomNum; i = i + 2) {
				roomNames[i / 2] = arguments[i];
				requiredPlayers[i / 2] = arguments[i + 1];
			}
			
			MultiplayerScreen.populateRooms(roomNames, requiredPlayers);
		}
		
	}
	
	/**
	 * Gets the IP of the server using UDP.
	 * @return the servers IP
	 */
	private String getServerIP() {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			byte[] sendData = "DISCOVERY_REQUEST".getBytes();
			
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				
				//skip the interface if its only local or if its down
				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;
				
				//loop through all network addresses
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					
					//skip if there is no broadcast
					if (broadcast == null)
						continue;
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Server.port);
					socket.send(sendPacket);
					
					System.out.println(getClass().getName() + ">>>Request packet sent to " + broadcast.getHostAddress() + ", Interface: "
							+ networkInterface.getDisplayName());
				}
				
			}
			
			System.out.println(getClass().getName() + ">>>Done looping over all network interfaces. Waiting for response from server.");
			
			byte[] buffer = new byte[15000];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			
			System.out.println(getClass().getName() + ">>>Broadcast response from server: " + packet.getAddress().getHostAddress());

			socket.close();
			return new String(packet.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "localhost";
	}

	/**
	 * Sends a request to the server for a list of rooms.
	 */
	public void refreshRooms() {
		try {
			out.writeUTF("ROOMS");
			System.out.println(getClass().getName() + ">>>Sent request to refresh rooms.");
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Sends a request to the server to add a new room.
	 * @param roomName the rooms name
	 */
	public void addRoom(String roomName, String roomNum) {
		try {
			out.writeUTF("NEWROOM/" + roomName + "/" + roomNum);
			System.out.println(getClass().getName() + ">>>Sent request to add room.");
			refreshRooms();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sends a request to the server to join a room.
	 * @param roomName the rooms name
	 */
	public void joinRoom(String roomName) {
		try {
			out.writeUTF("JOIN/" + roomName);
			System.out.println(getClass().getName() + ">>>Sent request to join room.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Adds a ClientListener to the list that receives events.
	 * @param toAdd the ClientListener to add.
	 */
	public void addListener(ClientListener toAdd) {
		listeners.add(toAdd);
	}
	

	
	
	public Socket getSocket() {
		return socket;
	}

	public String getNickname() {
		return nickname;
	}
	
	/**
	 * Retrieves a message from the server
	 * @return the String sent by the server
	 */
	public String getMessage() {
		try {
			return in.readUTF();
		} catch (IOException e) {
			System.out.println("Error reading message.");
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Sends a message to the server.
	 * @param toSend the String to send
	 */
	public void sendMessage(String toSend) {
		try {
			out.writeUTF(toSend);
		} catch (IOException e) {
			System.out.println("Error sending message.");
			e.printStackTrace();
		}
	}
	
}
