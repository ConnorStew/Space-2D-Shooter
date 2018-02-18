package backend.effects;

import backend.SinglePlayerEngine;

/**
 * Generic class for things that affect the game.
 * @author Connor Stewart
 */
public abstract class Effect {
	
	/** The duration of this effect. */
	protected final double DURATION;
	
	/** The timer used to time this effect. */
	protected double timer = 0;
	
	/**
	 * Creates an effect.
	 * @param duration the duration of the effect.
	 */
	public Effect(double duration) {
		this.DURATION = duration;
	}
	
	/**
	 * Times this effect.
	 * @param delta the amount of time since the last update
	 * @return whether this effect should be removed
	 */
	public boolean time(float delta, SinglePlayerEngine engine) {
		timer += delta;
		
		//activate the effect once if the duration is zero
		if (DURATION == 0) {
			end(engine);
			update(engine);
			return true;
		}
		
		if (DURATION > timer) {
			update(engine);
		} else {
			end(engine);
			return true;
		}
		
		return false;
	}
	
	/**
	 * This methods defines what should happen while this effect is active.
	 */
	public abstract void update(SinglePlayerEngine engine);
	
	/**
	 * This method should define ending the effect, such as resetting the effects of the effect.
	 */
	public abstract void end(SinglePlayerEngine engine);

}
