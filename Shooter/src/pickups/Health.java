package pickups;

import effects.Heal;

/**
 * This pickup heals the player.
 * @author Connor Stewart
 */
public class Health extends Pickup {

	public Health(int x, int y) {
		super("res/powerups/health.png", new Heal());
		setScale(0.05f);
		setPosition(x, y);
	}

}
