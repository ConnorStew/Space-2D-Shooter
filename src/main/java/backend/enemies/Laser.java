package backend.enemies;

import backend.entities.Entity;
import backend.projectiles.Ball;
import ui.SPGame;

/**
 * An enemy that keeps a certain distance away from the player while shooting at the player.
 * @author Connor Stewart
 */
public class Laser extends Enemy {

	/** Distance in pixels for lasers to keep away from the player. */
	private static final double DISTANCE = 30;

	/** The seconds in between firing projectiles. */
	private static final double FIRING_DELAY = 0.5;
	
	/** The time since a projectile was fired. */
	private double fireTimer = 0;

	/**
	 * Create a laser at an x and y location.
	 * @param x the x location to spawn the dropship at
	 * @param y the y location to spawn the dropship at
	 * @param game the game this enemy has been spawned in
	 */
	public Laser(float x, float y, SPGame game) {
		super(x, y, 50, 5, 0, 15, 2, "enemies/laserShip.png", game);
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		return (takeProjectileDamage(collidedWith));
	}

	@Override
	public void update(float delta) {
		fireTimer += delta;
		rotateTowards(GAME.getPlayer());
		
		if (distanceBetween(GAME.getPlayer()) > DISTANCE) {
			moveForward(speed * delta);
		} else {
			if (fireTimer > FIRING_DELAY) {
				fireTimer = 0;
				GAME.addEntity(new Ball(getCenterX(), getCenterY(), getRotation() - 90)); //90 to offset image rotation
			}
		}
	}


}
