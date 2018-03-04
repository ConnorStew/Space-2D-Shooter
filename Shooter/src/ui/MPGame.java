package ui;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import backend.entities.Entity;
import backend.entities.InanimateEntity;
import backend.entities.MultiplayerPlayer;
import backend.projectiles.Projectile;
import backend.projectiles.ProjectileType;
import networking.NetworkUtils;
import networking.client.Client;
import networking.client.ClientListener;

public class MPGame implements Screen, ClientListener {
	
	public static final float GAME_WIDTH = 100;

	public static final float GAME_HEIGHT = 100;

	private Client client;
	
	/** Players that are currently active in the game. */
	private CopyOnWriteArrayList<MultiplayerPlayer> players  = new CopyOnWriteArrayList<MultiplayerPlayer>();
	
	/** Projectiles that are currently active in the game. */
	private CopyOnWriteArrayList<Projectile> projectiles  = new CopyOnWriteArrayList<Projectile>();
	
	/** Font used to display score. */
	private BitmapFont font;

	/** Used to render the sprites/entities. */
	private SpriteBatch batch;
	
	/** Shape renderer used to render health bars. */
	private ShapeRenderer sr;
	
	/** The camera to render the game. */
	private OrthographicCamera cam;
	
	/** The background image. */
	private InanimateEntity map;
	
	/** This clients player. */
	private MultiplayerPlayer player;
	
	/** The last rotation that was sent to the server. */
	private float oldRotation;

	/** The last multiplayer ID assigned to an entity. */
	private int lastIDAssigned;
	
	public MPGame(Client client) {
		this.client = client;
		client.addListner(this);
	}

	@Override
	public void show() {
		System.out.println(getClass().getName() + ">>>Multiplayer game started!");
		
		lastIDAssigned = 0;
		
		//instantiate map
		map = new InanimateEntity("redPlanet.png", MPGame.GAME_WIDTH, MPGame.GAME_HEIGHT);
				
		player = new MultiplayerPlayer(MPGame.GAME_WIDTH / 2, MPGame.GAME_HEIGHT / 2, "default");
		
		//instantiate font for the score
		font = new BitmapFont();
		font.getData().setScale(0.2f);
		font.setUseIntegerPositions(false);
		
		//instantiate shape renderer
		sr = new ShapeRenderer();
		
		//instantiate sprite batch
		batch = new SpriteBatch();
		
		//instantiate camera
		cam = new OrthographicCamera(30, 30);
		cam.zoom = 2;
	}

	@Override
	public void render(float delta) {
		checkInput();
		
		//clear the last frame that was rendered
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//get the font coordinates according to the current camera position
		Vector3 fontCord = new Vector3(player.getCenterX(), player.getCenterY(), 0);
		cam.unproject(fontCord);

		//update camera
		cam.update();
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);

		//rotate the player towards the mouse
		player.rotateTowards(mousePos.x, mousePos.y);
		player.setRotation(player.getRotation() - 90); //-90 due to how the player sprite is drawn
			
