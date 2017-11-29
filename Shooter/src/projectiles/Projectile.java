package projectiles;

import com.badlogic.gdx.Gdx;
import enemies.Enemy;
import entities.Entity;

/**
 * Represents something the player can fire.
 * @author Connor Stewart
 */
public abstract class Projectile extends Entity {
	
	/** The amount of damage the projectile does when it hits. */
	private final double damage;
	
	/**
	 * Creates a bullet at an x/y location.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param rotation the rotation
	 * @param size the size of the image
	 * @param imageLocation the location of this projectiles image
	 */
	Projectile(float x, float y, float rotation, double damage, int speed, int size, String imageLocation) {
		super(imageLocation, 0, speed);

		this.damage = damage;
		
		setSize(size, size);
		setPosition(x - (getWidth() / 2), y - (getHeight() / 2)); //center the bullet in the middle of the ship
		setOriginCenter(); //set the origin for rotation
		setRotation(rotation + 90); //add the 90 because of the way the sprite is drawn
		moveForward(2.5); //move the bullet in front of the ship
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Enemy) //destroy the projectile if it collides with an enemy
			return true;
		
		return false;
	}
	
	@Override
	public void update(float delta) {
		moveForward(Gdx.graphics.getDeltaTime() * speed);
	}

	/**
	 * @return how much damage this projectile does
	 */
	public double getDamage() {
		return damage;
	}
	
}
