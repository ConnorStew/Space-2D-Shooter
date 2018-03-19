package network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

import network.Network;
import network.Network.AddRoom;
import network.Network.RefreshRooms;
import network.Network.RoomUpdate;
import network.Network.StartGame;
import network.Network.JoinRoom;
import network.Network.UpdateNickname;
import network.server.DiscoveryThread;
import ui.MPGame;
import ui.MultiplayerScreen;
import ui.UI;

public class ClientHandler {
	
	private final Client client = new Client();
	
	public ClientHandler(final String nickname) {
		client.start();
		
		Network.register(client);
		
		client.addListener(new ThreadedListener(new Listener(){
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof RoomUpdate) {
					RoomUpdate msg = (RoomUpdate) object;
					MultiplayerScreen.populateRooms(msg.roomNames, msg.requiredPlayers);
				}
				if (object instanceof StartGame) {
					Gdx.app.postRunnable(new Runnable() {
						public void run() {
							UI.getInstance().setScreen(new MPGame(client, nickname));
						}
					});
				}
			}
		}));
		
		try {
			client.connect(30000000, client.discoverHost(Network.UDP_PORT, 3000000), Network.TCP_PORT, Network.UDP_PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		UpdateNickname toSend = new UpdateNickname();
		toSend.nickname = nickname;
		client.sendTCP(toSend);
	}

	public void addRoom(String roomName, String roomNum) {
		AddRoom toSend = new AddRoom();
		toSend.roomName = roomName;
		toSend.requiredPlayers = roomNum;
		client.sendTCP(toSend);
		refreshRooms();
	}

	public void refreshRooms() {
		client.sendTCP(new RefreshRooms());
	}

	public void joinRoom(String selected) {
		JoinRoom toSend = new JoinRoom();
		toSend.roomName = selected;
		client.sendTCP(toSend);
	}
	
	/**
	 * Pings all available broadcast addresses to find the server and request to connect.
	 * @return 
	 */
	private String pingServerForConnection() {
		try {
			DatagramSocket socket = new DatagramSocket();
			socket.setBroadcast(true);
			byte[] sendData = "DR".getBytes();
			
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
					
					DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, DiscoveryThread.DISCOVERY_PORT);
					socket.send(sendPacket);
					
					System.out.println(getClass().getSimpleName() + " >>> Request packet sent to " + broadcast.getHostAddress() + ", Interface: "
							+ networkInterface.getDisplayName() + ".");
				}
				
			}
			
			byte[] buffer = new byte[256];
			socket.receive(new DatagramPacket(buffer, buffer.length));
			
			socket.close();
			
			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "localhost";
	}

}
