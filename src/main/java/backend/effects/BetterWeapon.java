package backend.effects;

import backend.entities.Player;
import backend.weapons.PlayerAutoWeapon;
import backend.weapons.PlayerLightWeapon;
import ui.SPGame;

public class BetterWeapon extends Effect {
	
	private Player player;

	public BetterWeapon(double duration) {
		super(duration);
		player = SPGame.getInstance().getPlayer();
	}

	@Override
	public void update() {
		if (!(player.getLeftWeapon() instanceof PlayerAutoWeapon))
			player.setLeftWeapon(new PlayerAutoWeapon());
	}

	@Override
	public void end() {
		player.setLeftWeapon(new PlayerLightWeapon());
	}

}
