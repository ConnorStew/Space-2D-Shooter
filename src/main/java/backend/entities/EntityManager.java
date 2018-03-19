package backend.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * This class manages the storage, removal and addition of entities in a game. 
 * @author Connor Stewart
 */
public class EntityManager {
	
	/** The entities active in the game. */
	private ArrayList<Entity> entities = new ArrayList<Entity>();
	
	/** The entities queued to be added to the game. */
	private Stack<Entity> entitiesToAdd = new Stack<Entity>();
	
	/** The entities queued to be added to the game. */
	private Stack<Entity> entitiesToRemove = new Stack<Entity>();
	
	/**
	 * Adds and removes entities as necessary.
	 */
	public void cycle() {
		while (!entitiesToRemove.isEmpty())
			entities.remove(entitiesToRemove.pop());
			
		while (!entitiesToAdd.isEmpty())
			entities.add(entitiesToAdd.pop());
	}
	
	/**
	 * Queue an entity for removal from this manager.
	 * @param toRemove the entity to remove
	 */
	public void removeEntity(Entity toRemove) {
		entitiesToRemove.push(toRemove);
	}
	
	/**
	 * Queue an entity for addition to this manager.
	 * @param toAdd the entity to add
	 */
	public void addEntity(Entity toAdd) {
		entitiesToAdd.push(toAdd);
	}
	
	/**
	 * Returns a list of unmodifiable entities stored in this manager
	 * @return a list of entities
	 */
	public List<Entity> getActiveEntities() {
		return Collections.unmodifiableList(entities);
	}
	
}
