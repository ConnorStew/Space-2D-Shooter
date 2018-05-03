package network.server;

import com.esotericsoftware.kryonet.Connection;

/**
 * This class stores information that the server knows about a client.
 */
class ClientInfo {

	/** This clients nickname. */
	private String nickname;

	/** This clients connection to the server. */
	private Connection conn;

	/** The ID of this clients player. */
	private int multiplayerID;

	/** The maximum length of a nickname. */
	static int MAX_NAME_LENGTH = 30;

	ClientInfo(Connection connection) {
		conn = connection;
	}

	/**
	 * @return this clients nickname
	 */
	String getNickname() {
		return nickname;
	}

	/**
	 * @return the connection this client is connected on
	 */
	Connection getConnection() {
		return conn;
	}

	/**
	 * @return this clients multiplayer ID
	 */
	int getID() {
		return multiplayerID;
	}

	/**
	 * Assigns this client a nickname.
	 * @param nickname the clients new nickname
	 */
	void setNickname(String nickname) {
		if (nickname != null)
			this.nickname = nickname;
	}

	/**
	 * Set the clients multiplayer ID
	 * @param id the new ID
	 */
	void setMultiplayerID(int id) {
		multiplayerID = id;
	}

}
