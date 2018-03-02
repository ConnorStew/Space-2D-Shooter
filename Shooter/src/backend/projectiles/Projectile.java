package backend.projectiles;

import com.badlogic.gdx.Gdx;

import backend.enemies.Asteroid;
import backend.enemies.Enemy;
import backend.entities.Entity;
import backend.entities.MultiplayerPlayer;
import backend.entities.Player;

/**
 * Represents something the player can fire.
 * @author Connor Stewart
 */
public abstract class Projectile extends Entity {
	
	/** The amount of damage the projectile does when it hits. */
	private final double damage;
	
	/** The type of projectile. */
	private final ProjectileType type;
	
	/**
	 * Creates a bullet at an x/y location.
	 * @param x the x coordinate
	 * @param y the y coordinate
	 * @param rotation the rotation
	 * @param size the size of the image
	 * @param imageLocation the location of this projectiles image
	 * @param type the type of projectile
	 */
	Projectile(float x, float y, float rotation, double damage, int speed, int size, String imageLocation, ProjectileType type) {
		super(imageLocation, 0, speed); //zero because projectiles do not have health

		this.type = type;
		this.damage = damage;
		
		setSize(size, size);
		setPosition(x - (getWidth() / 2), y - (getHeight() / 2)); //center the bullet in the middle of the ship
		setOriginCenter(); //set the origin for rotation
		setRotation(rotation + 90); //add the 90 because of the way the sprite is drawn
		moveForward(2.5); //move the bullet in front of the ship
	}
	
	Projectile(float x, float y, float rotation, double damage, int speed, int size, String imageLocation, ProjectileType type, int id) {
		super(imageLocation, 0, speed, id); //zero because projectiles do not have health

		this.type = type;
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
			if (type.equals(ProjectileType.PLAYER)) //if the projectile was fired by the player
				return true;
		
		if (collidedWith instanceof Player || collidedWith instanceof MultiplayerPlayer) //destroy the projectile if it collides with a player
			if (type.equals(ProjectileType.ENEMEY)) //if the projectile was fired by an enemy
				return true;
		
		if (collidedWith instanceof Asteroid)
			if (type.equals(ProjectileType.ENEMEY))
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
	
	/**
	 * @return the type of projectile
	 */
	public ProjectileType getType() {
		return type;
	}
	
}
