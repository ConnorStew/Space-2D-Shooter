package ui;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JOptionPane;

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
import networking.server.ServerGame;

public class MPGame implements Screen, ClientListener {
	
	/** Players that are currently active in the game. */
	private CopyOnWriteArrayList<MultiplayerPlayer> players  = new CopyOnWriteArrayList<MultiplayerPlayer>();
	
	/** Projectiles that are currently active in the game. */
	private CopyOnWriteArrayList<Projectile> projectiles  = new CopyOnWriteArrayList<Projectile>();

	private Client client;
	
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
	
	/** The last mouse position that was sent to the server. */
	private Vector3 oldPos;
	
	public MPGame(Client client) {
		this.client = client;
		client.addListener(this);
	}

	@Override
	public void show() {
		System.out.println(getClass().getSimpleName() + " >>> Multiplayer game started!");
		
		//instantiate map
		map = new InanimateEntity("redPlanet.png", ServerGame.GAME_WIDTH, ServerGame.GAME_HEIGHT);
				
		player = new MultiplayerPlayer(ServerGame.GAME_WIDTH / 2, ServerGame.GAME_HEIGHT / 2, "default");
		
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
		
		//get the player name coordinates according to the current camera position
		Vector3 nameCord = new Vector3(player.getCenterX(), player.getCenterY(), 0);
		cam.unproject(nameCord);

		//get the score coordinates according to the current camera position
		Vector3 scoreCord = new Vector3(10, 10, 0);
		cam.unproject(scoreCord);
		
		//update camera
		cam.update();
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);

		//tell the server that you have moved your mouse
		if (mousePos != oldPos) {
			oldPos = mousePos;
			client.sendMessageToGame("<MM/" + mousePos.x + "/" + mousePos.y + ">");
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
				projectiles.remove(projectile);
		
		//start drawing sprites
		batch.begin(); 
		
		//draw background
		map.draw(batch);
		
		int yincrease = 5;
		font.setUseIntegerPositions(false);
		
		for (int i = 0; i < players.size(); i++) {
			//draw the players name
			font.draw(batch, players.get(i).getPlayerName(), players.get(i).getCenterX(), players.get(i).getCenterY());
			//draw the players scores
			font.draw(batch, players.get(i).getPlayerName() + ": " + players.get(i).getKills(), scoreCord.x, scoreCord.y - (i * yincrease));
		}

		//draw players
		for (MultiplayerPlayer player : players) {
			player.draw(batch);
			if (player.getKills() >= 10) {
				UI.getInstance().setScreen(MenuScreen.getInstance());
				JOptionPane.showMessageDialog(null, player.getPlayerName() + " has won!", "Winner", JOptionPane.INFORMATION_MESSAGE);
				client.sendMessageToServer("<DC>");
			}
		}
			
		
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
			int multiplayerID = Integer.parseInt(arguments[1]);
			System.out.println(getClass().getSimpleName() + ">>>Request to add player " + playerName + ".");
			
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					MultiplayerPlayer toAdd = new MultiplayerPlayer(50, 50, playerName, multiplayerID);
					players.add(toAdd);
					System.out.println(client.getNickname());
					
					if (toAdd.getPlayerName().equals(client.getNickname()))
						player = toAdd;
					
				}
			});
		}
		
		if (command.equals("ADDPROJECTILE")) {
			int playerID = Integer.parseInt(arguments[0]);
			int projectileID = Integer.parseInt(arguments[1]);
			String projectileType = arguments[2];
			
			Gdx.app.postRunnable(new Runnable(){
				@Override
				public void run() {
					projectiles.add(getPlayerByID(playerID).fire(Gdx.graphics.getDeltaTime(), projectileType, ProjectileType.PVP, projectileID));
				}
			});
			
		}
		
		if (command.equals("REMOVEPROJECTILE")) {
			projectiles.remove(getProjectileByID(Integer.parseInt(arguments[0])));
		}
		
		if (command.equals("REMOVEPLAYER")) {
			players.remove(getProjectileByID(Integer.parseInt(arguments[0])));
		}
		
		if (command.equals("UPDATE")) {
			String updateType = arguments[0];
			int multiplayerID = Integer.parseInt(arguments[1]);
			float x = Float.parseFloat(arguments[2]);
			float y = Float.parseFloat(arguments[3]);
			float r = Float.parseFloat(arguments[4]);
			
			if (updateType.equals("PLAYER")) {
				double health = Double.parseDouble(arguments[5]);
				int kills = Integer.parseInt(arguments[6]);
				MultiplayerPlayer player = getPlayerByID(multiplayerID);
				if (player != null) {
					player.setX(x);
					player.setY(y);
					player.setRotation(r);
					player.setHealth(health);
					player.setKills(kills);
				}
			}
			
			if (updateType.equals("PROJECTILE")) {
				Projectile projectile = getProjectileByID(multiplayerID);
				if (projectile != null) {
					projectile.setX(x);
					projectile.setY(y);
					projectile.setRotation(r);
				}
			}
			
		}
		
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
	 * Send player key presses to the server.
	 */
	private void checkInput() {
		if (Gdx.input.isKeyPressed(Input.Keys.W))
			client.sendMessageToGame("<W_PRESS>");
			
		if (Gdx.input.isKeyPressed(Input.Keys.S))
			client.sendMessageToGame("<S_PRESS>");
			
		if (Gdx.input.isKeyPressed(Input.Keys.D))
			client.sendMessageToGame("<R_PRESS>");
			
		if (Gdx.input.isKeyPressed(Input.Keys.A)) 
			client.sendMessageToGame("<L_PRESS>");
			
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			client.sendMessageToGame("<LMB>");
			
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
			client.sendMessageToGame("<RMB>");
	}
}
