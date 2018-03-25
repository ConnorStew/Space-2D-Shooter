package backend.effects;

import backend.entities.Player;
import backend.weapons.PlayerAutoWeapon;
import backend.weapons.PlayerLightWeapon;
import ui.SPGame;

/**
 * This class is used to define the automatic aiming powerup which gives the player the {@link backend.weapons.PlayerAutoWeapon} for the duration.
 * @author Connor Stewart
 */
public class BetterWeapon extends Effect {
	
	/** The player that fired this weapon. */
	private final Player player;

	/**
	 * Gives the player an upgraded weapon for the duration of this effect.
	 */
	public BetterWeapon() {
		super(10);
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
