package backend.pickups;

import backend.effects.Effect;
import backend.entities.Entity;
import backend.entities.Player;
import ui.SPGame;

/**
 * Generic class for pickups, entities that can be collided with to activate effects.
 * @author Connor Stewart
 */
public abstract class Pickup extends Entity {
	
	/** The speed that pickups rotate at. */
	private static int rotationSpeed = 20;
	
	/** The effect that is triggered when this pickup is picked up. */
	private Effect effect;

	/**
	 * Creates a pickup.
	 * @param imageLocation the image to use
	 * @param effect the effect to activate when collided with
	 */
	public Pickup(String imageLocation, Effect effect) {
		//zero max health and zero speed because a powerup dosen't move and dosen't take damage
		super(imageLocation, 0, 0);
		this.effect = effect;
	}
	
	@Override
	public void onDestroy() {}
	
	@Override
	public void update(float delta) {
		//rotate pickups
		rotate(rotationSpeed * delta);
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player) {
			//activate the effect on collision
			SPGame.getInstance().addEffect(effect);
			return true;
		}
			
		return false;
	}
	
}
