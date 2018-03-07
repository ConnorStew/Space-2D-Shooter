package networking.server;

import java.net.InetAddress;

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
	
	private boolean confirmedGroupJoin = false;
	
	public ClientInfo(InetAddress inetAddress, int port) {
		this.ipAddress = inetAddress;
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
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ClientInfo) {
			ClientInfo toCheck = ((ClientInfo)obj);
			if (toCheck.getIpAddress().equals(ipAddress) && toCheck.getPort() == port)
				return true;
		}

		return false;
	}

	public boolean isConfirmedGroupJoin() {
		return confirmedGroupJoin;
	}

	public void setConfirmedGroupJoin(boolean confirmedGroupJoin) {
		this.confirmedGroupJoin = confirmedGroupJoin;
	}

}
