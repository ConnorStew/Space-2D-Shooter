package backend.enemies;

import backend.entities.Entity;
import backend.projectiles.Ball;
import ui.SPGame;

/**
 * An enemy that keeps a distance away from the player and shoots at the player.
 * @author Connor Stewart
 */
public class Laser extends Enemy {
	
	/** The lasers default pixels per second. */
	private static final int SPEED = 5;
	
	/** Points awarded for killing a laser. */
	private static final int POINTS = 50;
	
	/** Damage the laser does on collision with the player. */
	private static final int DAMAGE = 0;
	
	/** Laser maximum health. */
	private static final int MAX_HEALTH = 15;
	
	/** Distance in pixels for lasers to keep away from the player. */
	private static final double DISTANCE = 30;
	
	/** The size of the sprite. */
	private static final int SIZE = 2;
	
	/** The seconds in between firing projectiles. */
	private static final double FIRING_DELAY = 0.5;
	
	/** The time since a projectile was fired. */
	private double fireTimer = 0;

	/**
	 * Create a laser at an x and y location.
	 * @param x the x location to spawn the dropship at
	 * @param y the y location to spawn the dropship at
	 */
	public Laser(float x, float y) {
		super(x, y, POINTS, SPEED, DAMAGE, MAX_HEALTH, SIZE, "laserShip.png");
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
		fireTimer += delta;
		rotateTowards(SPGame.getInstance().getPlayer());
		
		if (distanceBetween(SPGame.getInstance().getPlayer()) > DISTANCE) {
			moveForward(SPEED * delta);
		} else {
			if (fireTimer > FIRING_DELAY) {
				fireTimer = 0;
				SPGame.getInstance().addEntity(new Ball(getCenterX(), getCenterY(), getRotation() - 90)); //90 to offset image rotation
			}
		}
	}


}
