package networking.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

import networking.NetworkUtils;

/**
 * This class represents the server that connects clients.
 * @author Connor Stewart
 */
public class Server implements ServerListener {
	
	/** An ArrayList of rooms that can be modified concurrently. */
	private CopyOnWriteArrayList<Room> rooms = new CopyOnWriteArrayList<Room>();
	
	/** Clients connected to the server. */
	private CopyOnWriteArrayList<ClientInfo> clients = new CopyOnWriteArrayList<ClientInfo>();
	
	/** Listeners who want to receive events from this server. */
	private CopyOnWriteArrayList<ServerListener> listeners = new CopyOnWriteArrayList<ServerListener>();

	/** The singleton instance of the server. */
	private final static Server instance = new Server();
	
	/** The port the DatagramSocket runs on. */
	public static final int SINGLE_PORT = 4521;
	
	/** The port the MulticastSocket runs on. */
	public static final int MULTI_PORT = 4522;
	
	/** Whether the server is online and ready to receive packets. */
	private boolean running;
	
	/** The socket that sends messages to a single client. */
	private DatagramSocket singleSocket;
	
	/** The IP address this server is running on. */
	private InetAddress serverIPAddress;
	
	/** The last ip address assigned to a multicast group. */
	private int[] lastGroupAssgined = {225, 10, 10, 10};
	
	/** The multicast address to send information about rooms to. */
	private InetAddress roomGroup;

	/**
	 * Starts the server and some threads to manage server activities.
	 */
	private Server() {
		listeners.add(this);
		
		try {
			serverIPAddress = InetAddress.getLocalHost();
			roomGroup = InetAddress.getByName("224.80.30.34");
			singleSocket = new DatagramSocket(new InetSocketAddress(serverIPAddress, Server.SINGLE_PORT));
			running = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			//this catches BindException, this just means that the server is already running.
			System.out.println(Server.class.getSimpleName() + " >>> Server already running.");
		}
		
		//start listening for messages sent to this servers DatagamSocket
		Thread serverSingleMessageListener = new Thread(){
			@Override
			public void run() { 
				while (running) {
					byte[] buffer = new byte[256];
					DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
						
					try {
						singleSocket.receive(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
						
					System.out.println(Server.class.getSimpleName() + " >>> Received packet: " + new String(buffer));
					
					for (ServerListener listener : listeners)
						listener.messageReceived(new ClientInfo(packet.getAddress(), packet.getPort()), new String(packet.getData()));
				}
			}
		};
	
		//thread to time clients out if they don't send a message for too long
		Thread clientValidation = new Thread() {
			
			/** Delay before a client is considered timed out in milliseconds. Currently 10 minutes.*/
			private final long delay = 100000;
			
			@Override
			public void run() { 
				while (running) {
					long time = System.currentTimeMillis();
					
					for (ClientInfo info : clients)
						if ((time - info.getRecivedMessageTime()) > delay)
							clientDisconnected(info);
				}
			}
		};
		

		clientValidation.setName("Client Validation Thread");
		clientValidation.setDaemon(true);
		clientValidation.start();
		
		serverSingleMessageListener.setName("Server Message Listener Thread");
		serverSingleMessageListener.start();
	}

	@Override
	public void messageReceived(ClientInfo info, String message) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		//if the client is new add them to the list of clients
		if (!clients.contains(info))
			clients.add(info);
		else
			info = clients.get(clients.indexOf(info));

		//update the last time the server received a message from this client
		info.recivedMessage();
		
		//describe the client that sent the message
		System.out.println(getClass().getSimpleName() + ">>>Recived command from " + info.getNickname() +  ": " + command);

		//validation against a command that coulden't be parsed
		if (command == null)
			return;
		
		//a client has sent a request to update their nickname
		if (command.equals("NN")) {
			info.setNickname(arguments[0]);
			sendMessageToSingleClient("<NNR>", info);
		}
		
		//a client is requesting a connection to the server
        if (command.equals("RC")) {
        	//send the client the servers IP address
        	sendMessageToSingleClient("<IP/" + serverIPAddress.getHostAddress() +  ">", info);
        }
        
		//a client is requesting the room groups IP address
        if (command.equals("RG")) {
        	//no "/" after RGIP because the room group will start with an "/"
        	sendMessageToSingleClient("<RGIP" + roomGroup +  ">", info);
        }
        
		//a client wishes to add a new room to the server
		if (command.equals("NR")) {
			try {
				rooms.add(new Room(arguments[0], Integer.parseInt(arguments[1])));
			} catch (NumberFormatException e) {
				System.out.println(getClass().getSimpleName() + " >>> Required players isn't an integer skipping room add.");
			}
		}
		
		//a client wants a list of rooms on the server
		if (command.equals("RR")) {
			String toSend = "";
			for (Room room : rooms) {
				toSend = toSend + "/" + room.getRoomName() + "/" + room.getRequiredPlayers();
			}
			sendMessageToSingleClient("<RU" + toSend + ">", info);
		}
		
		//a client want to join a game room
		if (command.equals("JR")) {
			String roomName = arguments[0];
			
			for (Room room : rooms)
				if (room.getRoomName().equals(roomName))
					room.addClient(info);
		}
        
	}

	@Override
	public void clientDisconnected(ClientInfo info) {
		clients.remove(info);
		System.out.println(Server.class.getSimpleName() + " >>> Client with the ip " + info.getIpAddress() + " has disconnected.");
	}
	
	
	/**
	 * Add a new ServerListener to receive events from the server.
	 * @param sl the ServerListener to add
	 */
	public void addListener(ServerListener sl) {
		listeners.add(sl);
	}

	/**
	 * Remove a ServerListener from the servers list.
	 * @param sl the ServerListener to remove
	 */
	public void removeListener(ServerListener listener) {
		listeners.remove(listener);
	}

	/**
	 * @return the singleton instance of the server
	 */
	public static Server getInstance() {
		return instance ;
	}

	/**
	 * Sends a message to a single client.
	 * @param message the message to send
	 * @param client the client to send the message to
	 */
	void sendMessageToSingleClient(String message, ClientInfo client) {
		byte[] buffer = message.getBytes();
		
    	try {
    		//send the message to the client
			singleSocket.send(new DatagramPacket(buffer, buffer.length, client.getIpAddress(), client.getPort()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This is called when a room has enough players to start. <br>
	 * This creates a new instance of ServerGame.
	 * @param room the room that wishes to start their game
	 */
	public void startGame(Room room) {
		new ServerGame(room, getMulticastAddress());
		rooms.remove(room); //remove the room from join able rooms as it has started
	}
	
	private InetAddress getMulticastAddress() {
		//increment the last part of the IP
		lastGroupAssgined[3]++;
		
		InetAddress toReturn = null;
		
		try {
			toReturn = InetAddress.getByName(lastGroupAssgined[0] + "." + lastGroupAssgined[1] + "." + lastGroupAssgined[2] + "." + lastGroupAssgined[3]);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		return toReturn;
	}
	
	public static InetAddress getServerIP() {
		return instance.serverIPAddress;
	}

}
