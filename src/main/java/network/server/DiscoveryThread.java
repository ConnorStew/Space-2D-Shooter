package network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * This thread listens for UDP requests, then responds with the IP of the server.
 * @author Connor Stewart
 */
public class DiscoveryThread extends Thread {
	
	private InetAddress serverIP;
	
	public final static int DISCOVERY_PORT = 4395;
	
	public DiscoveryThread(InetAddress serverIP) {
		this.serverIP = serverIP;
	}

	@Override
	public void run() {
		DatagramSocket socket = null;
		
		try {
			socket = new DatagramSocket(DISCOVERY_PORT, InetAddress.getLocalHost());
			socket.setBroadcast(true);
			
			System.out.println(getClass().getSimpleName() + " >>> Ready to receive broadcast packets!");
			
			while (true) {
				byte[] buffer = new byte[256];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				
				//wait to receive a packet
				socket.receive(packet);
				
				//a packet has been received
				System.out.println(getClass().getSimpleName() + " >>> Recived '" + new String(buffer).trim() + "' from: " + packet.getAddress().getHostAddress().trim());
				
				//check that the packet is looking to join the server
				String message = new String(packet.getData()).trim();
				if (message.equals("DR")) {
					//send the servers IP to the client
					byte[] sendData = serverIP.getHostAddress().getBytes();
					
					//send the data to the client
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					socket.send(sendPacket);
					
					System.out.println(getClass().getSimpleName() + " >>> Sent '" + new String(sendData) + "' to: " + packet.getAddress().getHostAddress());
				}
				
				
			}
			
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		socket.close();
	}

}