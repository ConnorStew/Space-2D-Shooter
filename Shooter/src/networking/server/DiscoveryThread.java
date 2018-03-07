package networking.server;

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

	@Override
	public void run() {
		DatagramSocket socket = null;
		
		try {
			socket = new DatagramSocket(Server.SINGLE_PORT, InetAddress.getByName("0.0.0.0"));
			socket.setBroadcast(true);
			
			while (true) {
				System.out.println(getClass().getName() + ">>>Ready to receive broadcast packets!");
				byte[] buffer = new byte[15000];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				
				//wait to receive a packet
				socket.receive(packet);
				
				//a packet has been received
				System.out.println(getClass().getName() + ">>>Recived packet from: " + packet.getAddress().getHostAddress());
				
				//check that the packet is looking to join the server
				String message = new String(packet.getData()).trim();
				if (message.equals("DISCOVERY_REQUEST")) {
					//send the servers IP to the client
					//byte[] sendData = Server.server.getInetAddress().getHostAddress().getBytes();
					
					//send the data to the client
					//DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, packet.getAddress(), packet.getPort());
					//socket.send(sendPacket);
					
					//System.out.println(getClass().getName() + ">>>Sent packet to: " + packet.getAddress().getHostAddress());
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
