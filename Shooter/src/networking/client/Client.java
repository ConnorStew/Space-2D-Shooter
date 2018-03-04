package networking.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Enumeration;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;

import networking.MessageListener;
import networking.NetworkUtils;
import networking.Type;
import networking.server.Server;
import ui.MPGame;
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
	private OutputStream out;
	
	/** The clients output to the server. */
	private InputStream in;
	
	/** A list of ClientListeners that wish to receive events from this client. */
	private CopyOnWriteArrayList<ClientListener> listeners = new CopyOnWriteArrayList<ClientListener>();

	/**
	 * This constructor should be used to create a new client and connect to the server.<br>
	 * <u>This should be used on the client side.</u>
	 * @param nickname the clients nickname
	 */
	public Client(String nickname) {
		this.nickname = nickname;
		listeners.add(this);

		try {
			//connect to the server
			socket = new Socket(getServerIP(), Server.port);
			System.out.println(getClass().getName() + ">>>Connected to the server.");
			
			out = socket.getOutputStream();
			in = socket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//send nickname through when connected
		NetworkUtils.sendMessage("NICKNAME/" + nickname, out);
		System.out.println(getClass().getName() + ">>>Sent nickname.");
		
		MessageListener ml = new MessageListener(this, Type.Client);
		ml.start();
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
		
		System.out.println(getClass().getName() + ">>>Recived message from server: " + message);
		
		//server letting the client know that the game is ready to begin
		if (command.equals("START_GAME")) {
			Client client = this;
			MPGame toStart = new MPGame(client);
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					UI.getInstance().setScreen(toStart);
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
		NetworkUtils.sendMessage("ROOMS", out);
		System.out.println(getClass().getName() + ">>>Sent request to refresh rooms.");	
	}
	
	/**
	 * Sends a request to the server to add a new room.
	 * @param roomName the rooms name
	 */
	public void addRoom(String roomName, String roomNum) {
		NetworkUtils.sendMessage("NEWROOM/" + roomName + "/" + roomNum, out);
		System.out.println(getClass().getName() + ">>>Sent request to add room.");
		refreshRooms();
	}

	/**
	 * Sends a request to the server to join a room.
	 * @param roomName the rooms name
	 */
	public void joinRoom(String roomName) {
		NetworkUtils.sendMessage("JOIN/" + roomName, out);
		System.out.println(getClass().getName() + ">>>Sent request to join room.");
	}

	
	public Socket getSocket() {
		return socket;
	}

	public String getNickname() {
		return nickname;
	}

	public InputStream getInputStream() {
		return in;
	}

	public OutputStream getOutputStream() {
		return out;
	}
	
	public CopyOnWriteArrayList<ClientListener> getListeners() {
		return listeners;
	}

	public void addListner(ClientListener listener) {
		listeners.add(listener);
	}

	public void setNickname(String newNickname) {
		nickname = newNickname;
	}
	
}
