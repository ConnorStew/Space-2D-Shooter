package network.server;

import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import database.ScoreDAO;
import network.ConfirmType;
import network.ErrorType;
import network.Network;
import network.Network.*;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * This class handles server functions.
 */
public class ServerHandler extends Listener {
	
	/** The singleton instance of the server handler. */
	private static ServerHandler instance = new ServerHandler();

	/** The server that this handles. */
	private Server server = new Server();

	/** Open rooms on the server. */
	private Array<Room> rooms = new Array<>();
	
	/** The clients connected to the server. */
	private Array<ClientInfo> clients = new Array<>();

	/** Games currently being hosted on the server. */
	private Array<ServerGame> games = new Array<>();

	/**
	 * Starts the server.
	 */
	private ServerHandler() {
		ServerHandler.instance = this;

		//start the server
		server.start();
		
		Network.register(server);
		
		server.addListener(this);

		try {
			server.bind(new InetSocketAddress(InetAddress.getLocalHost(), Network.TCP_PORT), new InetSocketAddress(InetAddress.getLocalHost(), Network.UDP_PORT));
		} catch (IOException e) {
			System.out.println("Server is already running on this network.");
		}
	}

	@Override
	public void connected(Connection connection) {
		clients.add(new ClientInfo(connection));
	}

	@Override
	public void disconnected(Connection connection) {
		ClientInfo left = getClientInfoByConnection(connection);

		//remove the client from games
		for (ServerGame game : games) {
			if (game.getRoom().getClients().contains(left, false)) {
				game.removePlayer(left);
				game.getRoom().getClients().removeValue(left, false);
			}
		}

		//remove client from rooms
		for (Room room : rooms) {
			if (room.getClients().contains(left, false)) {
				room.getClients().removeValue(left, false);
			}
		}

		clients.removeValue(left, false);
	}

	@Override
	public void received(Connection connection, Object object) {
		ClientInfo client = getClientInfoByConnection(connection);

		serverMessages(client, connection, object);
		gameMessages(client, connection, object);
	}

	/**
	 * Responds to messages related to the server.
	 * @param client the client that sent the message
	 * @param connection the connection the client is on
	 * @param object the message the client sent
	 */
	private void serverMessages(ClientInfo client, Connection connection, Object object) {
		if (object instanceof AddRoom) {
			AddRoom msg = (AddRoom) object;
			String roomName = msg.roomName;

			if (roomName.length() > Room.MAX_NAME_LEN) {
				ErrorMessage em = new ErrorMessage();
				em.message = "Your room name is too long (max " + Room.MAX_NAME_LEN +  " characters), your room has not been added.";
				connection.sendTCP(em);
				return;
			}

			boolean duplicateName = false;

			for (Room room : rooms) {
				if (room.getRoomName().equals(roomName)) {
					duplicateName = true;
					continue;
				}
			}

			if (duplicateName == false) {
				for (ServerGame game : games) {
					if (game.getRoom().getRoomName().equals(roomName)) {
						duplicateName = true;
						continue;
					}
				}
			}

			if (duplicateName) {
				ErrorMessage em = new ErrorMessage();
				em.message = "Your room name '" + roomName + "' is already in use, please choose another.";
				connection.sendTCP(em);
				return;
			}

			Room toAdd = new Room(msg.roomName, getClientInfoByConnection(connection));
			toAdd.addClient(getClientInfoByConnection(connection));
			rooms.add(toAdd);
			sendTCPToAll(getRoomUpdate());
		}

		//received a request for a list of available rooms
		if (object instanceof RefreshRooms) {
			connection.sendTCP(getRoomUpdate());
		}

		//a client has requested to join a room
		if (object instanceof JoinRoom) {
			JoinRoom msg = (JoinRoom) object;
			for (Room room : rooms)
				if (room.getRoomName().equals(msg.roomName))
					room.addClient(getClientInfoByConnection(connection));
		}

		//a client has requested to change their nickname
		if (object instanceof UpdateNickname) {
			UpdateNickname msg = (UpdateNickname) object;

			boolean duplicateNickname = false;

			for (ClientInfo clientInfo : clients)
				if (clientInfo.getNickname() != null && clientInfo.getNickname().equals(msg.nickname))
					duplicateNickname = true;

			if (duplicateNickname) {
				ErrorMessage em = new ErrorMessage();
				em.type = ErrorType.DuplicateName;
				em.message = "Your nickname '" + msg.nickname + "' is already in use, please choose another.";
				connection.close();
				connection.sendTCP(em);
				return;
			} else {
				client.setNickname(msg.nickname);
			}

			if (msg.nickname == null || msg.nickname.isEmpty()) {
				ErrorMessage em = new ErrorMessage();
				em.message = "You must give a nickname!";
				connection.sendTCP(em);
				connection.close();
				return;
			}

			if (msg.nickname.length() > ClientInfo.MAX_NAME_LENGTH) {
				ErrorMessage em = new ErrorMessage();
				em.message = "Your nickname is too long (max " + ClientInfo.MAX_NAME_LENGTH +  " characters).";
				connection.sendTCP(em);
				connection.close();
				return;
			}

			ConfirmationMessage reply = new ConfirmationMessage();
			reply.type = ConfirmType.ValidName;
			connection.sendTCP(reply);
		}

		//removes this client from its current room
		if (object instanceof Network.LeaveLobby) {
			for (Room room : rooms)
				if (room.hasClient(client))
					room.removeClient(client);
		}

		//find the clients room and start the game if the client that sent this message is the loader
		if (object instanceof Network.RequestGameStart) {
			Room clientsRoom = null;
			for (Room room : rooms) {
				if (room.getClients().contains(client, false)) {
					clientsRoom = room;
					break;
				}
			}

			if (client != null && clientsRoom != null && client.equals(clientsRoom.getLeader())) {
				startGame(clientsRoom);
				closeRoom(clientsRoom);
			}
		}

		if (object instanceof Network.UploadScore) {
			UploadScore msg = (UploadScore) object;
			new ScoreDAO().writeScore(msg.name, msg.score);
			ConfirmationMessage reply = new ConfirmationMessage();
			reply.type = ConfirmType.ScoreAdded;
			connection.sendTCP(reply);
		}

		if (object instanceof Network.RefreshScores) {
			ScoreUpdate msg = new ScoreUpdate();
			ScoreDAO dao = new ScoreDAO();

			msg.names = dao.getNames();
			msg.scores = dao.getScores();

			connection.sendTCP(msg);
		}
	}

