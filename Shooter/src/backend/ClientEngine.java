package backend;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import backend.entities.Entity;
import backend.entities.MultiplayerPlayer;
import networking.NetworkUtils;
import networking.client.Client;
import networking.client.ClientListener;

/**
 * This engine takes data from the server.
 * @author Connor Stewart
 */
public class ClientEngine extends Engine implements ClientListener {

	private Client thisClient;
	private MultiplayerPlayer player;

	public ClientEngine(Client thisClient) {
		System.out.println(getClass().getName() + ">>>Client engine started!");
		this.thisClient = thisClient;
		
		//start listening for messages from the server
		thisClient.addListener(this);
	}
	
	@Override
	public void messageReceived(String message) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		if (command.equals("ADDPLAYER")) {
			String playerName = arguments[0];
			System.out.println(getClass().getName() + ">>>Request to add player " + playerName);
			
			ClientEngine thisEngine = this;
			
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					MultiplayerPlayer toAdd = new MultiplayerPlayer(50, 50, thisEngine, playerName);
					activeEntities.add(toAdd);
					if (toAdd.getPlayerName().equals(thisClient.getNickname()))
						setPlayer(toAdd);
				}
			});
			
		}
		
		if (command.equals("MOVE")) {
			MultiplayerPlayer toMove = null;
			
			for (Entity entity : activeEntities) {
				if (entity instanceof MultiplayerPlayer) {
					if (((MultiplayerPlayer) entity).getPlayerName().equals(arguments[0])) {
						toMove = (MultiplayerPlayer) entity;
						
						switch (arguments[1]) {
						case "UP":
							toMove.moveUp(Gdx.graphics.getDeltaTime());
							break;
						case "DOWN":
							toMove.moveDown(Gdx.graphics.getDeltaTime());
							break;
						case "RIGHT":
							toMove.moveRight(Gdx.graphics.getDeltaTime());
							break;
						case "LEFT":
							toMove.moveLeft(Gdx.graphics.getDeltaTime());
							break;
						}
						
					}
				}
			}
		}
		/*
		if (command.equals("SHOOTL")) {
			for (Entity entity : activeEntities) {
				if (entity instanceof MultiplayerPlayer) {
					if (((MultiplayerPlayer) entity).getPlayerName().equals(arguments[0])) {
						MultiplayerPlayer toFire = (MultiplayerPlayer) entity;
						Projectile projectile = toFire.fire(Gdx.graphics.getDeltaTime(), "Light");
						if (toFire != null)
							activeEntities.add(projectile);
					}	
				}
			}
		}
		
		if (command.equals("SHOOTR")) {
			for (Entity entity : activeEntities) {
				if (entity instanceof MultiplayerPlayer) {
					if (((MultiplayerPlayer) entity).getPlayerName().equals(arguments[0])) {
						MultiplayerPlayer toFire = (MultiplayerPlayer) entity;
						Projectile projectile = toFire.fire(Gdx.graphics.getDeltaTime(), "Heavy");
						if (toFire != null)
							activeEntities.add(projectile);
					}	
				}
			}
		}
		*/
		
	}

	public void setPlayer(MultiplayerPlayer player) {
		this.player = player;
	}
	
	public MultiplayerPlayer getPlayer() {
		return player;
	}

	@Override
	public void update(float delta) {
		//poll for user input
		checkInput();
		
		//move entities
		for (Entity entity : activeEntities)
			entity.update(delta);
	}

	/**
	 * Send player key presses to the server.
	 */
	private void checkInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			thisClient.sendMessage("W_PRESS");
			
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			thisClient.sendMessage("S_PRESS");
			
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			thisClient.sendMessage("R_PRESS");
			
		if (Gdx.input.isKeyPressed(Input.Keys.A)) 
			thisClient.sendMessage("L_PRESS");
			
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			thisClient.sendMessage("LMB");
			
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
			thisClient.sendMessage("RMB");
	}


	@Override
	public int getScore() {
		return 0;
	}

	@Override
	public CopyOnWriteArrayList<Entity> getActiveEntities() {
		return activeEntities;
	}

	@Override
	public void addToScore(int pOINTS) {
		
	}

}
