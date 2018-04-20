package network.server;

import backend.entities.Entity;
import backend.entities.MultiplayerPlayer;
import backend.projectiles.Projectile;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Listener;
import network.Network;
import network.Network.*;
import org.mockito.Mockito;

/**
 * This thread hosts a server side game game once a room of players has been assembled.
 * @author Connor Stewart
 */
public class ServerGame extends Listener implements ApplicationListener {
	
	/** The room containing clients playing this game. */
	private final Room room;
	
	/** The last multiplayer ID assigned to an entity. */
	private static int lastIDAssigned;

	/** The entities in this game. */
	private Array<Entity> entities = new Array<>();

	/** The time inbetween game updates ticks in seconds. */
	private float tickTime = 1;

	private float tickTimer;

	ServerGame(Room toHost) {
		this.room = toHost;
		
		ServerHandler.getInstance().addListener(this);
		
		Gdx.gl = Mockito.mock(GL20.class);
		
		new HeadlessApplication(this);
	}

	public void message(Object object) {
		if (object instanceof MouseMoved) {
			MouseMoved msg = (MouseMoved) object;
			MultiplayerPlayer toUpdate = getPlayerByID(msg.id);
			if (toUpdate != null) {
				//rotate the player towards the mouse
				toUpdate.rotateTowards(msg.x, msg.y);
				toUpdate.setRotation(toUpdate.getRotation() - 90); //-90 due to how the player sprite is drawn
			}
		}

		if (object instanceof MouseInput) {
			MouseInput msg = (MouseInput) object;
			MultiplayerPlayer toUpdate = getPlayerByID(msg.id);
			if (toUpdate != null) {
				if (msg.buttonCode == Input.Buttons.LEFT) {
					Projectile pp = toUpdate.getLeftWeapon().fire(toUpdate.getCenterX(), toUpdate.getCenterY(), toUpdate.getRotation());
					if (pp != null) {
						lastIDAssigned++;
						String projectileType = "Light";
						AddProjectile toSend = new AddProjectile();
						toSend.playerID = toUpdate.getMultiplayerID();
						toSend.id = lastIDAssigned;
						toSend.type = projectileType;
						ServerHandler.getInstance().getServer().sendToAllUDP(toSend);

						pp.setFiredByID(toUpdate.getMultiplayerID());
						pp.setMultiplayerID(lastIDAssigned);
						entities.add(pp);
					}
				}
				if (msg.buttonCode == Input.Buttons.RIGHT) {
					Projectile pp = toUpdate.getRightWeapon().fire(toUpdate.getCenterX(), toUpdate.getCenterY(), toUpdate.getRotation());
					if (pp != null) {
						lastIDAssigned++;
						String projectileType = "Heavy";

						AddProjectile toSend = new AddProjectile();
						toSend.playerID = toUpdate.getMultiplayerID();
						toSend.id = lastIDAssigned;
						toSend.type = projectileType;
						ServerHandler.getInstance().getServer().sendToAllUDP(toSend);

						pp.setFiredByID(toUpdate.getMultiplayerID());
						pp.setMultiplayerID(lastIDAssigned);
						entities.add(pp);
					}
				}
			}
		}

		if (object instanceof KeyInput) {
			KeyInput key = (KeyInput) object;
			MultiplayerPlayer toUpdate = getPlayerByID(key.id);

			if (toUpdate != null) {
				if (key.keyCode == Input.Keys.W)
					toUpdate.moveUp(Gdx.graphics.getDeltaTime());

				if (key.keyCode == Input.Keys.S)
					toUpdate.moveDown(Gdx.graphics.getDeltaTime());

				if (key.keyCode == Input.Keys.D)
					toUpdate.moveRight(Gdx.graphics.getDeltaTime());

				if (key.keyCode == Input.Keys.A)
					toUpdate.moveLeft(Gdx.graphics.getDeltaTime());
			}
		}
	}

