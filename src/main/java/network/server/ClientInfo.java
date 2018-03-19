package network.server;

import com.esotericsoftware.kryonet.Connection;

public class ClientInfo {
	
	private String nickname;
	
	private Connection conn;
	
	/** The id of the player this client controls. */
	private int playerID;

	public ClientInfo(Connection connection) {
		conn = connection;
	}

	public String getNickname() {
		return nickname;
	}

	public Connection getConnection() {
		return conn;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

}
