package server;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This class listens for new connections to the server.
 * @author Connor Stewart
 */
public class ServerListener extends Thread {
	
	@Override
	public void run() {
		while (true) {
			try {
				System.out.println(getClass().getName() + ">>>Listening for new clients!");
	            
				//wait for a client to request a connection
	            Socket client = Server.server.accept();
	           
	            System.out.println(getClass().getName() + ">>>Client connected on " +  client.getInetAddress().getHostName());
	            
	            DataInputStream in = new DataInputStream(client.getInputStream());
	            
	            //when the client connects, it will send its nickname through
	            String nickname = in.readUTF();
	            
	            System.out.println(getClass().getName() + ">>>Client nickname recived from " +  client.getInetAddress().getHostName() + ": " + nickname);
	               
		        //new thread for a client
		        new ClientConnectionThread(client, nickname).start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
