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
import java.net.SocketException;
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
	
	/** The socket that sends/receives messages to the game. */
	private MulticastSocket udpSocket;
	
	/** The socket that receives and sends TCP messages to the server. */
	private Socket tcpSocket;
	
	/** The servers IP. */
	private InetAddress serverIP;
	
	/** This clients nickname. */
	private String nickname;
	
	/** Whether this client wants to receive packets from the server. */
	private boolean running = true;
	
	/** The IP address of the game's DatagramSocket. */
	private InetAddress gameIP;
	
	/** The port of the games DatagramSocket. */
	private int gamePort;

	/** A list of ClientListeners that wish to receive events from this client. */
	private CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<ClientListener>();

	/** This clients id. */
	private int clientID;
	
	/** The port this client listens for UDP messages on. */
	public static int UDP_PORT = 5425;
	
	/**
	 * This constructor should be used to create a new client and connect to the server.<br>
	 * <u>This should be used on the client side.</u>
	 * @param nickname the clients nickname
	 * @throws SocketException 
	 */
	public Client(String nickname) throws SocketException {
		this.nickname = nickname;
		listeners.add(this);
		
		try {
			serverIP = InetAddress.getByName(pingServerForConnection());
			udpSocket = new MulticastSocket(Client.UDP_PORT);
			tcpSocket = new Socket(serverIP, Server.TCP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Thread clientTCPMessageListener = new Thread() {
			@Override
			public void run() {
				while (running) {
					byte[] buffer = new byte[1000];
					
					try {
						tcpSocket.getInputStream().read(buffer);
					} catch (IOException e) {
						if (e instanceof SocketException)
							System.out.println(getClass().getName() + " >>> Lost connection to the server.");
						else
							e.printStackTrace();
					}
					
					String message = new String(buffer).trim();
					
					System.out.println(Client.class.getSimpleName() + " >>> Recieved TCP message '" + message + "' from server: " + serverIP.getHostAddress().trim() + ".");
						
					for (ClientListener l : listeners)
						l.messageReceived(message, tcpSocket.getInetAddress());

				}
			}
		};
		
		
		Thread clientUDPMessageListener = new Thread() {
			@Override
			public void run() {
				while (running) {

					byte[] buffer = new byte[256];
					
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					String message = null;
					
					try {
						udpSocket.receive(dp);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					message = new String(dp.getData());
					
					//System.out.println(Client.class.getSimpleName() + " >>> Recieved UDP message '" + message.trim() + "' on port " + Client.UDP_PORT);
					
					for (ClientListener l : listeners)
						l.messageReceived(message, udpSocket.getInetAddress());
				}
			}
		};
		
		clientUDPMessageListener.setName("Client UDP Message Listener");
		clientUDPMessageListener.start();
		
		clientTCPMessageListener.setName("Client TCP Message Listener");
		clientTCPMessageListener.start();
		
		sendMessageToServer("<NN/" + nickname + ">");
	}
	
	public void messageReceived(String message, InetAddress address) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		if (command == null)
			return;
		
		//the server has requested that this starts their game
		if (command.equals("SG")) {
			try {
				gameIP = InetAddress.getByName(arguments[0]);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
			gamePort = Integer.parseInt(arguments[1]);
						
			final MPGame toStart = new MPGame(this);
			Gdx.app.postRunnable(new Runnable(){
				public void run() {
					UI.getInstance().setScreen(toStart);
				}
			});
		}
		
		if (command.equals("CID")) {
			clientID = Integer.parseInt(arguments[0]);
		}
		
	}
	
	/**
	 * Pings all available broadcast addresses to find the server and request to connect.
	 * @return 
	 */
	private String pingServerForConnection() {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] sendData = "DR".getBytes();
			
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
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Server.UDP_PORT);
					socket.send(sendPacket);
					
					System.out.println(getClass().getSimpleName() + " >>> Request packet sent to " + broadcast.getHostAddress() + ", Interface: "
							+ networkInterface.getDisplayName() + ".");
				}
				
			}
			
			byte[] buffer = new byte[256];
			socket.receive(new DatagramPacket(buffer, buffer.length));
			
			socket.close();
			
			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "localhost";
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
	 * Sends a message to the server using TCP.
	 * @param toSend the String to send
	 */
	public void sendMessageToServer(String toSend) {
		try {
			//error here
			//System.out.println(getClass().getSimpleName() + " >>> Sending message to server : " + toSend);
			tcpSocket.getOutputStream().write(toSend.getBytes("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a message to the game the player is currently in using UDP.
	 * @param toSend the String to send
	 */
	public void sendMessageToGame(String toSend) {
		try {
			byte[] buffer = (clientID + "#" + toSend).getBytes();
			System.out.println(getClass().getSimpleName() + " >>> Sending message to game : " + toSend);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, gameIP, gamePort);
			udpSocket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sends a request to the server to add a new room.
	 * @param roomName the rooms name
	 */
	public void addRoom(String roomName, String roomNum) {
		sendMessageToServer("<NR/" + roomName + "/" + roomNum + ">");
		System.out.println(getClass().getSimpleName() + " >>> Sent request to add room.");
		refreshRooms();
	}
	
	/**
	 * Sends a request to the server to join a room.
	 * @param roomName the rooms name
	 */
	public void joinRoom(String roomName) {
		sendMessageToServer("<JR/" + roomName + ">");
		System.out.println(getClass().getSimpleName() + " >>> Sent request to join room.");
	}
	
	/**
	 * Sends a request to the server for a list of rooms.
	 */
	public void refreshRooms() {
		sendMessageToServer("<RR>");
		System.out.println(getClass().getSimpleName() + " >>> Sent request to refresh rooms.");	
	}
	

	/**
	 * Add a listener to receive event about this client.
	 * @param toAdd the listener to add
	 */
	public void addListener(ClientListener toAdd) {
		listeners.add(toAdd);
	}
	
	/**
	 * @return the players nickname
	 */
	public String getNickname() {
		return nickname;
	}

	public boolean isRunning() {
		return running;
	}

	public CopyOnWriteArrayList<ClientListener> getListeners() {
		return listeners;
	}
}
