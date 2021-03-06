package backend.pickups;

import backend.effects.Heal;
import ui.SPGame;

/**
 * This pickup heals the player.
 * @author Connor Stewart
 */
public class Health extends Pickup {

	public Health(int x, int y, SPGame game) {
		super("powerups/health.png", new Heal(game), game);
		setScale(0.05f);
		setPosition(x, y);
	}

}
