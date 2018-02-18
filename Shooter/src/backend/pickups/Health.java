package backend.pickups;

import backend.Engine;
import backend.effects.Heal;

/**
 * This pickup heals the player.
 * @author Connor Stewart
 */
public class Health extends Pickup {

	public Health(int x, int y, Engine engine) {
		super("powerups/health.png", new Heal(), engine);
		setScale(0.05f);
		setPosition(x, y);
	}

}
