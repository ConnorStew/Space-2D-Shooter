package enemies;

import entities.Entity;
import projectiles.Projectile;
import ui.GameScreen;

/**
 * Generic class for enemies.
 * @author Connor Stewart
 */
public abstract class Enemy extends Entity {
	
	/** The amount of points killing this enemy rewards. */
	private final int POINTS;
	
	/** The amount of damage the enemy does. */
	private final int DAMAGE;
	
	/**
	 * Create an enemy.
	 * @param x the x coordinate to spawn them on
	 * @param y the y coordinate to spawn them on
	 * @param points the amount of points this enemy rewards
	 * @param speed how many pixels this enemy moves per second
	 * @param damage the amount of damage the enemy does
	 * @param maxHealth the maximum health of this enemy
	 * @param imageLocation the location of this enemies image
	 */
	Enemy(float x, float y , int points, int speed, int damage, int maxHealth, String imageLocation) {
		super(imageLocation, maxHealth, speed);
		this.POINTS = points;
		this.DAMAGE = damage;
		setSize(2,2);
		setPosition(x, y);
		setOriginCenter();
	}
	
	/**
	 * Handles damage from a projectile to this enemy.
	 * @param collidedWith the entity this enemy collided with
	 * @return whether the damage destroyed the enemy
	 */
	protected boolean takeProjectileDamage(Entity collidedWith) {
		if (collidedWith instanceof Projectile) {
			reduceHealth(((Projectile) collidedWith).getDamage());
			
			if (health <= 0) { //remove the enemy if it has no health left
				GameScreen.addToScore(POINTS); //add this enemies points to the score
				return true; //destroy this enemy
			}
		}
		return false;
	}
	
	/**
	 * @return the amount of damage the enemy does on collision
	 */
	public double getDamage() {
		return DAMAGE;
	}
	
}
