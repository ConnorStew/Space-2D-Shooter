package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Enumeration;

import server.Server;
import ui.MultiplayerScreen;

/**
 * This class manages the clients connection to the server.
 * @author Connor Stewart
 */
public class ClientConnection {
	
	/** The socket that connects to the server. */
	private Socket socket;
	
	/** The clients output to the server. */
	private DataOutputStream out;
	
	/** The clients input from the server. */
	private DataInputStream in;
	
	public ClientConnection(String nickname) {
		try {
			//connect to the server
			socket = new Socket(getServerIP(), Server.port);
			
			System.out.println(getClass().getName() + ">>>Connected to the server.");
			
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			
			//send nickname through when connected
			out.writeUTF(nickname);
			System.out.println(getClass().getName() + ">>>Sent nickname.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Refresh the rooms on the UI
	 */
	public void refreshRooms() {
		
		ArrayList<String> roomNames = new ArrayList<String>();
		System.out.println(getClass().getName() + ">>>Refrehsing Rooms.");
		
		try {
			out.writeUTF("ROOMS");
			String message = "";
				
			//wait for a list of rooms
			while (!message.equals("end")) {
				message = in.readUTF();
				
				if (!message.equals("end"))
					roomNames.add(message);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		MultiplayerScreen.populateRooms(roomNames);
	}
	
	/**
	 * Gets the IP of the server using UDP.
	 * @return the servers IP
	 */
	private String getServerIP() {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			
			byte[] sendData = "DISCOVERY_REQUEST".getBytes();
			
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface networkInterface = interfaces.nextElement();
				
				//skip the interface if its only local or if its down
				if (networkInterface.isLoopback() || !networkInterface.isUp())
					continue;
				
				//loop through all network addresses
				for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
					InetAddress broadcast = interfaceAddress.getBroadcast();
					
					//skip if there is no broadcast
					if (broadcast == null)
						continue;
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, Server.port);
					socket.send(sendPacket);
					
					System.out.println(getClass().getName() + ">>>Request packet sent to " + broadcast.getHostAddress() + ", Interface: "
							+ networkInterface.getDisplayName());
				}
				
			}
			
			System.out.println(getClass().getName() + ">>>Done looping over all network interfaces. Waiting for response from server.");
			
			byte[] buffer = new byte[15000];
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			
			System.out.println(getClass().getName() + ">>>Broadcast response from server: " + packet.getAddress().getHostAddress());

			socket.close();
			return new String(packet.getData());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return "localhost";
	}

	/**
	 * Sends a message to the server to add a new room.
	 * @param roomName the rooms name
	 */
	public void addRoom(String roomName) {
		try {
			out.writeUTF("NEWROOM");
			out.writeUTF(roomName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		refreshRooms();
	}

}