	/**
	 * This class responds to messages about a game a client is in.
	 * @param client the client that sent the message
	 * @param connection the connection the client is on
	 * @param object the message the client sent
	 */
	private void gameMessages(ClientInfo client, Connection connection, Object object) {
		for (ServerGame game : games) {
			if (game.getRoom().getClients().contains(client, false)) {
				game.message(object);
				break;
			}
		}
	}

	/**
	 * Opens a new room on the server.
	 * @param room the room to open
	 */
	private void startGame(Room room) {
		games.add(new ServerGame(room));
	}

	/**
	 * Returns the information know about a client using their connection.
	 * @param connection the clients connection
	 * @return the ClientInfo object
	 */
	private ClientInfo getClientInfoByConnection(Connection connection) {
		for (ClientInfo client : clients)
			if (client.getConnection().equals(connection))
				return client;
		
		return null;
	}

	/**
	 * Gets the singleton instance of this class.
	 * @return the singleton instance of this class
	 */
	public static ServerHandler getInstance() {
		return instance;
	}

	/**
	 * Adds a listener for KryoNet server events.
	 * @param toAdd the listener to add
	 */
	void addListener(Listener toAdd) {
		server.addListener(toAdd);
	}

	/**
	 * Gets the KyroNet server object.
	 * @return the KyroNet server object
	 */
	public Server getServer() {
		return server;
	}

	/**
	 * Sends a TCP message to an array containing client information.
	 * @param clients the array of clients
	 * @param message the message to send
	 */
	void sendTCPTo(Array<ClientInfo> clients, Object message) {
		for (ClientInfo client : clients)
			client.getConnection().sendTCP(message);
	}

	/**
	 * Send TCP to all clients connected to the server.
	 * @param message the message to send
	 */
	private void sendTCPToAll(Object message) {
		for (ClientInfo client : clients)
			client.getConnection().sendTCP(message);
	}

	/**
	 * Removes a room from this list of rooms available rooms to join.
	 * @param room the room to remove
	 */
	void closeRoom(Room room) {
		rooms.removeValue(room, false);
		sendTCPToAll(getRoomUpdate());
	}


	/**
	 * Gets a RoomUpdate object containing the names of all rooms available to join.
	 * @return the RoomUpdate object
	 */
	private RoomUpdate getRoomUpdate() {
		RoomUpdate toSend = new RoomUpdate();
		String[] roomNames = new String[rooms.size];

		for (int i = 0; i < rooms.size; i++)
			roomNames[i] = rooms.get(i).getRoomName();

		toSend.roomNames = roomNames;

		return toSend;
	}

	public void endGame(ServerGame serverGame) {
		games.removeValue(serverGame, false);
		serverGame.close();
		serverGame.dispose();
	}
}
