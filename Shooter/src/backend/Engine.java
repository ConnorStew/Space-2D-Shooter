package backend;

import java.util.concurrent.CopyOnWriteArrayList;

import backend.effects.Effect;
import backend.entities.Entity;

abstract public class Engine {
	
	
	/** Entities that are currently active in the game. */
	protected CopyOnWriteArrayList<Entity> activeEntities  = new CopyOnWriteArrayList<Entity>();
	
	/** Effects that are currently active in the game. */
	protected CopyOnWriteArrayList<Effect> activeEffects  = new CopyOnWriteArrayList<Effect>();

	/** The players score. */
	private int score;

	public static final int GAME_WIDTH = 100;

	public static final int GAME_HEIGHT = 100;

	abstract public void update(float delta);

	abstract public int getScore();

	public CopyOnWriteArrayList<Entity> getActiveEntities() {
		return activeEntities;
	}

	public void addToScore(int toAdd) {
		score = score + score;
	}
	
	public void addEffect(Effect toAdd) {
		activeEffects.add(toAdd);
	}
	
	public void addEntity(Entity toAdd) {
		activeEntities.add(toAdd);
	}



}
