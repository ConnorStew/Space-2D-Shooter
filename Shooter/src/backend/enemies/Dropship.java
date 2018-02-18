package backend.enemies;

import backend.SinglePlayerEngine;
import backend.entities.Entity;

/**
 * An enemy that keeps a distance away from the player and spawns {@link Runner}s.
 * @author Connor Stewart
 */
public class Dropship extends Enemy {
	
	/** The default spawning delay */
	public static final int DEFAULT_DELAY = 2;

	/** The dropship's default pixels per second. */
	private static final int SPEED = 5;
	
	/** Points awarded for killing a dropship. */
	private static final int POINTS = 50;
	
	/** Damage the dropship does on collision with the player. */
	private static final int DAMAGE = 0;
	
	/** Dropship maximum health. */
	private static final int MAX_HEALTH = 50;
	
	/** Distance in pixels for dropships to keep away from the player. */
	private static final double DISTANCE = 25;
	
	/** The seconds in between spawning enemies. */
	private double spawnDelay = 2;
	
	/** The size of the sprite. */
	private static final int SIZE = 4;
	
	/** The time since an enemy was spawned. */
	private double spawnTimer = 0;
	
	/**
	 * Create a dropship at an x and y location.
	 * @param x the x location to spawn the dropship at
	 * @param y the y location to spawn the dropship at
	 */
	public Dropship(float x, float y, SinglePlayerEngine engine) {
		super(x, y, POINTS, SPEED, DAMAGE, MAX_HEALTH, SIZE, "dropship.png", engine);
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (takeProjectileDamage(collidedWith))
				return true; //destroy this enemy
		
		return false;
	}

	@Override
	public void onDestroy() {}

	@Override
	public void update(float delta) {
		spawnTimer += delta; //update spawn timer every update
		
		//move towards the player, but keep distance
		if (distanceBetween(engine.getPlayer()) > DISTANCE) {
			moveTowards(engine.getPlayer(), delta);
		} else { //can only spawn when not moving
			if (spawnTimer > spawnDelay) {
				spawnTimer = 0; //reset spawn timer
				engine.addEntity(new Runner(getCenterX(), getCenterY(), engine));
			}
		}
	}

	/**
	 * Sets the time on the timer.
	 * @param newTime the new time for the timer
	 */
	public void setTimer(double newTime) {
		spawnTimer = newTime;
	}
	
	/**
	 * @return the current time on the timer
	 */
	public double getTimer() {
		return spawnTimer;
	}
	
	/**
	 * Sets a new delay for spawning runners.
	 * @param newDelay the new delay
	 */
	public void setDelay(double newDelay) {
		spawnDelay = newDelay;
	}
	
	/**
	 * @return the time in between spawning runners
	 */
	public double getDelay() {
		return spawnDelay;
	}

 	
}
