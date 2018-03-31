package ui;

import backend.animations.AnimationHandler;
import backend.effects.Effect;
import backend.enemies.Asteroid;
import backend.enemies.Enemy;
import backend.entities.Entity;
import backend.entities.InanimateEntity;
import backend.entities.Player;
import backend.logic.Spawner;
import backend.projectiles.LockOn;
import backend.projectiles.Projectile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The screen that contains the singleplayer game.
 * @author Connor Stewart
 */
public class SPGame extends GameScreen {

	/** The height of the game. */
	public static final int GAME_HEIGHT = 100;

	/** The width of the game. */
	public static final int GAME_WIDTH = 100;

	/** The current instance of a singleplayer game. */
	private static SPGame INSTANCE;

	private CopyOnWriteArrayList<Entity> activeEntities;

	private Spawner spawner;

	private CopyOnWriteArrayList<Effect> activeEffects;

	private Player player;

	private int score;

	private CopyOnWriteArrayList<AnimationHandler> activeAnimations;

	public SPGame() {
		SPGame.INSTANCE = this;
	}

	public void show() {
		super.show();

		player = new Player(SPGame.GAME_WIDTH / 2, SPGame.GAME_HEIGHT / 2);
		
		//instantiate map
		map = new InanimateEntity("backgrounds/redPlanet.png", SPGame.GAME_WIDTH, SPGame.GAME_HEIGHT);

		//instantiate camera position
		cam.position.set(player.getX(), player.getY(), 0);
		
		//instantiate logic entities
		spawner = new Spawner(this);
		activeEntities = new CopyOnWriteArrayList<Entity>();
		activeEffects = new CopyOnWriteArrayList<Effect>();
		activeAnimations = new CopyOnWriteArrayList<AnimationHandler>();

		//reset score
		score = 0;
		
		//add the player entity
		activeEntities.add(player);
	}

	public void render(float delta) {
		super.render(delta);
		checkInput(delta);
		
		update(delta);
		
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

		//get the starTrekFont coordinates according to the current camera position
		Vector3 fontCord = new Vector3(10, 10, 0);
		cam.unproject(fontCord);
		
		//start drawing sprites
		batch.begin();
		
		//draw background
		map.draw(batch);
		
		//draw the players score
		font.draw(batch, Integer.toString(score), fontCord.x, fontCord.y);

		//draw
		for (AnimationHandler animation : activeAnimations)
			animation.draw(batch);
			
		for (Entity entity : activeEntities)
			entity.draw(batch);
		
		//stop drawing sprites
		batch.end();
		
		//start drawing shapes
		sr.begin(ShapeRenderer.ShapeType.Filled);
		
		//draw health bars
		for (Entity entity : activeEntities) {
			//if (entity instanceof LockOn)
				//((LockOn) entity).drawDebug(cam);
			
			if (entity.hasHealth())
				entity.drawHP(sr, cam); //draw health bar
		}


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
			if (effect.time(delta))
				activeEffects.remove(effect);
			
		
		//move entities
		for (Entity entity : activeEntities)
			entity.update(delta);
		
		//update the animations and remove if they need to
		for (AnimationHandler animation : activeAnimations)
			if (animation.update(delta))
				activeAnimations.remove(animation);

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
	
	public Enemy getNearestVisibleEnemy(LockOn projectile) {
		double lowestDistance = 100000000;
		Enemy closestEnemy = null;
		
		for (Entity entity : activeEntities) {
			if (entity instanceof Enemy) {
				if (!(entity instanceof Asteroid)) {
					if (projectile.canSee(entity)) {
						double distance = projectile.distanceBetween(entity);
						if (distance < lowestDistance) {
							closestEnemy = (Enemy) entity;
							lowestDistance = distance;
						}
					}
				}
			}
		}
		
		return closestEnemy;
	}

	public void addAnimation(AnimationHandler toAdd) {
		activeAnimations.add(toAdd);
	}
	
}
