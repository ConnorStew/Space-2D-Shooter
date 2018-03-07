package networking.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;

import networking.NetworkUtils;
import networking.server.Server;
import ui.MPGame;
import ui.UI;

/**
 * This class represents a player who controllers a character on a multiplayer game.
 * @author Connor Stewart
 */
public class Client implements ClientListener {
	
	/** The socket that listens for UDP messages from the server. */
	private MulticastSocket udpSocket;
	
	/** The socket that receives and sends TCP messages to the server. */
	private Socket tcpSocket;
	
	/** The servers IP. */
	private InetAddress serverIP;
	
	/** The clients nickname. */
	private String nickname;
	
	/** A list of ClientListeners that wish to receive events from this client. */
	private CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<ClientListener>();
	
	/** The multicast address to receive updates about game rooms. */
	private InetAddress roomGroup;
	
	/** Whether this client wants to receive packets from the server. */
	private boolean running = true;
	
	/** If the server has received this clients nickname. */
	private boolean nicknameReceived = false;

	/**
	 * This constructor should be used to create a new client and connect to the server.<br>
	 * <u>This should be used on the client side.</u>
	 * @param nickname the clients nickname
	 */
	public Client(String nickname) {
		listeners.add(this);
		
		try {
			udpSocket = new MulticastSocket(Server.MULTI_PORT);
			tcpSocket = new Socket();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//thread to listen for messages from the server
		Thread singleServerListeningThread = new Thread() {
			@Override
			public void run() { 
				while (running) {
					byte[] buffer = new byte[256];
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					
					try {
						udpSocket.receive(dp);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					for (ClientListener listener : listeners)
						listener.messageReceived(new String(dp.getData()));
				}
			}
		};
		
		//thread to listen for messages from the server
		Thread multipleServerListeningThread = new Thread() {
			@Override
			public void run() { 
				while (running) {

					
					for (ClientListener listener : listeners)
						listener.messageReceived(new String(dp.getData()));
				}
			}
		};
		
		singleServerListeningThread.setName(nickname + "'s Multicast UDP Listening Thread");
		singleServerListeningThread.start();
		
		multipleServerListeningThread.setName(nickname + "'s UDP Listening Thread");
		multipleServerListeningThread.start();
		
		//ping network interfaces to find the game server
		while (serverIP == null)
			pingServerForConnection();
		
		//send nickname to the server
		while (nicknameReceived == false)
			sendMessage("<NN/" + nickname + ">");
		
		//request the room group multicast address
		sendMessage("<RG>");
	}
	
	@Override
	public void messageReceived(String message) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		System.out.println(getClass().getName() + ">>> Received message: " + message);
		
		//the server has sent its IP
		if (command.equals("IP")) {
			try {
				serverIP = InetAddress.getByName(arguments[0]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
		}
		
		//the server has sent the room group IP
		if (command.equals("RGIP")) {
			try {
				roomGroup = InetAddress.getByName(arguments[0]);
				udpSocket.joinGroup(roomGroup);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//the server has requested that this client joins a group
		if (command.equals("JG")) {
			try {
				InetAddress groupIP = InetAddress.getByName(arguments[0]);
				udpSocket.joinGroup(groupIP);
				System.out.println(getClass().getName() + ">>> Joined group on IP " + groupIP);
				
				//let the server know you have joined the group
				sendMessage("<JGC>");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//the server has requested that this client joins a group
		if (command.equals("SG")) {
			Client client = this;
			MPGame toStart = new MPGame(client);
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					UI.getInstance().setScreen(toStart);
				}
			});
		}
		
		//the server has received the nickname
		if (command.equals("NNR"))
			nicknameReceived = true;
	}
	
	/**
	 * Pings all available broadcast addresses to find the server and request to connect.
	 */
	private void pingServerForConnection() {
		try {
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
					
					//send a request to this broadcast address to connect
					sendMessage("<RC>", broadcast);
					
					System.out.println(getClass().getSimpleName() + " >>> Request packet sent to " + broadcast.getHostAddress() + ", Interface: "
							+ networkInterface.getDisplayName() + ".");
				}
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Prints debug info to check how large a message is that is being sent.
	 * @param stringToSend the string that is going to be sent to the server
	 */
	@SuppressWarnings("unused")
	private void printMessageDebugInfo(String stringToSend) {
		try {
			String bytesAsString = new String("");
			byte[] bytesToSend;
			
			bytesToSend = stringToSend.getBytes("UTF-8");
			
			for (byte data : bytesToSend)
				bytesAsString += data;
			
			for (int i = 0; i < stringToSend.length(); i++) {
				String currentCharacter = stringToSend.substring(i, i + 1);
				
				byte characterAsByte = currentCharacter.getBytes()[0];
				
				String bitsInByte = Integer.toBinaryString(characterAsByte);
				
				if (bitsInByte.length() < 8)
					bitsInByte = ("00000000" + bitsInByte).substring(bitsInByte.length());
					
				System.out.println(currentCharacter + " in bits (binary): " + bitsInByte);
				System.out.println(currentCharacter + " in bytes: " + characterAsByte);
			}
			
			System.out.println("Total bytes: " + bytesAsString);
			System.out.println("Whole string: " + new String(bytesToSend, "UTF-8"));
			System.out.println("Size of byte array: " + bytesToSend.length);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message to the server's single socket via UDP.
	 * @param message the message
	 */
	public void sendMessage(String message) {
		byte[] buffer = message.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, serverIP, Server.SINGLE_PORT);
		
		try {
			tcpSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Send a message to an InetAddress via UDP.
	 * @param message the message
	 */
	public void sendMessage(String message, InetAddress toSend) {
		byte[] buffer = message.getBytes();
		
		DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, toSend, Server.SINGLE_PORT);
		
		try {
			tcpSocket.send(sendPacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a request to the server to add a new room.
	 * @param roomName the rooms name
	 */
	public void addRoom(String roomName, String roomNum) {
		sendMessage("<NR/" + roomName + "/" + roomNum + ">");
		System.out.println(getClass().getSimpleName() + " >>> Sent request to add room.");
		refreshRooms();
	}
	
	/**
	 * Sends a request to the server for a list of rooms.
	 */
	public void refreshRooms() {
		sendMessage("<RR>");
		System.out.println(getClass().getSimpleName() + " >>> Sent request to refresh rooms.");	
	}
	
	/**
	 * Sends a request to the server to join a room.
	 * @param roomName the rooms name
	 */
	public void joinRoom(String roomName) {
		sendMessage("<JR/" + roomName + ">");
		System.out.println(getClass().getSimpleName() + " >>> Sent request to join room.");
	}

	public void addListener(ClientListener toAdd) {
		listeners.add(toAdd);
	}
	
	/**
	 * @return the players nickname
	 */
	public String getNickname() {
		return nickname;
	}
}
