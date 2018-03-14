package networking.client;

import java.net.InetAddress;

/**
 * A client listener receives events related to clients.
 * @author Connor Stewart
 */
public interface ClientListener {
	
	/**
	 * A message received from the the server.
	 * @param message
	 * @param inetAddress 
	 */
	public void messageReceived(String message, InetAddress inetAddress);

}
