package networking.server;

import java.net.InetAddress;
import java.net.Socket;

/**
 * This class represents the information that the server has about a particular client that has connected.
 * @author Connor Stewart
 */
public class ClientInfo {
	
	/** The IP address of this client. */
	private InetAddress ipAddress;
	
	/** The port this client is connected through. */
	private int port;
	
	/** The time in milliseconds that this client last sent a message to the server. */
	private long recivedMessageTime;
	
	/** The clients nickname. */
	private String nickname;
	
	/** This clients TCP socket. */
	private Socket clientSocket;
	
	/** Whether this client has joined the most recent UDP group request. */
	private boolean joinedUDPGroup = false;
	
	/** The ID assigned to this client. */
	private int clientID;
	
	private static int lastClientIDAssigned = 0;
	
	public ClientInfo(Socket clientSocket) {
		this.clientID = ++lastClientIDAssigned;
		this.clientSocket = clientSocket;
		this.ipAddress = clientSocket.getInetAddress();
		this.port = clientSocket.getPort();
		this.nickname = "Unknown Client"; //default nickname
	}
	
	public ClientInfo(InetAddress ipAddress, int port) {
		this.clientID = ++lastClientIDAssigned;
		this.ipAddress = ipAddress;
		this.port = port;
		this.nickname = "Unknown Client"; //default nickname
	}

	/**
	 * Should be called when the server receives a message from this client. <br>
	 * This updates the time since this client received a message for the purpose of timing out clients.
	 */
	public void recivedMessage() {
		recivedMessageTime = System.currentTimeMillis();
	}
	
	/**
	 * Set this clients nickname.
	 * @param nickname the clients nickname
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
	/**
	 * @return the clients nickname
	 */
	public String getNickname() {
		return nickname;
	}

	/**
	 * @return the clients IP address
	 */
	public InetAddress getIpAddress() {
		return ipAddress;
	}

	/**
	 * @return the clients port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @return the time the client last received a message in milliseconds
	 */
	public long getRecivedMessageTime() {
		return recivedMessageTime;
	}
	
	public Socket getSocket() {
		return clientSocket;
	}
	
	public boolean hasJoinedUDPGroup() {
		return joinedUDPGroup;
	}
	
	public void setHasJoinedUDPGroup(boolean toSet) {
		joinedUDPGroup = toSet;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClientInfo) {
			ClientInfo toCheck = ((ClientInfo)obj);
			if (toCheck.getIpAddress().equals(ipAddress) && toCheck.getPort() == port)
				return true;
		}

		return false;
	}

	public int getClientID() {
		return clientID;
	}

}
