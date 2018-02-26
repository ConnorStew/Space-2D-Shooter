package networking.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import networking.MessageListener;
import networking.NetworkUtils;
import networking.Type;
import networking.client.Client;

/**
 * This class represents the server that connects clients.
 * @author Connor Stewart
 */
public class Server implements ServerListener {
	
	/** An ArrayList of rooms that can be modified concurrently. */
	private CopyOnWriteArrayList<Room> rooms = new CopyOnWriteArrayList<Room>();
	
	/** Clients connected to the server. */
	private CopyOnWriteArrayList<Client> clients = new CopyOnWriteArrayList<Client>();

	/** The singleton instance of the server. */
	private final static Server instance = new Server();
	
	/** The port the server runs on. */
	public final static int port = 3854;
	
	/** The server that accepts connections from sockets. */
	static ServerSocket server;
	
	/** Listeners who want to recive events from this server. */
	private ArrayList<ServerListener> sListeners = new ArrayList<ServerListener>();

	private Server() {
		try {
			sListeners.add(this);
			
			server = new ServerSocket(port, 0, InetAddress.getLocalHost());
			
			//sends UDP packets out to let clients get this servers IP address
			new DiscoveryThread().start();
			
			//start listening for clients
			Thread clientConnectionListener = new Thread(){
				@Override
				public void run() { 
					try {	
						while (true) {
							System.out.println(getClass().getName() + ">>>Listening for new clients!");
							
							//wait for a client to request a connection
				            Socket socket = server.accept();
					        System.out.println(getClass().getName() + ">>>Client connected on " +  socket.getInetAddress().getHostName());
					            
						    clientConncted(new Client(socket, "default"));
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			};
			
			clientConnectionListener.setName("Client Connection Listener");
			clientConnectionListener.setDaemon(true);
			clientConnectionListener.start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//this catches BindException, this just means that the server is already running.
			System.out.println(Server.class.getName() + ">>>Server already running.");
		}
		
	}

	private void clientConncted(Client client) {
		clients.add(client);
		MessageListener ml = new MessageListener(client,Type.Server);
		ml.start();
	}

	/**
	 * A room has requested that its game be started
	 * @param room
	 */
	void startGame(Room room) {
		new ServerGameThread(room).start();
	}
	
	public static Server getInstance() {
		return instance ;
	}

	@Override
	public void messageReceived(Client client, String message) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);

		System.out.println(getClass().getName() + ">>>Recived command from " + client.getNickname() +  ": " + command);

		if (command.equals("ROOMS")) {
			String toSend = "ROOMS";
			
			//get all room names to send
			for (Room room : rooms)
				toSend = toSend + "/" + room.getRoomName() + "/" + room.getRequiredPlayers();
			
			NetworkUtils.sendMessage(toSend, client.getOutputStream());
		}
		
		//add a room to the server
		if (command.equals("NEWROOM")) {
			try {
				rooms.add(new Room(arguments[0], Integer.parseInt(arguments[1])));
			} catch (NumberFormatException e) {
				System.out.println(getClass().getName() + ">>>Required players isn't an integer skipping room add.");
			}
		}
			
		if (command.equals("JOIN")) {
			String roomName = arguments[0];
			
			for (Room room : rooms)
				if (room.getRoomName().equals(roomName))
					room.addClient(client);
		}
        
        if (command.equals("NICKNAME")) {
        	client.setNickname(arguments[0]);
            System.out.println(getClass().getName() + ">>>Client nickname recived from " + client.getSocket().getInetAddress().getHostName() + ": " + client.getNickname());
        }
		
	}

	public void addListener(ServerListener sl) {
		sListeners.add(sl);
	}

	public ArrayList<ServerListener> getListeners() {
		return sListeners;
	}

}
