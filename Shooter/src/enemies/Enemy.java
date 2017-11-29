package enemies;

import entities.Entity;

/**
 * Generic class for enemies.
 * @author Connor Stewart
 */
public abstract class Enemy extends Entity {
	
	/** The amount of points killing this enemy rewards. */
	protected int points;
	
	/** The amount of damage the enemy does. */
	private int damage;
	
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
		this.points = points;
		this.damage = damage;
		setSize(2,2);
		setPosition(x, y);
		setOriginCenter();
	}
	
	/**
	 * @return the amount of damage the enemy does on collision
	 */
	public double getDamage() {
		return damage;
	}
	
}
