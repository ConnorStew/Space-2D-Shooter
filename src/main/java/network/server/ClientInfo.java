package network.server;

import com.esotericsoftware.kryonet.Connection;

public class ClientInfo {
	
	private String nickname;
	
	private Connection conn;
	
	/** The id of the player this client controls. */
	private int playerID;

	ClientInfo(Connection connection) {
		conn = connection;
	}

	String getNickname() {
		return nickname;
	}

	Connection getConnection() {
		return conn;
	}

	void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPlayerID() {
		return playerID;
	}

	void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

}
