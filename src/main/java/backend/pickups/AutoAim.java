package backend.pickups;

import backend.effects.BetterWeapon;
import ui.SPGame;

public class AutoAim extends Pickup {

	public AutoAim(int x, int y, SPGame game) {
		super("powerups/autoAim.png", new BetterWeapon(game), game);
		setScale(0.05f);
		setPosition(x, y);
	}

}
