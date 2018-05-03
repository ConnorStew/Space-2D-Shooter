package ui;

import backend.entities.InanimateEntity;
import backend.entities.MultiplayerPlayer;
import backend.projectiles.Projectile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;
import network.Network;
import network.Network.*;

import javax.swing.*;

/**
 * This class handles displaying a multiplayer game for the client.
 */
public class MPGame extends GameScreen {
	
	/** Players that are currently active in the game. */
	private Array<Projectile> projectiles  = new Array<Projectile>();

	/** Players that are currently active in the game. */
	private Array<MultiplayerPlayer> players  = new Array<MultiplayerPlayer>();

	/** This clients player. */
	private MultiplayerPlayer player;
	
	/** The last mouse position that was sent to the server. */
	private Vector3 oldPos;
	
	/** The client connected to the server. */
	private Client client;

	/** This clients nickname. */
	private String clientNickname;
	
	public MPGame(Client client, String nickname) {
		this.client = client;
		this.clientNickname = nickname;
		
		client.addListener(new ThreadedListener(new Listener(){
			@Override
			public void received(Connection connection, Object object) {
				if (object instanceof AddPlayer) {
					final AddPlayer msg = (AddPlayer) object;
					Gdx.app.postRunnable(() -> {
						MultiplayerPlayer toAdd = new MultiplayerPlayer(Network.GAME_HEIGHT / 2, Network.GAME_HEIGHT / 2, msg.name);
						toAdd.setMultiplayerID(msg.id);
						players.add(toAdd);

						System.out.println("Adding player: " + player.getPlayerName());

						if (toAdd.getPlayerName().equals(clientNickname))
							player = toAdd;
					});
				}
				if (object instanceof AddProjectile) {
					final AddProjectile msg = (AddProjectile) object;
					Gdx.app.postRunnable(() -> {
						Projectile toAdd = null;
						MultiplayerPlayer player = getPlayerByID(msg.playerID);
						if (player != null) {
							if (msg.type.equals("Light")) {
								toAdd = player.getLeftWeapon().fireWithoutValidation(player.getCenterX(), player.getCenterY(), player.getRotation());
							} else {
								toAdd = player.getRightWeapon().fireWithoutValidation(player.getCenterX(), player.getCenterY(), player.getRotation());
							}
						}

						if (toAdd != null) {
							toAdd.setFiredByID(msg.playerID);
							toAdd.setMultiplayerID(msg.id);
							projectiles.add(toAdd);
						}

					});
				}
				if (object instanceof UpdatePlayer) {
					UpdatePlayer msg = (UpdatePlayer) object;
					MultiplayerPlayer toUpdate = getPlayerByID(msg.id);
					if (toUpdate != null) {
						toUpdate.setX(msg.x);
						toUpdate.setY(msg.y);
						toUpdate.setRotation((float) msg.r);
						toUpdate.setHealth(msg.health);
						toUpdate.setKills(msg.kills);
					}
				}
				if (object instanceof UpdateProjectile) {
					UpdateProjectile msg = (UpdateProjectile) object;
					Projectile toUpdate = getProjectileByID(msg.id);
					if (toUpdate != null) {
						toUpdate.setX(msg.x);
						toUpdate.setY(msg.y);
						toUpdate.setRotation((float) msg.r);
					}
				}
				if (object instanceof RemoveProjectile) {
					RemoveProjectile msg = (RemoveProjectile) object;
					Projectile toRemove = getProjectileByID(msg.id);
					projectiles.removeValue(toRemove, false);
				}
				if (object instanceof RemovePlayer) {
					MultiplayerPlayer toRemove = getPlayerByID(((RemovePlayer) object).id);
					players.removeValue(toRemove, false);
				}
				if (object instanceof PlayerWon) {
					MultiplayerPlayer winningPlayer = getPlayerByID(((PlayerWon) object).id);
					if (winningPlayer != null)
						win(winningPlayer);
				}
			}
		}));
	}

	public void show() {
		super.show();
		System.out.println(getClass().getSimpleName() + " >>> Multiplayer game started!");
		
		//instantiate map
		map = new InanimateEntity("backgrounds/redPlanet.png", Network.GAME_WIDTH, Network.GAME_HEIGHT);
		player = new MultiplayerPlayer(Network.GAME_WIDTH / 2, Network.GAME_HEIGHT / 2, "default");
	}

