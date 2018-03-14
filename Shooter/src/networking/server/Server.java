package networking.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

import networking.NetworkUtils;

/**
 * This class represents the server that connects clients.
 * @author Connor Stewart
 */
public class Server implements ServerListener {
	
	/** A list of rooms that can be joined by clients. */
	private CopyOnWriteArrayList<Room> rooms = new CopyOnWriteArrayList<Room>();
	
	/** Information about clients connected to the server. */
	private CopyOnWriteArrayList<ClientInfo> clients = new CopyOnWriteArrayList<ClientInfo>();
	
	/** Listeners who want to receive events from this server. */
	private CopyOnWriteArrayList<ServerListener> listeners = new CopyOnWriteArrayList<ServerListener>();

	/** The singleton instance of the server. */
	private final static Server instance = new Server();
	
	/** The port the server accepts TCP messages on. */
	public static final int TCP_PORT = 4521;
	
	/** The port the server sends UDP messages on. */
	public static final int UDP_PORT = 4522;
	
	/** Whether the server is online and ready to receive packets. */
	private boolean running;
	
	/** The TCP socket that this client receives/sends messages on. */
	private ServerSocket tcpServer;
	
	/** The last ip address assigned to a multicast group. */
	private int[] lastGroupAssgined = {225, 255, 255, 233};

	/**
	 * Starts the server and some threads to manage server activities.
	 */
	private Server() {
		listeners.add(this);
		
		try {
			tcpServer = new ServerSocket();
			tcpServer.bind(new InetSocketAddress(InetAddress.getLocalHost(), Server.TCP_PORT));
			running = true;
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
			//this catches BindException, this just means that the server is already running.
			System.out.println(Server.class.getSimpleName() + " >>> Server already running.");
		}
		
		System.out.println(Server.class.getSimpleName() + " >>> Started server on " + tcpServer.getInetAddress() + ".");
		
		new DiscoveryThread().start();
		
		Thread clientConnectionThread = new Thread() {
			@Override
			public void run() {
				while (running) {
					try {
						Socket newConnection = tcpServer.accept();
						ClientInfo newClient = new ClientInfo(newConnection);
						
						System.out.println(Server.class.getSimpleName() + " >>> New client has connection on IP:"
						+ newClient.getIpAddress().getHostAddress() + " ,port:" + newClient.getPort());
						
						new ServerTCPMessageListener(newClient).start();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		clientConnectionThread.start();
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
		
		//debug info
		System.out.println(getClass().getSimpleName() + " >>> Recived command from client '" + info.getNickname() + "'.");
		System.out.println(getClass().getSimpleName() + " >>> Command: '" + command + "'.");
		
		if (arguments.length == 0) {
			System.out.print(getClass().getSimpleName() + " >>> No arguments.");
		} else {
			if (arguments.length > 1) {
				System.out.print(getClass().getSimpleName() + " >>> Argument: ");
			} else {
				System.out.print(getClass().getSimpleName() + " >>> Arguments: ");
			}
			
			for (int i = 0; i < arguments.length; i++) {
				if (i != 0)
					System.out.print(" ," + "'" + arguments[i] + "'");
				else
					System.out.print("'" + arguments[i] + "'");
			}
		}
		
		System.out.println();

		//validation against a command that coulden't be parsed
		if (command == null)
			return;
		
		//a client has sent a request to update their nickname
		if (command.equals("NN")) {
			info.setNickname(arguments[0]);
			sendMessageToSingleClient("<CID/" + info.getClientID() + ">", info);
		}
        
		//a client wishes to add a new room to the server
		if (command.equals("NR")) {
			try {
				rooms.add(new Room(arguments[0], Integer.parseInt(arguments[1])));
				System.out.println(getClass().getSimpleName() + " >>> Added new room '" + arguments[0] +"'.");
			} catch (NumberFormatException e) {
				System.out.println(getClass().getSimpleName() + " >>> Required players isn't an integer skipping room add.");
			}
		}
		
		//a client wants a list of rooms on the server
		if (command.equals("RR")) {
			String toSend = "";
			for (Room room : rooms)
				toSend = toSend + "/" + room.getRoomName() + "/" + room.getRequiredPlayers();
			sendMessageToSingleClient("<RU" + toSend + ">", info);
		}
		
		//a client want to join a game room
		if (command.equals("JR")) {
			String roomName = arguments[0];
			
			for (Room room : rooms)
				if (room.getRoomName().equals(roomName))
					room.addClient(info);
		}
		
		if (command.equals("JGC")) {
			info.setHasJoinedUDPGroup(true);
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
    	try {
    		//send the message to the client
			client.getSocket().getOutputStream().write(message.getBytes());
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
		new ServerGame(room, getMulticastAddress(), getMulticastAddress());
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
		return instance.tcpServer.getInetAddress();
	}

	public CopyOnWriteArrayList<ServerListener> getListeners() {
		return listeners;
	}

	public static boolean isRunning() {
		return getInstance().running;
	}

	public static CopyOnWriteArrayList<ClientInfo> getClients() {
		return instance.clients;
	}
	
	static ServerSocket getTCPSocket() {
		return instance.tcpServer;
	}

}
