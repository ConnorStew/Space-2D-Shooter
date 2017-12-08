package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import entities.Entity;
import entities.InanimateEntity;
import entities.Player;
import logic.CollisionManager;
import logic.EntityManager;
import logic.Spawner;
import projectiles.Projectile;

/**
 * The screen that contains the main game.
 * @author Connor Stewart
 */
public class GameScreen implements Screen {
	
	/** The singleton instance of this class. */
	private static GameScreen instance = new GameScreen();
	
	/** The entity manager. */
	private static EntityManager em;
	
	/** Font used to display score. */
	private BitmapFont font;

	/** Used to render the sprites/entities. */
	private SpriteBatch batch;
	
	/** Shape renderer used to render health bars. */
	private ShapeRenderer sr;
	
	/** The camera to render the game. */
	private OrthographicCamera cam;
	
	/** The players score. */
	private static int score;
	
	/** The background image. */
	private InanimateEntity map;
	
	/** The player. */
	private Player player;
	
	/** Manages collisions between entities. */
	private CollisionManager cm;
	
	/** Manager spawning enemies. */
	public static Spawner spawner;
	
	private GameScreen(){};
	
	@Override
	public void show() {
		//instantiate entities
		map = new InanimateEntity("redPlanet.png", 100, 100);
		player = new Player(map.getCenterX(), map.getCenterY());
		
		//instantiate logic entities
		cm = new CollisionManager();
		spawner = new Spawner();
		em = new EntityManager();
		
		//instantiate shape renderer
		sr = new ShapeRenderer();
		
		//instantiate sprite batch
		batch = new SpriteBatch();
		
		//instantiate font for the score
		font = new BitmapFont();
		font.getData().setScale(0.2f);
		font.setUseIntegerPositions(false);
		
		//instanciate camera
		cam = new OrthographicCamera(30, 30);
		cam.position.set(player.getX(), player.getY(), 0);
		cam.zoom = 2;
		
		//reset score
		score = 0;
		
		em.addEntity(player);
	}

	@Override
	public void render(float delta) {
		
		//update camera
		cam.update();
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);

		//rotate the player towards the mouse
		player.rotateTowards(mousePos.x, mousePos.y);
		player.setRotation(player.getRotation() - 90); //-90 due to how the player sprite is drawn

		//validate camera movement
		if (player.getCenterY() - cam.viewportHeight > 0 && player.getCenterY() + cam.viewportHeight < map.getHeight())
			cam.position.y = player.getCenterY();
		
		if (player.getCenterX() - cam.viewportWidth > 0 && player.getCenterX() + cam.viewportWidth < map.getWidth())
			cam.position.x = player.getCenterX();

		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);
		
		//spawn an enemy
		spawner.spawnEnemies(delta);
		
		//check for collisions
		cm.checkCollision(delta, em);
		
		//poll for user input
		checkInput(delta);

		//get the font coordinates according to the current camera position
		Vector3 fontCord = new Vector3(10, 10, 0);
		cam.unproject(fontCord);
		
		//start drawing sprites
		batch.begin();
		
		//draw background
		map.draw(batch);
		

		
		//draw the players score
		font.draw(batch, Integer.toString(score), fontCord.x, fontCord.y);

		//update entities before drawing them
		em.cycle();
		
		//draw 
		for (Entity entity : em.getActiveEntities())
			entity.draw(batch);
		
		
		
		//test.render(delta);
		
		//test.draw(batch);
		
		//stop drawing sprites
		batch.end();
		
		//start drawing shapes
		sr.begin(ShapeRenderer.ShapeType.Filled);
		
		//draw health bars
		for (Entity entity : em.getActiveEntities()) {
			if (entity.hasHealth())
				entity.drawHP(sr, cam); //draw health bar
		}

		//stop drawing shapes
		sr.end();
		
		//move entities
		for (Entity entity : em.getActiveEntities())
			entity.update(delta);
		
		
	}

	/**
	 * Checks for user input and reacts accordingly.
	 * @param delta the time since the last frame was rendered
	 */
	private void checkInput(float delta) {
		Projectile potentialProjectile = player.fire(delta);
		if (potentialProjectile != null)
			em.addEntity(potentialProjectile);
	}
	
	/**
	 * Adds an amount to the score.
	 * @param points the amount of points to add to the score.
	 */
	public static void addToScore(int points) {
		GameScreen.score += points;
	}
	
	public static Player getPlayer() {
		return instance.player;
	}
	
	public static Screen getInstance() {
		return instance;
	}
	
	public static float getMapHeight() {
		return instance.map.getHeight();
	}
	
	public static float getMapWidth() {
		return instance.map.getWidth();
	}

	@Override
	public void resize(int width, int height) {
		cam.update();
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {
		batch.dispose();
		sr.dispose();
	}

	public static void addEntity(Entity toAdd) {
		em.addEntity(toAdd);
	}

	public static int getScore() {
		return score;
	}

}
