package networking.server;

/**
 * A server listener receives events related to the server.
 * @author Connor Stewart
 */
public interface ServerListener {
	
	/**
	 * A message has been received from a client.
	 * @param info the information regarding client that sent the message
	 * @param message the message sent by the client
	 */
	public void messageReceived(ClientInfo info, String message);
	
	/**
	 * A client has been timed out or has disconnected.
	 * @param info the clients information
	 */
	public void clientDisconnected(ClientInfo info);

}
