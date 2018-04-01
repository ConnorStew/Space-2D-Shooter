package backend.effects;

import backend.entities.Player;
import backend.weapons.PlayerAutoWeapon;
import backend.weapons.PlayerLightWeapon;
import ui.SPGame;

/**
 * This class is used to define the automatic aiming powerup which gives the player the {@link backend.weapons.PlayerAutoWeapon} for the duration of this effect.
 * @author Connor Stewart
 */
public class BetterWeapon extends Effect {
	
	/** The player that fired this weapon. */
	private final Player PLAYER;

	/**
	 * Gives the player an upgraded weapon for the duration of this effect.
	 * @param game the game this effect has been activated in
	 */
	public BetterWeapon(SPGame game) {
		super(game, 10);
		PLAYER = game.getPlayer();
	}

	@Override
	public void update() {
		if (!(PLAYER.getLeftWeapon() instanceof PlayerAutoWeapon))
			PLAYER.setLeftWeapon(new PlayerAutoWeapon(GAME));
	}

	@Override
	public void end() {
		PLAYER.setLeftWeapon(new PlayerLightWeapon());
	}

}
