package logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import entities.Entity;

/**
 * This class is responsible for handling looping through entity storage.
 * @author Connor Stewart
 */
public class EntityManager {
	
	/** Entities that need to be removed from the game. */
	private ArrayList<Entity> deadEntities  = new ArrayList<Entity>();
	
	/** Entities that need to be added to the game. */
	private ArrayList<Entity> newEntities  = new ArrayList<Entity>();
	
	/** Entities that are currently active in the game. */
	private ArrayList<Entity> activeEntities  = new ArrayList<Entity>();
	
	/**
	 * Adds an entity to the game on the next cycle.
	 * @param toAdd the entity to add
	 */
	public void addEntity(Entity toAdd) {
		newEntities.add(toAdd);
	}

	/**
	 * Removes an entity from the game on the next cycle. <br>
	 * The entities {@link Entity#onDestroy()} method is also called before removal.
	 * @param toRemove the entity to remove
	 */
	public void removeEntity(Entity toRemove) {
		toRemove.onDestroy();
		deadEntities.add(toRemove);
	}
	
	/**
	 * Cycles through entities that have been queued for removal/addition.
	 */
	public void cycle() {
		//remove any entities queued for removal
		for (Entity deadEntity : deadEntities)
			activeEntities.remove(deadEntity);

		//add any entities queued for addition
		for (Entity newEntity : newEntities)
			activeEntities.add(newEntity);
		
		newEntities.clear();
		deadEntities.clear();
	}
	
	/**
	 * @return an immutable list of active entities
	 */
	public List<Entity> getActiveEntities() {
		return Collections.unmodifiableList(activeEntities);
	}
	
}
