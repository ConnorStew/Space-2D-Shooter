package networking.server;

import networking.client.Client;

/**
 * A server listener receives events related to the server.
 * @author Connor Stewart
 */
public interface ServerListener {
	
	/**
	 * A message has been received from a client.
	 * @param client the client that sent the message
	 * @param message the message sent by the client
	 */
	public void messageReceived(Client client, String message);

}
