package backend.pickups;

import backend.Engine;
import backend.effects.Slow;

/**
 * This pickup slows all enemies.
 * @author Connor Stewart
 */
public class Time extends Pickup {

	public Time(int x, int y, Engine engine) {
		super("powerups/time.png", new Slow(), engine);
		setScale(0.05f);
		setPosition(x, y);
	}


}
