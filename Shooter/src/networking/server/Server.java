package networking.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import networking.NetworkUtils;
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
	
	/** Listeners who want to recive events from this server. */
	private ArrayList<ServerListener> listeners = new ArrayList<ServerListener>();
	
	/** The port the server runs on. */
	public final static int port = 3854;
	
	/** The server that accepts connections from sockets. */
	static ServerSocket server;

	private Server() {
		try {
			listeners.add(this);
			
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
					            
					        DataInputStream in = new DataInputStream(socket.getInputStream());
					            
					        //when the client connects, it will send its nickname through
					        String nickname = in.readUTF();
							
					        System.out.println(getClass().getName() + ">>>Client nickname recived from " + socket.getInetAddress().getHostName() + ": " + nickname);
				               
						    clientConncted(new Client(socket, nickname));
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
	
	public void addListener(ServerListener toAdd) {
		listeners.add(toAdd);
	}

	private void clientConncted(Client client) {
		clients.add(client);
		
		//start a thread to listen for messages from that client
		Thread messageListenerThread = new Thread() {
			@Override
			public void run() {
				while (true) {
					try {
						//wait for message
						String message = client.getIn().readUTF();
						for (ServerListener listener : listeners)
							listener.messageReceived(client, message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		};
		
		messageListenerThread.setName(client.getNickname() + " Message Listener Thread");
		messageListenerThread.setDaemon(true);
		messageListenerThread.start();
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
			
			//send all room names
			for (Room room : rooms)
				toSend = toSend + "/" + room.getRoomName();
			
			try {
				client.getOut().writeUTF(toSend);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (command.equals("NEWROOM")) {
			//add the room to the server
			rooms.add(new Room(arguments[0], 2));
		}
		
		if (command.equals("JOIN")) {
			String roomName = arguments[0];
			
			for (Room room : rooms)
				if (room.getRoomName().equals(roomName))
					room.addClient(client);
		}
		
	}

}
