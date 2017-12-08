package powerups;

import entities.Entity;
import entities.Player;

/**
 * Generic class for powerups.
 * @author Connor Stewart
 */
public abstract class Powerup extends Entity {
	
	/** The speed that powerups rotate at. */
	private static int rotationSpeed = 20;

	public Powerup(String imageLocation) {
		//zero max health and zero speed because a powerup dosen't move and dosen't take damage
		super(imageLocation, 0, 0);
	}
	
	/** Active this powerups effect. */
	abstract void effect();
	
	@Override
	public void update(float delta) {
		rotate(rotationSpeed * delta);
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player) {
			effect();
			return true;
		}
			
		return false;
	}
	
}
