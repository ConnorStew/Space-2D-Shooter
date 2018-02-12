package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class represents the server that connects clients.
 * @author Connor Stewart
 */
public class Server {
	
	/** An ArrayList of rooms that can be modified concurrently. */
	static CopyOnWriteArrayList<Room> rooms = new CopyOnWriteArrayList<Room>();
	
	/** The port the server runs on. */
	public final static int port = 3854;
	
	/** The server that accepts connections from sockets. */
	static ServerSocket server;

	public Server() {
		try {
			server = new ServerSocket(port, 0, InetAddress.getLocalHost());
			
			//sends UDP packets out to let clients get this servers IP address
			new DiscoveryThread().start();
			
			//listen for new clients attempting to connect to the server
			new ServerListener().start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			//this catches BindException, this just means that the server is already running.
			System.out.println(Server.class.getName() + ">>>Server already running.");
		}
		
	}

}
