package networking.client;

/**
 * A client listener receives events related to clients.
 * @author Connor Stewart
 */
public interface ClientListener {
	
	/**
	 * A message received from the the server.
	 * @param message
	 */
	public void messageReceived(String message);

}
