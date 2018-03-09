package ui;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import backend.effects.Effect;
import backend.entities.Entity;
import backend.entities.InanimateEntity;
import backend.entities.Player;
import backend.logic.Spawner;
import backend.projectiles.Projectile;

/**
 * The screen that contains the main game.
 * @author Connor Stewart
 */
public class SPGame implements Screen {
	
	public static final int GAME_HEIGHT = 100;

	public static final int GAME_WIDTH = 100;

	private static SPGame INSTANCE;

	/** Font used to display score. */
	private BitmapFont font;

	/** Used to render the entities. */
	private SpriteBatch batch;
	
	/** Shape renderer used to render health bars. */
	private ShapeRenderer sr;
	
	/** The camera to render the game. */
	private OrthographicCamera cam;
	
	/** The background image. */
	private InanimateEntity map;

	private CopyOnWriteArrayList<Entity> activeEntities;

	private Spawner spawner;

	private CopyOnWriteArrayList<Effect> activeEffects;

	private Player player;

	private int score;
	
	public SPGame() {
		SPGame.INSTANCE = this;
	}

	@Override
	public void show() {
		player = new Player(SPGame.GAME_WIDTH / 2, SPGame.GAME_HEIGHT / 2);
		
		//instantiate map
		map = new InanimateEntity("redPlanet.png", SPGame.GAME_WIDTH, SPGame.GAME_HEIGHT);
		
		//instantiate shape renderer
		sr = new ShapeRenderer();
		
		//instantiate sprite batch
		batch = new SpriteBatch();
		
		//instantiate font for the score
		font = new BitmapFont();
		font.getData().setScale(0.2f);
		font.setUseIntegerPositions(false);
		
		//instantiate camera
		cam = new OrthographicCamera(30, 30);
		cam.position.set(player.getX(), player.getY(), 0);
		cam.zoom = 2;
		
		//instantiate logic entities
		spawner = new Spawner(this);
		activeEntities = new CopyOnWriteArrayList<Entity>();
		activeEffects = new CopyOnWriteArrayList<Effect>();
		
		//reset score
		score = 0;
		
		//add the player entity
		activeEntities.add(player);
	}

	@Override
	public void render(float delta) {
		checkInput(delta);
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		update(delta);
		
		//update camera
		cam.update();
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);

		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);
		
		//rotate the player towards the mouse
		player.rotateTowards(mousePos.x, mousePos.y);
		player.setRotation(player.getRotation() - 90); //-90 due to how the player sprite is drawn

		//validate camera movement
		if (player.getCenterY() - cam.viewportHeight > 0 && player.getCenterY() + cam.viewportHeight < map.getHeight())
			cam.position.y = player.getCenterY();
		
		if (player.getCenterX() - cam.viewportWidth > 0 && player.getCenterX() + cam.viewportWidth < map.getWidth())
			cam.position.x = player.getCenterX();

		//get the font coordinates according to the current camera position
		Vector3 fontCord = new Vector3(10, 10, 0);
		cam.unproject(fontCord);
		
		//start drawing sprites
		batch.begin();
		
		//draw background
		map.draw(batch);
		
		//draw the players score
		font.draw(batch, Integer.toString(score), fontCord.x, fontCord.y);

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
	
	public void update(float delta) {
		//poll for user input
		checkInput(delta);
		
		//spawn enemies
		spawner.spawnEnemies(delta);
		
		//check for collisions between entities
		for (Entity e1 : activeEntities) {
			for (Entity e2 : activeEntities) {
				if (e1.getBoundingRectangle().overlaps(e2.getBoundingRectangle())) {
					if (e1.onCollision(e1)) {
						e1.onDestroy();
						activeEntities.remove(e1);
					}
					if (e2.onCollision(e1)) {
						e2.onDestroy();
						activeEntities.remove(e2);
					}
				}//end checking for collisions
			}//end e2 loop
		}//end e1 loop
		
		//loop through effects
		for (Effect effect : activeEffects)
			effect.time(delta);
		
		//move entities
		for (Entity entity : activeEntities)
			entity.update(delta);

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

	public static SPGame getInstance() {
		return INSTANCE;
	}

	public int getScore() {
		return score;
	}
	
	/**
	 * Checks for user input and reacts accordingly.
	 * @param delta the time since the last frame was rendered
	 */
	private void checkInput(float delta) {
		Projectile potentialProjectile = player.fire(delta);
		if (potentialProjectile != null)
			activeEntities.add(potentialProjectile);
	}
	
	/**
	 * Adds an amount to the score.
	 * @param points the amount of points to add to the score.
	 */
	public void addToScore(int points) {
		score += points;
	}

	public CopyOnWriteArrayList<Entity> getActiveEntities() {
		return activeEntities;
	}

	public void addEntity(Entity toAdd) {
		activeEntities.add(toAdd);
		
	}

	public void addEffect(Effect effect) {
		activeEffects.add(effect);
		
	}

	public Player getPlayer() {
		return player;
	}
	
}
