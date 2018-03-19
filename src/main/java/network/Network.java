package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

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
	}
	
	public static class AddRoom {
		public String roomName;
		public String requiredPlayers;
	}
	
	public static class RoomUpdate {
		public String[] roomNames;
		public String[] requiredPlayers;
	}
	
	public static class JoinRoom {
		public String roomName;
	}
	
	public static class UpdateNickname {
		public String nickname;
	}
	
	public static class KeyInput {
		public int id;
		public int keyCode;
	}
	
	public static class MouseInput {
		public int id;
		public int buttonCode;
	}
	
	public static class AddPlayer {
		public String name;
		public int id;
	}
	
	public static class RemoveProjectile {
		public int id;
	}
	
	public static class RemovePlayer {
		public int id;
	}

	public static class UpdateProjectile {
		public int id;
		public float x;
		public float y;
		public double r;
	}

	public static class UpdatePlayer {
		public int id;
		public float x;
		public float y;
		public double r;
		public double health;
		public int kills;
	}
	
	public static class AddProjectile {
		public int playerID;
		public int id;
		public String type;
	}
	
	public static class MouseMoved {
		public int id;
		public float x;
		public float y;
	}
	
	public static class StartGame {}
	public static class RefreshRooms {}
}
