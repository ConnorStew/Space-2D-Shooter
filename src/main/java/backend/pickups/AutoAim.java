package backend.pickups;

import backend.effects.BetterWeapon;

public class AutoAim extends Pickup {

	public AutoAim(int x, int y) {
		super("powerups/autoAim.png", new BetterWeapon());
		setScale(0.05f);
		setPosition(x, y);
	}

}
