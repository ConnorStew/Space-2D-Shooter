package backend.enemies;

import backend.entities.Entity;
import ui.SPGame;

/**
 * An enemy that keeps a distance away from the player and spawns {@link Runner}s.
 * @author Connor Stewart
 */
public class Dropship extends Enemy {
	
	/** The default spawning delay */
	public static final int DEFAULT_DELAY = 2;

	/** Distance in pixels for dropships to keep away from the player. */
	private static final double DISTANCE = 25;
	
	/** The seconds in between spawning enemies. */
	private double spawnDelay = 2;
	
	/** The time since an enemy was spawned. */
	private double spawnTimer = 0;
	
	/**
	 * Create a dropship at an x and y location.
	 * @param x the x location to spawn the dropship at
	 * @param y the y location to spawn the dropship at
	 * @param game the game this enemy has been spawned in
	 */
	public Dropship(float x, float y, SPGame game) {
		super(x, y, 50, 5, 0, 50, 4, "enemies/dropship.png", game);
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		return (takeProjectileDamage(collidedWith));
	}

	@Override
	public void update(float delta) {
		spawnTimer += delta; //update spawn timer every update
		
		//move towards the player, but keep distance
		if (distanceBetween(GAME.getPlayer()) > DISTANCE) {
			moveTowards(GAME.getPlayer(), delta);
		} else { //can only spawn when not moving
			if (spawnTimer > spawnDelay) {
				spawnTimer = 0; //reset spawn timer
				GAME.addEntity(new Runner(getCenterX(), getCenterY(), GAME));
			}
		}
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
