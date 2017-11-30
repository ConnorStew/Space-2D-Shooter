package enemies;

import entities.Entity;
import entities.Player;

/**
 * An enemy that flies from one side of the screen to the other. <br>
 * This enemy cannot be damaged and can only be destroyed by colliding with it.
 * @author Connor Stewart
 */
public class Asteroid extends Enemy {

	/** An asteroids default pixels per second. */
	private static final int SPEED = 15;
	
	/** Points awarded for killing an asteroid. */
	private static final int POINTS = 0;
	
	/** Damage an asteroid does on collision with the player. */
	private static final int DAMAGE = 5;
	
	/** An asteroids maximum health. */
	private static final int MAX_HEALTH = 0; //zero because it cannot be damaged
	
	/** The size of the sprite. */
	private static final int SIZE = 2;
	
	/**
	 * Create an asteroid at an x and y location.
	 * @param x the x location to spawn the asteroid at
	 * @param y the y location to spawn the asteroid at
	 */
	public Asteroid(float x, float y) {
		super(x, y, POINTS, SPEED, DAMAGE, MAX_HEALTH, SIZE, "asteroid.png");
	}

	@Override
	public void onDestroy() {}

	@Override
	public void update(float delta) {
		moveForward(speed * delta);
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player) //destroy the asteroid if it collides with the player
			return true;
		
		return false;
	}

}
