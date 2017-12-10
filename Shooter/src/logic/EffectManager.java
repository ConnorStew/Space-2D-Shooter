package logic;

import java.util.ArrayList;

import effects.Effect;

/**
 * Manages effects.
 * @author Connor Stewart
 */
public class EffectManager {
	
	/** Effects that need to be added to the game. */
	private ArrayList<Effect> newEffects  = new ArrayList<Effect>();
	
	/** Effects that are currently active in the game. */
	private ArrayList<Effect> activeEffects  = new ArrayList<Effect>();
	
	/** effects that need to be removed from the game. */
	private ArrayList<Effect> deadEffects  = new ArrayList<Effect>();

	/**
	 * Adds a new effect to the game.
	 * @param toAdd the effect to add
	 */
	public void addEffect(Effect toAdd) {
		newEffects.add(toAdd);
	}
	
	/**
	 * Cycles through active effects.
	 * @param delta the amount of time since the last update
	 */
	public void cycle(float delta) {
		//add any effects queued for addition
		for (Effect newEffect : newEffects)
			activeEffects.add(newEffect);
		
		//delete any effects queued for removal
		for (Effect deadEffect : deadEffects)
			activeEffects.remove(deadEffect);
		
		//clear temporary arrays
		newEffects.clear();
		deadEffects.clear();
		
		//activate active effects
		for (Effect effect : activeEffects)
			if (effect.time(delta))
				deadEffects.add(effect);
			
	}

}
