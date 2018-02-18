package backend.effects;

import backend.SinglePlayerEngine;

/**
 * This effect heals the player.
 * @author Connor Stewart
 */
public class Heal extends Effect {
	
	/** The amount the player is healed for. */
	private final static int HEAL_AMOUNT = 5;

	public Heal() {
		super(0); //instant effect
	}

	@Override
	public void update(SinglePlayerEngine engine) {
		engine.getPlayer().heal(HEAL_AMOUNT);
	}

	@Override
	public void end(SinglePlayerEngine engine) {}

}
