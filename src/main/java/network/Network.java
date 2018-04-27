package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import java.util.ArrayList;

/**
 * This class stores information relevant to both clients and the server.
 * @author Connor Stewart
 */
public class Network {

	/** The width of a multiplayer game. */
	public static final int GAME_WIDTH = 100;
	
	/** The height of a multiplayer game. */
	public static final float GAME_HEIGHT = 100;

	/** The TCP port the games uses. */
	public static final int TCP_PORT = 2343;

	/** The UDP port the game uses. */
	public static final int UDP_PORT = 2344;
	
	/**
	 * This methods registers objects that are going to be sent over the network.
	 * @param endPoint the endPoint to register the classes to
	 */
	public static void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(String[].class);
		kryo.register(ArrayList.class);
		
		kryo.register(AddRoom.class);
		kryo.register(RoomUpdate.class);
		kryo.register(RefreshRooms.class);
		kryo.register(JoinRoom.class);
		kryo.register(UpdateNickname.class);
		kryo.register(StartGame.class);
		kryo.register(KeyInput.class);
		kryo.register(MouseInput.class);
		kryo.register(AddPlayer.class);
		kryo.register(RemovePlayer.class);
		kryo.register(RemoveProjectile.class);
		kryo.register(UpdateProjectile.class);
		kryo.register(UpdatePlayer.class);
		kryo.register(MouseMoved.class);
		kryo.register(AddProjectile.class);
		kryo.register(JoinLobby.class);
		kryo.register(RequestGameStart.class);
		kryo.register(LeaveLobby.class);
		kryo.register(LobbyPlayers.class);
		kryo.register(LobbyClosed.class);
		kryo.register(ErrorMessage.class);
		kryo.register(ErrorType.class);
		kryo.register(PlayerWon.class);
		kryo.register(UploadScore.class);
		kryo.register(ScoreUpdate.class);
		kryo.register(ConfirmationMessage.class);
		kryo.register(ConfirmType.class);
		kryo.register(RefreshScores.class);
	}

	//client -> server

	/** A request to add a new room to the server. */
	public static class AddRoom {
		/** The rooms name. */
		public String roomName;
	}

	/** A request to upload a score to the database. */
	public static class UploadScore {
		/** The score to upload. */
		public int score;
		/** The nickname of the player who scored the score. */
		public String name;
	}

	/** A request to join a room on the server. */
	public static class JoinRoom {
		/** The name of the room to join. */
		public String roomName;
	}

	/** A request to update this clients nickname. */
	public static class UpdateNickname {
		/** The new nickname. */
		public String nickname;
	}

	/** A request to join a lobby. */
	public static class JoinLobby {
		/** Whether this client is the leader of the lobby. */
		public boolean leader;
	}

	/** A notification that the client has pressed a key. */
	public static class KeyInput {
		/** The players ID. */
		public int id;
		/** The code of the pressed key. */
		public int keyCode;
	}

	/** A notification that the client has pressed a mouse button. */
	public static class MouseInput {
		/** The players ID. */
		public int id;
		/** The code of the pressed mouse button. */
		public int buttonCode;
	}

	/** A notification that the client has moved their mouse. */
	public static class MouseMoved {
		/** The players ID. */
		public int id;
		/** The mouses x coordinate within the game. */
		public float x;
		/** The mouses y coordinate within the game. */
		public float y;
	}

	/** The client is requesting a list of scores. */
	public static class RefreshScores {}

	/** The client is requesting that their lobby's game is started. */
	public static class RequestGameStart {}

	/** The client is requesting a new set of available rooms. */
	public static class RefreshRooms {}

	/** The client is requesting to leave a lobby. */
	public static class LeaveLobby {}

	//server -> clients

	/** An update containing available rooms to join. */
	public static class RoomUpdate {
		/** The names of available rooms. */
		public String[] roomNames;
	}

	/** An update containing the scores from the database. */
	public static class ScoreUpdate {
		/** The scores from the database. */
		public ArrayList<Integer> scores;
		/** The names from the database. */
		public ArrayList<String> names;
	}

	/** A notification that a player has won the game. */
	public static class PlayerWon {
		/** The ID of the winning player. */
		public int id;
	}

	/** An update containing the names of players in a lobby. */
	public static class LobbyPlayers {
		/** A list of names of the players in a lobby. */
		public String[] players;
	}

	/** An update telling clients to add a player to their game on client-side. */
	public static class AddPlayer {
		/** The new players nickname. */
		public String name;

		/** The new players multiplayer ID. */
		public int id;
	}

	/** An update telling clients to add a projectile to their game on client-side. */
	public static class AddProjectile {
		/** The ID of the player who fired the projectile. */
		public int playerID;
		/** The ID of the projectile. */
		public int id;
		/** The type of projectile being fired. */
		public String type;
	}

	/** An update telling clients to remove a player from their game on client-side. */
	public static class RemovePlayer {
		/** The players multiplayer ID. */
		public int id;
	}

	/** An update telling clients to remove a projectile from their game on client-side. */
	public static class RemoveProjectile {
		/** The projectiles multiplayer ID. */
		public int id;
	}

	/** This update is used to describe the position of a projectile. */
	public static class UpdateProjectile {
		/** The multiplayer ID of the projectile that should be updated. */
		public int id;
		/** The projectiles x coordinate. */
		public float x;
		/** The projectiles y coordinate. */
		public float y;
		/** The projectiles rotation. */
		public double r;
	}

	/** This update is used to describe the attributes of a player. */
	public static class UpdatePlayer {
		/** The multiplayer ID of the player that should be updated. */
		public int id;
		/** The players x coordinate. */
		public float x;
		/** The players y coordinate. */
		public float y;
		/** The players rotation. */
		public double r;
		/** The players health. */
		public double health;
		/** The amount of kills the player has. */
		public int kills;
	}

    /** This class is used to send error messages to clients. */
	public static class ErrorMessage {
		/** The type of error. */
		public ErrorType type;
		/** The error message. */
		public String message;
	}

	/** This class is used to send confirmation messages to the client. */
	public static class ConfirmationMessage {
		/** The type of message. */
		public ConfirmType type;
	}

	/** The server is telling a client to start a multiplayer client-side game. */
	public static class StartGame {}

	/** The server is telling a client that their lobby has been closed. */
	public static class LobbyClosed {}

}