		if (Math.floor(player.getRotation()) != oldRotation) {
			oldRotation = (float) Math.floor(player.getRotation());
			NetworkUtils.sendMessage("ROTATE/" + Math.floor(player.getRotation()), client.getOutputStream());
		}
		
		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);

		//validate camera movement
		if (player.getCenterY() - cam.viewportHeight > 0 && player.getCenterY() + cam.viewportHeight < map.getHeight())
			cam.position.y = player.getCenterY();
		
		if (player.getCenterX() - cam.viewportWidth > 0 && player.getCenterX() + cam.viewportWidth < map.getWidth())
			cam.position.x = player.getCenterX();
		
		//update players
		for (MultiplayerPlayer player : players)
			player.update(delta);
		
		//update projectile
		for (Projectile projectile : projectiles)
			projectile.update(delta);

		//check for collisions between players and projectiles
		for (MultiplayerPlayer player : players) {
			for (Projectile projectile : projectiles) {
				if (projectile.getBoundingRectangle().overlaps(player.getBoundingRectangle())) {
					//a projectile has collided with the player
					
					//skip this projectile if it collides with the player that fired it
					if (projectile.getFiredBy().equals(player))
						continue;
					
					//if this projectile was fired by this client notify the server
					if (projectile.getFiredBy().getPlayerName().equals(client.getNickname()))
						NetworkUtils.sendMessage("HIT/" + player.getMultiplayerID() + "/" + projectile.getMultiplayerID(), client.getOutputStream());
				}
			}
		}
		
		//start drawing sprites
		batch.begin(); 
		
		//draw background
		map.draw(batch);
		
		for (MultiplayerPlayer player : players) {
			//draw the players name
			font.draw(batch, player.getPlayerName(), player.getCenterX(), player.getCenterY());
		}

		//draw players
		for (MultiplayerPlayer player : players)
			player.draw(batch);
		
		//draw projectiles
		for (Projectile projectile : projectiles)
			projectile.draw(batch);
		
		//stop drawing sprites
		batch.end();
		
		//start drawing shapes
		sr.begin(ShapeRenderer.ShapeType.Filled);
		
		//draw health bars
		for (Entity entity : players)
			if (entity.hasHealth())
				entity.drawHP(sr, cam);

		//stop drawing shapes
		sr.end();
	}
	
	

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}

	@Override
	public void messageReceived(String message) {
		String command = NetworkUtils.parseCommand(message);
		String[] arguments = NetworkUtils.parseArguements(message);
		
		if (command.equals("ADDPLAYER")) {
			String playerName = arguments[0];
			System.out.println(getClass().getName() + ">>>Request to add player " + playerName);
			
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					MultiplayerPlayer toAdd = new MultiplayerPlayer(50, 50, playerName, (++lastIDAssigned));
					players.add(toAdd);
					if (toAdd.getPlayerName().equals(client.getNickname()))
						player = toAdd;
				}
			});
			
		}
		
		if (command.equals("MOVE")) {
			MultiplayerPlayer toMove = null;
			
			for (Entity entity : players) {
				if (isEntityPlayer(entity, arguments[0])) {
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
		
		if (command.equals("ROTATE"))
			for (Entity entity : players)
				if (isEntityPlayer(entity, arguments[0]))
					((MultiplayerPlayer) entity).setRotation(Float.parseFloat(arguments[1]));
		
		if (command.equals("SHOOTL")) {
			String playerNickname = arguments[0];
			String fireID = arguments[1];
			for (MultiplayerPlayer player : players) {
				if (player.getPlayerName().equals(playerNickname)) {
					
					Gdx.app.postRunnable(new Runnable(){
						@Override
						public void run() {
							Projectile toFire = player.fire(Gdx.graphics.getDeltaTime(), "Light", ProjectileType.PVP, Integer.parseInt(fireID));
							projectiles.add(toFire);	
						}
					});
					
				}
			}
		}
		
		if (command.equals("RESOLVE_COLLISION")) {
			int playerID = Integer.parseInt(arguments[0]);
			int projectileID = Integer.parseInt(arguments[1]);
			Projectile collidedWith = getProjectileByID(projectileID);
			MultiplayerPlayer player = getPlayerByID(playerID);
			
			try {
				player.reduceHealth(collidedWith.getDamage());
			} catch (NullPointerException e) {
				e.printStackTrace();
				System.out.println("Cannot find projectile with id " + projectileID);
				for (Projectile projectile : projectiles) {
					System.out.println("id: " + projectile.getMultiplayerID());
				}
			}
			
			
			if (player.getHealth() <= 0) {
				player.resetHealth();
				player.setPosition(GAME_WIDTH / 2, GAME_HEIGHT / 2);
			}
			
			//remove the projectile from the game
			projectiles.remove(collidedWith);

		}
		
	}
	
	private Projectile getProjectileByID(int projectileID) {
		for (Projectile projectile : projectiles)
			if (projectile.getMultiplayerID() == projectileID)
				return projectile;
		
		return null;
	}
	
	private MultiplayerPlayer getPlayerByID(int playerID) {
		for (MultiplayerPlayer player : players)
			if (player.getMultiplayerID() == playerID)
				return player;
		
		return null;
	}

	/**
	 * Checks if an entity is a multiplayer player with the correct nickname
	 * @param toCheck the entity to check
	 * @param nickname the entities nickname
	 * @return whether this entity has passed the above checks
	 */
	private boolean isEntityPlayer(Entity toCheck, String nickname) {
		if (toCheck instanceof MultiplayerPlayer)
			if (((MultiplayerPlayer) toCheck).getPlayerName().equals(nickname))
				return true;
		
		return false;
	}
	
	/**
	 * Send player key presses to the server.
	 */
	private void checkInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			NetworkUtils.sendMessage("W_PRESS", client.getOutputStream());
			
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			NetworkUtils.sendMessage("S_PRESS", client.getOutputStream());
			
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			NetworkUtils.sendMessage("R_PRESS", client.getOutputStream());
			
		if (Gdx.input.isKeyPressed(Input.Keys.A)) 
			NetworkUtils.sendMessage("L_PRESS", client.getOutputStream());
			
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && player.canFireLight())
			NetworkUtils.sendMessage("LMB", client.getOutputStream());
			
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && player.canFireHeavy())
			NetworkUtils.sendMessage("RMB", client.getOutputStream());
	}

}