	public void render(float delta) {
		super.render(delta);

		checkInput();
		
		//get the player name coordinates according to the current camera position
		Vector3 nameCord = new Vector3(player.getCenterX(), player.getCenterY(), 0);
		cam.unproject(nameCord);

		//get the score coordinates according to the current camera position
		Vector3 scoreCord = new Vector3(10, 10, 0);
		cam.unproject(scoreCord);
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);

		//tell the server that you have moved your mouse
		if (mousePos != oldPos) {
			oldPos = mousePos;
			MouseMoved toSend = new MouseMoved();
			toSend.id = player.getMultiplayerID();
			toSend.x = mousePos.x;
			toSend.y = mousePos.y;
			client.sendUDP(toSend);
		}
		
		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);

		//validate camera movement
		if (player.getCenterY() - cam.viewportHeight > 0 && player.getCenterY() + cam.viewportHeight < map.getHeight())
			cam.position.y = player.getCenterY();
		
		if (player.getCenterX() - cam.viewportWidth > 0 && player.getCenterX() + cam.viewportWidth < map.getWidth())
			cam.position.x = player.getCenterX();
		
		//validate the all projectiles are still moving
		for (Projectile projectile : projectiles)
			if (projectile.isDead(delta))
				projectiles.removeValue(projectile, false);
		
		//start drawing sprites
		batch.begin(); 
		
		//draw background
		map.draw(batch);
		
		int yIncrease = 5;
		font.setUseIntegerPositions(false);
		
		for (int i = 0; i < players.size; i++) {
			//draw the players name
			font.draw(batch, players.get(i).getPlayerName(), players.get(i).getCenterX(), players.get(i).getCenterY());
			//draw the players scores
			font.draw(batch, players.get(i).getPlayerName() + ": " + players.get(i).getKills(), scoreCord.x, scoreCord.y - (i * yIncrease));
		}

		//draw players
		for (MultiplayerPlayer player : players) {
			player.draw(batch);
		}
			
		//draw projectiles
		for (Projectile projectile : projectiles)
			projectile.draw(batch);
		
		//stop drawing sprites
		batch.end();
		
		//start drawing shapes
		sr.begin(ShapeRenderer.ShapeType.Filled);
		
		//draw health bars
		for (MultiplayerPlayer player : players)
			if (player.hasHealth())
				player.drawHP(sr, cam);

		//stop drawing shapes
		sr.end();
	}

	private void win(MultiplayerPlayer player) {
		Gdx.app.postRunnable(() -> ControlGame.getInstance().setScreen(new MenuScreen()));

		client.close();
		dispose();

		JOptionPane.showMessageDialog(null, player.getPlayerName() + " has won!", "Winner", JOptionPane.INFORMATION_MESSAGE);
	}

	public void resize(int width, int height) {}

	public void pause() {}

	public void resume() {}

	public void hide() {}

	public void dispose() {}
	
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
	 * Gets a Projectile using its multiplayer id.
	 * @param id the multiplayer id to search for
	 * @return the projectile that has a matching id or null
	 */
	private Projectile getProjectileByID(int id) {
		for (Projectile projectile : projectiles)
			if (projectile.getMultiplayerID() == id)
				return projectile;
		
		return null;
	}
	
	/**
	 * Send player key presses to the server.
	 */
	private void checkInput() {
		checkKeyInput(Input.Keys.W);
		checkKeyInput(Input.Keys.S);
		checkKeyInput(Input.Keys.D);
		checkKeyInput(Input.Keys.A);

		checkMouseInput(Input.Buttons.LEFT);
		checkMouseInput(Input.Buttons.RIGHT);
	}

	/**
	 * Checks if a key has been pressed and notifies the server if so.
	 * @param keyCode the keyCode to check
	 */
	private void checkKeyInput(int keyCode) {
		if (Gdx.input.isKeyPressed(keyCode)) {
			KeyInput toSend = new KeyInput();
			toSend.id = player.getMultiplayerID();
			toSend.keyCode = keyCode;
			client.sendTCP(toSend);
		}
	}

	/**
	 * Checks if a mouse button has been pressed and notifies the server if so.
	 * @param buttonCode the buttonCode to check
	 */
	private void checkMouseInput(int buttonCode) {
		if (Gdx.input.isButtonPressed(buttonCode)) {
			MouseInput toSend = new MouseInput();
			toSend.id = player.getMultiplayerID();
			toSend.buttonCode = buttonCode;
			client.sendTCP(toSend);
		}
	}
	
}
