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
	private static final int ROTATION_SPEED = 20;
	
	/** The effect that is triggered when this pickup is picked up. */
	private final Effect EFFECT;

	/** The game this pickup has been spawned in. */
	private final SPGame GAME;

	/**
	 * Creates a pickup.
	 * @param imageLocation the image to use
	 * @param effect the effect to activate when collided with
	 * @param game the game this pickup has been spawned in
	 */
	Pickup(String imageLocation, Effect effect, SPGame game) {
		//zero max health and zero speed because a powerup doesn't move and doesn't take damage
		super(imageLocation, 0, 0);
		this.GAME = game;
		this.EFFECT = effect;
	}
	
	@Override
	public void onDestroy() {}
	
	@Override
	public void update(float delta) {
		//rotate pickups
		rotate(ROTATION_SPEED * delta);
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player) {
			//activate the effect on collision
			GAME.addEffect(EFFECT);
			return true;
		}
			
		return false;
	}
	
}
