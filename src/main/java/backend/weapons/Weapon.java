package backend.weapons;

import backend.projectiles.Projectile;

/**
 * A weapon is something that is fired by both a player and an enemy.<br>
 * This class defines what {@link backend.projectiles.Projectile} this weapon fires and at what speed.
 * @author Connor Stewart
 */
public abstract class Weapon {
	
	/** The cooldown in seconds of this weapon. */
	private final float cd;
	
	/** The time since the last projectile was fired. */
	private float timer;
	
	/**
	 * Creates a weapon with the specified cooldown.
	 * @param cooldown the cooldown in seconds, in between shots
	 */
	Weapon(float cooldown) {
		cd = cooldown;
		timer = cd;
	}
	
	/**
	 * Updates the timer since a weapon was fired.
	 * @param delta the time since the last frame was rendered
	 */
	public void update(float delta) {
		timer = timer + delta;
	}
	
	/**
	 * Returns this weapons projectile is validation is passed.
	 * @param x the projectile's x position
	 * @param y the projectile's y position
	 * @param r the projectile's rotation
	 * @return the projectile fired by this weapon
	 */
	public Projectile fire(float x, float y, float r) {
		if (timer >= cd) {
			timer = 0;
			return getProjectile(x,y,r);
		} else {
			return null;
		}
	}
	
	/**
	 * Returns this weapons projectile without any validation - for use on the clientside of a multiplayer game.
	 * @param x the projectile's x position
	 * @param y the projectile's y position
	 * @param r the projectile's rotation
	 * @return the projectile fired by this weapon
	 */
	public Projectile fireWithoutValidation(float x, float y, float r) {
		return getProjectile(x,y,r);
	}
	
	/**
	 * This method should return the projectile that is fired by this weapon.
	 * @param x the projectile's x position
	 * @param y the projectile's y position
	 * @param r the projectile's rotation
	 * @return the projectile to fire.
	 */
	protected abstract Projectile getProjectile(float r, float y, float x);

}
