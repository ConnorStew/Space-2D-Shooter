package backend.pickups;

import backend.effects.Slow;
import ui.SPGame;

/**
 * This pickup slows all enemies.
 * @author Connor Stewart
 */
public class Time extends Pickup {

	public Time(int x, int y, SPGame game) {
		super("powerups/time.png", new Slow(game), game);
		setScale(0.05f);
		setPosition(x, y);
	}


}
