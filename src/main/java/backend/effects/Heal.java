package backend.effects;

import ui.SPGame;

/**
 * This effect heals the player.
 * @author Connor Stewart
 */
public class Heal extends Effect {
	
	/** The amount the player is healed for. */
	private final static int HEAL_AMOUNT = 5;

	/**
	 * Activates the heal effect on the current singleplayer player.
	 * @param game the game this effect has been activated in
	 */
	public Heal(SPGame game) {
		super(game, 0);
	}

	@Override
	public void update() {
		GAME.getPlayer().heal(HEAL_AMOUNT);
	}

	@Override
	public void end() {}

}
