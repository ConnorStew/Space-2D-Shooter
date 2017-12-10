package pickups;

import effects.Slow;

/**
 * This pickup slows all enemies.
 * @author Connor Stewart
 */
public class Time extends Pickup {

	public Time(int x, int y) {
		super("powerups/time.png", new Slow());
		setScale(0.05f);
		setPosition(x, y);
	}

}
