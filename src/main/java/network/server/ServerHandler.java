package network.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import network.Network;
import network.Network.AddRoom;
import network.Network.JoinRoom;
import network.Network.RefreshRooms;
import network.Network.RoomUpdate;
import network.Network.UpdateNickname;

public class ServerHandler {
	
	/** The singleton instance of the server handler. */
	private final static ServerHandler instance = new ServerHandler();

	/** The server that this handles. */
	private Server server = new Server();

	/** Whether the server is running. */
	private boolean running = true;
	
	/** Open rooms on the server. */
	private ArrayList<Room> rooms = new ArrayList<Room>();
	
	/** The clients connected to the server. */
	private ArrayList<ClientInfo> clients = new ArrayList<ClientInfo>();
	
	private ServerHandler() {
		//start the server
		server.start();
		
		Network.register(server);
		
		server.addListener(new Listener() {
			@Override
			public void connected(Connection connection) {
				clients.add(new ClientInfo(connection));
			}
			
			@Override
			public void disconnected(Connection connection) {
				clients.remove(getClientByConnection(connection));
			}
			
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof AddRoom) {
					AddRoom msg = (AddRoom) object;
					rooms.add(new Room(msg.roomName, Integer.parseInt(msg.requiredPlayers)));
					System.out.println("Server >>> Added room '" + msg.roomName + "'.");
				}
				
				if (object instanceof RefreshRooms) {
					RoomUpdate toSend = new RoomUpdate();
					String[] roomNames = new String[rooms.size()];
					String[] requiredPlayers = new String[rooms.size()];
					
					for (int i = 0; i < rooms.size(); i++) {
						roomNames[i] = rooms.get(i).getRoomName();
						requiredPlayers[i] = String.valueOf(rooms.get(i).getRequiredPlayers());
					}
					
					toSend.roomNames = roomNames;
					toSend.requiredPlayers = requiredPlayers;
					
					connection.sendTCP(toSend);
				}
				
				if (object instanceof JoinRoom) {
					JoinRoom msg = (JoinRoom) object;
					for (Room room : rooms) 
						if (room.getRoomName().equals(msg.roomName))
							room.addClient(getClientByConnection(connection));
				}
				
				if (object instanceof UpdateNickname) {
					UpdateNickname msg = (UpdateNickname) object;
					getClientByConnection(connection).setNickname(msg.nickname);
				}
			}
		});
		try {
			server.bind(new InetSocketAddress(InetAddress.getLocalHost(), Network.TCP_PORT), new InetSocketAddress(InetAddress.getLocalHost(), Network.UDP_PORT));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ServerHandler getInstance() {
		return instance;
	}

	public void addListener(Listener toAdd) {
		server.addListener(toAdd);
	}

	public boolean isRunning() {
		return running ;
	}

	public void startGame(Room room) {
		System.out.println("Request to start room " + room.getRoomName());
		new ServerGame(room);
	}
	
	public ClientInfo getClientByConnection(Connection connection) {
		for (ClientInfo client : clients)
			if (client.getConnection().equals(connection))
				return client;
		
		return null;
	}
	
	public Server getServer() {
		return server;
	}
	
}
