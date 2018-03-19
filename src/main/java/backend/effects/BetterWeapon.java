package backend.effects;

import backend.entities.Player;
import ui.SPGame;

public class BetterWeapon extends Effect {

	public BetterWeapon(double duration) {
		super(duration);
	}

	@Override
	public void update() {
		Player player = SPGame.getInstance().getPlayer();
	}

	@Override
	public void end() {

	}

}
