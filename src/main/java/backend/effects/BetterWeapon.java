package backend.effects;

import backend.entities.Player;
import backend.weapons.PlayerAutoWeapon;
import backend.weapons.Weapon;
import ui.SPGame;

public class BetterWeapon extends Effect {
	
	private Weapon defaultWeapon;

	public BetterWeapon(double duration) {
		super(duration);
	}

	@Override
	public void update() {
		Player player = SPGame.getInstance().getPlayer();
		
		if (!(player.getLeftWeapon() instanceof PlayerAutoWeapon)) {
			defaultWeapon = player.getLeftWeapon();
			player.setLeftWeapon(new PlayerAutoWeapon());
		}
	}

	@Override
	public void end() {
		SPGame.getInstance().getPlayer().setLeftWeapon(defaultWeapon);
	}

}
