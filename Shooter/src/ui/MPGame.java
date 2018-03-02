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
	
	/** Entities that are currently active in the game. */
	private CopyOnWriteArrayList<Entity> activeEntities  = new CopyOnWriteArrayList<Entity>();
	
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
	
	private Vector3 oldPos;
	
	public MPGame(Client client) {
		this.client = client;
		client.addListner(this);
	}

	@Override
	public void show() {
		System.out.println(getClass().getName() + ">>>Multiplayer game started!");
		
		
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

		if (mousePos != oldPos) {
			//rotate the player towards the mouse
			player.rotateTowards(mousePos.x, mousePos.y);
			player.setRotation(player.getRotation() - 90); //-90 due to how the player sprite is drawn
			
			oldPos = mousePos;
			NetworkUtils.sendMessage("ROTATE/" + Math.floor(player.getRotation()), client.getOutputStream());
		}

		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);

		//validate camera movement
		if (player.getCenterY() - cam.viewportHeight > 0 && player.getCenterY() + cam.viewportHeight < map.getHeight())
			cam.position.y = player.getCenterY();
		
		if (player.getCenterX() - cam.viewportWidth > 0 && player.getCenterX() + cam.viewportWidth < map.getWidth())
			cam.position.x = player.getCenterX();
		
		//update entities
		for (Entity entity : activeEntities)
			entity.update(delta);
		
		//start drawing sprites
		batch.begin();
		
		//draw background
		map.draw(batch);
		
		
		for (Entity entity : activeEntities) {
			if (entity instanceof MultiplayerPlayer) {
				MultiplayerPlayer toDraw = (MultiplayerPlayer) entity;
				font.draw(batch, toDraw.getPlayerName(), toDraw.getCenterX(), toDraw.getCenterY());
				
				if (toDraw.shouldFire == true) {
					Projectile toFire;
					if (toDraw.getPlayerName().equals(client.getNickname())) {
						toFire = toDraw.fire(Gdx.graphics.getDeltaTime(), "Light", ProjectileType.PLAYER);
					} else {
						toFire = toDraw.fire(Gdx.graphics.getDeltaTime(), "Light", ProjectileType.ENEMEY);
					}
					
					
					
					if (toFire != null) {
						activeEntities.add(toFire);	
						toDraw.shouldFire = false;
					}
				}
			}
		}

		//check for collisions between entities and this clients projectiles
		for (Entity e1 : activeEntities) {
			for (Entity e2 : activeEntities) {
				if (!e1.equals(e2)) {
					if (e1.getBoundingRectangle().overlaps(e2.getBoundingRectangle())) {
						if (e1 instanceof Projectile)
							activeEntities.remove(e1);
						
						if (e2 instanceof Projectile)
							activeEntities.remove(e2);
						
						if (e1.onCollision(e2)) {
							NetworkUtils.sendMessage("REMOVE/" + e1.getMultiplayerID() , client.getOutputStream());
						}
						if (e2.onCollision(e1)) {
							NetworkUtils.sendMessage("REMOVE/" + e2.getMultiplayerID() , client.getOutputStream());
						}
					}//end checking for collisions
				}
			}//end e2 loop
		}//end e1 loop

		//draw 
		for (Entity entity : activeEntities)
			entity.draw(batch);
		
		//stop drawing sprites
		batch.end();
		
		//start drawing shapes
		sr.begin(ShapeRenderer.ShapeType.Filled);
		
		//draw health bars
		for (Entity entity : activeEntities)
			if (entity.hasHealth())
				entity.drawHP(sr, cam); //draw health bar

		
		
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
					MultiplayerPlayer toAdd = new MultiplayerPlayer(50, 50, playerName, Integer.parseInt(arguments[1]));
					activeEntities.add(toAdd);
					if (toAdd.getPlayerName().equals(client.getNickname()))
						player = toAdd;
				}
			});
			
		}
		
		if (command.equals("MOVE")) {
			MultiplayerPlayer toMove = null;
			for (Entity entity : activeEntities) {
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
			for (Entity entity : activeEntities)
				if (isEntityPlayer(entity, arguments[0]))
					((MultiplayerPlayer) entity).setRotation(Float.parseFloat(arguments[1]));
		
		if (command.equals("SHOOTL")) {
			for (Entity entity : activeEntities) {
				if (isEntityPlayer(entity, arguments[0])) {
					MultiplayerPlayer mp = (MultiplayerPlayer) entity;
					mp.shouldFire = true;
					mp.fireID = Integer.parseInt(arguments[1]);
				}
			}
		}
		
		if (command.equals("DELETE")) {
			int id = Integer.parseInt(arguments[0]);
			
			for (Entity entity : activeEntities) {
				if (entity.getMultiplayerID() == id) {
					entity.onDestroy();
					activeEntities.remove(entity);
				}
			}

		}
		
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
			
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			NetworkUtils.sendMessage("LMB", client.getOutputStream());
			
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
			NetworkUtils.sendMessage("RMB", client.getOutputStream());
	}

}
