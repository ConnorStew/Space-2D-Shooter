package networking.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArrayList;

import org.mockito.Mockito;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;

import backend.entities.MultiplayerPlayer;
import backend.projectiles.Projectile;
import backend.projectiles.ProjectileType;
import networking.NetworkUtils;
import networking.client.Client;

/**
 * This thread hosts a server side game game once a room of players has been assembled.
 * @author Connor Stewart
 */
public class ServerGame implements ApplicationListener, ServerListener {
	
	/** The width of the game. */
	public static final float GAME_WIDTH = 100;

	/** The height of the game. */
	public static final float GAME_HEIGHT = 100;
	
	/** The room containing clients playing this game. */
	private final Room room;
	
	/** The last multiplayer ID assigned to an entity. */
	private int lastIDAssigned;
	
	/** Players that are currently active in the game. */
	private CopyOnWriteArrayList<MultiplayerPlayer> players  = new CopyOnWriteArrayList<MultiplayerPlayer>();
	
	/** Projectiles that are currently active in the game. */
	private CopyOnWriteArrayList<Projectile> projectiles  = new CopyOnWriteArrayList<Projectile>();
	
	/** The socket that receives messages from the clients. */
	private DatagramSocket udpSocket;
	
	private final int udpPort = 8645;

		
	public ServerGame(Room toHost, InetAddress multicastGroup, InetAddress commandGroup) {
		Server.getInstance().addListener(this);
		
		room = toHost;
		lastIDAssigned = 0;
		
		try {
			udpSocket = new DatagramSocket(udpPort, InetAddress.getLocalHost());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Gdx.gl = Mockito.mock(GL20.class);
		
		new HeadlessApplication(this);
		
		Thread udpMessageListener = new Thread() {
			@Override
			public void run() {
				while (Server.isRunning()) {
					byte[] buffer = new byte[256];
					
					DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
					String message = null;
					
					try {
						udpSocket.receive(dp);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					message = new String(dp.getData());
					
					System.out.println(ServerGame.class.getSimpleName() + " >>> Recieved UDP message " + message.trim() + " from client: " + dp.getAddress().getHostAddress().trim() + " on port " + dp.getPort());
					
					String[] arguments = message.split("#");
					
					ClientInfo info = null;
					
					//get the client from the list of clients
					for (ClientInfo client : Server.getClients())
						if (client.getClientID() == Integer.parseInt(arguments[0]))
							info = client;

					messageReceived(info, arguments[1]);
				}
			}
		};
		
		udpMessageListener.setName(toHost.getRoomName() + "'s UDP Message Listener");
		udpMessageListener.start();
		
		System.out.println(getClass().getSimpleName() + " >>> Listening for UDP packets on " + udpSocket.getLocalAddress() + ", port:" + udpSocket.getLocalPort());
	}
	
	@Override
	public void create() {
		//tell the clients to open their game screens
		for (ClientInfo client : room.getClients())
			Server.getInstance().sendMessageToSingleClient("<SG/" + udpSocket.getLocalAddress().getHostAddress() + "/" + udpPort +  ">", client);

		//tell the clients to add the player characters to the game
		for (ClientInfo client : room.getClients()) {
			lastIDAssigned++;
			
			//tell all players to add this clients character
			for (int i = 0; i < room.getClients().size(); i++) {
				Server.getInstance().sendMessageToSingleClient("<ADDPLAYER/" + client.getNickname() + "/" + lastIDAssigned + ">", room.getClients().get(i));
			}
			
			players.add(new MultiplayerPlayer(GAME_HEIGHT / 2, GAME_HEIGHT / 2, client.getNickname(), lastIDAssigned));
		}

	}
	
	@Override
	public void messageReceived(ClientInfo client, String message) {
		message = message.trim();
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		//if the client is on the server load their info
		if (room.getClients().contains(client))
			client = room.getClients().get(room.getClients().indexOf(client));
		
		if (command.equals("W_PRESS")) {
			System.out.println(getClass().getSimpleName() + " >>> " + client.getNickname() + " has pressed the 'w' key.");

			getPlayerByClient(client).moveUp(Gdx.graphics.getDeltaTime());
		}
		
		if (command.equals("S_PRESS")) {
			System.out.println(getClass().getSimpleName() + " >>> " + client.getNickname() + " has pressed the 's' key.");

			getPlayerByClient(client).moveDown(Gdx.graphics.getDeltaTime());
		}
		
		if (command.equals("R_PRESS")) {
			System.out.println(getClass().getSimpleName() + " >>> " + client.getNickname() + " has pressed the 'r' key.");
			
			getPlayerByClient(client).moveRight(Gdx.graphics.getDeltaTime());
		}
		
		if (command.equals("L_PRESS")) {
			System.out.println(getClass().getSimpleName() + " >>> " + client.getNickname() + " has pressed the 'l' key.");
			
			getPlayerByClient(client).moveLeft(Gdx.graphics.getDeltaTime());
		}
		
		//a client has moved their mouse
		if (command.equals("MM")) {
			MultiplayerPlayer toUpdate = getPlayerByClient(client);
			float x = Float.parseFloat(arguments[0]);
			float y = Float.parseFloat(arguments[1]);
			
			//rotate the player towards the mouse
			toUpdate.rotateTowards(x, y);
			toUpdate.setRotation(toUpdate.getRotation() - 90); //-90 due to how the player sprite is drawn
		}
		
		if (command.equals("LMB")) {
			MultiplayerPlayer player = getPlayerByClient(client);
			
			if (player.canFireLight()) {
				lastIDAssigned++;
				String projectileType = "Light";
				sendMessageToGroup("<ADDPROJECTILE/" + player.getMultiplayerID() + "/" + lastIDAssigned + "/"  + projectileType + ">");
				projectiles.add(player.fire(Gdx.graphics.getDeltaTime(), projectileType, ProjectileType.PVP, lastIDAssigned));
			}
			
		}
		
		if (command.equals("RMB")) {
			MultiplayerPlayer player = getPlayerByClient(client);
			
			if (player.canFireHeavy()) {
				lastIDAssigned++;
				String projectileType = "Heavy";
				sendMessageToGroup("<ADDPROJECTILE/" + player.getMultiplayerID() + "/" + lastIDAssigned + "/"  + projectileType + ">");
				projectiles.add(player.fire(Gdx.graphics.getDeltaTime(), projectileType, ProjectileType.PVP, lastIDAssigned));
			}
			
		}
		
		if (command.equals("DC")) {
			removePlayer(getPlayerByClient(client));
			room.getClients().remove(client);
			
			if (room.getClients().isEmpty())
				Gdx.app.exit();
		}
		
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		
		//update players
		for (MultiplayerPlayer player : players)
			player.update(delta);
			
		
		//update projectile
		for (Projectile projectile : projectiles) {
			projectile.update(delta);
			
			if (projectile.getX() > GAME_WIDTH || projectile.getX() < 0 || projectile.getY() > GAME_HEIGHT || projectile.getY() < 0)
				removeProjectile(projectile);
		}
			

		//check for collisions between players and projectiles
		for (MultiplayerPlayer player : players) {
			for (Projectile projectile : projectiles) {
				if (projectile.getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
					if (projectile.getFiredByID() != player.getMultiplayerID()) {
						if (projectile.onCollision(player)) {
							removeProjectile(projectile);
						}
						
						if (player.onCollision(projectile)) {
							getPlayerByID(projectile.getFiredByID()).incrementKills();
							player.resetHealth();
							player.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 2);
						}
					}
				}
			}
		}
		
		//send the player update command to the clients
		for (MultiplayerPlayer player : players)
			sendMessageToGroup("<UPDATE"
					+ "/" + "PLAYER"
					+ "/" + player.getMultiplayerID() 
					+ "/" + player.getX()
					+ "/" + player.getY()
					+ "/" + Math.floor(player.getRotation())
					+ "/" + player.getHealth()
					+ "/" + player.getKills() + ">");
		
		//send the projectile update command to the clients
		for (Projectile projectile : projectiles)
			sendMessageToGroup("<UPDATE"
					+ "/" + "PROJECTILE"
					+ "/" + projectile.getMultiplayerID() 
					+ "/" + projectile.getX()
					+ "/" + projectile.getY()
					+ "/" + Math.floor(projectile.getRotation()) + ">");
	}

	@Override
	public void pause() {}

	@Override
	public void dispose() {}
	
	@Override
	public void resume() {}

	/**
	 * Gets a MultiplayerPlayer using a client.
	 * @param client the client
	 * @return the player that has a matching client or null
	 */
	private MultiplayerPlayer getPlayerByClient(ClientInfo client) {
		for (MultiplayerPlayer player : players)
			if (player.getPlayerName().equals(client.getNickname()))
				return player;
		
		return null;
	}
	

	
	private void removeProjectile(Projectile toRemove) {
		sendMessageToGroup("<REMOVEPROJECTILE/" + toRemove.getMultiplayerID() + ">");
		toRemove.onDestroy();
		projectiles.remove(toRemove);
	}
	
	private void removePlayer(MultiplayerPlayer toRemove) {
		sendMessageToGroup("<REMOVEPLAYER/" + toRemove.getMultiplayerID() + ">");
		players.remove(toRemove);
	}
	
	/**
	 * Gets a MultiplayerPlayer using its multiplayer id.
	 * @param id the multiplayer id to search for
	 * @return the player that has a matching id or null
	 */
	private MultiplayerPlayer getPlayerByID(int id) {
		for (MultiplayerPlayer player : players)
			if (player.getMultiplayerID() == id)
				return player;
		
		return null;
	}

	/**
	 * Sends a message to all clients.
	 * @param message the message to send
	 * @param group the multicast group to send the message to
	 */
	private void sendMessageToGroup(String message) {
		byte[] buffer = message.getBytes();
		
		

		for (ClientInfo client : room.getClients()) {
			try {
				System.out.println(getClass().getSimpleName() + " >>> Sending " + message + " to client with ip "+ client.getIpAddress() + " on port " + Client.UDP_PORT);
				udpSocket.send(new DatagramPacket(buffer, buffer.length, client.getIpAddress(), Client.UDP_PORT));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void clientDisconnected(ClientInfo info) {
		
	}

}