	public void create() {
		//tell the clients to open their game screens
		for (ClientInfo client : room.getClients())
			client.getConnection().sendTCP(new StartGame());
		
		try {
			Thread.sleep(500L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//tell the clients to add the player characters to the game
		for (int i = 0; i < room.getClients().size ; i++) {
			lastIDAssigned++;

			room.getClients().get(i).setMultiplayerID(lastIDAssigned);

			AddPlayer toSend = new AddPlayer();
			toSend.id = lastIDAssigned;
			toSend.name = room.getClients().get(i).getNickname();
			ServerHandler.getInstance().sendTCPTo(room.getClients(), toSend);

			MultiplayerPlayer toAdd = new MultiplayerPlayer(Network.GAME_HEIGHT / 2, Network.GAME_HEIGHT / 2, room.getClients().get(i).getNickname());
			toAdd.setMultiplayerID(lastIDAssigned);
			entities.add(toAdd);
		}
	}
	
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();

		for (int i = 0 ; i < entities.size; i++) {
			Entity currentEntity = entities.get(i);
			currentEntity.update(delta);
			if (currentEntity instanceof Projectile) {
				Projectile projectile = (Projectile) currentEntity;
				
				//send the projectile update command to the clients
				UpdateProjectile toSend = new UpdateProjectile();
				toSend.id = projectile.getMultiplayerID();
				toSend.x = projectile.getX();
				toSend.y = projectile.getY();
				toSend.r = Math.floor(projectile.getRotation());
				ServerHandler.getInstance().getServer().sendToAllUDP(toSend);
				
				//remove the projectile if its outside the map
				if (projectile.getX() > Network.GAME_WIDTH || projectile.getX() < 0 || projectile.getY() > Network.GAME_HEIGHT || projectile.getY() < 0)
					removeProjectile(projectile);
			} else if (currentEntity instanceof MultiplayerPlayer) {
				MultiplayerPlayer player = (MultiplayerPlayer) currentEntity;
				
				//send the player update command to the clients
				UpdatePlayer toSend = new UpdatePlayer();
				toSend.id = player.getMultiplayerID();
				toSend.x = player.getX();
				toSend.y = player.getY();
				toSend.r = Math.floor(player.getRotation());
				toSend.health = player.getHealth();
				toSend.kills = player.getKills();
				ServerHandler.getInstance().getServer().sendToAllUDP(toSend);
			}
			
			//check if this entity collides with any others
			for (int j = 0; j < entities.size; j++)
				if (currentEntity.getBoundingRectangle().overlaps(entities.get(j).getBoundingRectangle()) && !currentEntity.equals(entities.get(j)))
					resolveCollision(currentEntity, entities.get(j));
		}

		tickTimer =+ delta;

		if (delta >= tickTimer)
			tick();
	}

	private void tick() {
		tickTime = 0;

		if (room.getClients().size <= 1) {
			ClientInfo lastClient = room.getClients().get(0);
			if (lastClient != null) {
				sendWin(lastClient);
				ServerHandler.getInstance().endGame(this);
			}
		}

		for (ClientInfo client : room.getClients()) {
			if (getPlayerByID(client.getID()).getKills() >= 10) {
				sendWin(client);
				ServerHandler.getInstance().endGame(this);
			}
		}
	}

	private void sendWin(ClientInfo lastClient) {
		PlayerWon toSend = new PlayerWon();
		toSend.id = lastClient.getID();
		ServerHandler.getInstance().sendTCPTo(room.getClients(), toSend);
	}



	/**
	 * Resolves a collision between two entities.
	 * @param entity1 the first entity in the collision
	 * @param entity2 the second entity in the collision
	 */
	private void resolveCollision(Entity entity1, Entity entity2) {
		if (entity1 instanceof MultiplayerPlayer) {
			if (entity2 instanceof Projectile) {
				Projectile projectile = (Projectile) entity2;
				MultiplayerPlayer player = (MultiplayerPlayer) entity1;
				
				//if the projectile was fired by another player
				if (projectile.getFiredByID() != player.getMultiplayerID()) {
					player.reduceHealth(projectile.getDamage());
					entities.removeValue(projectile, false);
					
					if (player.getHealth() <= 0) {
						MultiplayerPlayer toIncrement = getPlayerByID(projectile.getFiredByID());

						if (toIncrement != null)
							toIncrement.incrementKills();

						player.resetHealth();
						player.setPosition(Network.GAME_WIDTH / 2, Network.GAME_HEIGHT / 2);
					}
				}
			}
		}
	}
	
	/**
	 * Gets a MultiplayerPlayer using its multiplayer id.
	 * @param id the multiplayer id to search for
	 * @return the player that has a matching id or null
	 */
	private MultiplayerPlayer getPlayerByID(int id) {
		for (Entity entity : entities)
			if (entity instanceof MultiplayerPlayer)
				if (entity.getMultiplayerID() == id)
					return (MultiplayerPlayer) entity;
		
		return null;
	}
	
	/**
	 * Tells all clients to remove a projectile and queue it for removal from the server.
	 * @param toRemove the projectile to remove
	 */
	private void removeProjectile(Projectile toRemove) {
		//remove on clients
		RemoveProjectile toSend = new RemoveProjectile();
		toSend.id = toRemove.getMultiplayerID();
		ServerHandler.getInstance().getServer().sendToAllUDP(toSend);
		
		//remove on server
		toRemove.onDestroy();
		entities.removeValue(toRemove, false);
	}

	public void resize(int width, int height) {}
	public void pause() {}
	public void resume() {}
	public void dispose() {}

	Room getRoom() {
		return room;
	}

	public void removePlayer(ClientInfo client) {
		MultiplayerPlayer toRemove = getPlayerByID(client.getID());

		RemovePlayer toSend = new RemovePlayer();
		toSend.id = toRemove.getMultiplayerID();
		ServerHandler.getInstance().sendTCPTo(room.getClients(), toSend);
	}
}