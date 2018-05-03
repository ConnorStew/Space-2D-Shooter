package backend.effects;

import ui.SPGame;

/**
 * Generic class for things that affect the game as a whole.
 * @author Connor Stewart
 */
public abstract class Effect {
	
	/** The duration of this effect. */
	private final double DURATION;

	/** The timer used to time this effect. */
	private double timer = 0;

	/** The game screen this effect has been activated on. */
	protected final SPGame GAME;

	/**
	 * Creates an effect.
	 * @param game the game this effect has been activated in
	 * @param duration the duration of the effect
	 */
	Effect(SPGame game, double duration) {
		this.DURATION = duration;
		this.GAME = game;
	}
	
	/**
	 * Times this effect.
	 * @param delta the amount of time since the last update
	 * @return whether this effect should be removed
	 */
	public boolean time(float delta) {
		timer += delta;
		
		//activate the effect once if the duration is zero
		if (DURATION == 0) {
			end();
			update();
			return true;
		}
		
		if (DURATION > timer) {
			update();
			return false;
		} else {
			end();
			return true;
		}
	}
	
	/**
	 * This methods defines what should happen while this effect is active.
	 */
	public abstract void update();
	
	/**
	 * This method should define ending the effect, such as resetting the effects of the effect.
	 */
	public abstract void end();

}
