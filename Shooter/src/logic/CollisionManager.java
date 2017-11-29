package logic;

import java.util.ArrayList;

import entities.Entity;

/**
 * Manages collisions between entities.
 * @author Connor Stewart
 */
public class CollisionManager {
	
	/** Entities that need to be removed from the screen. */
	private ArrayList<Entity> deadEntities  = new ArrayList<Entity>();

	/**
	 * Checks if entities have collided with each other. <br>
	 * Calls onCollision on entities when they have collided. <br>
	 * Calls onDestroy when an enemy has been destroyed.
	 * @param delta the time since the last frame was rendered
	 */
	public void checkCollision(float delta, ArrayList<Entity> activeEntities) {
		
		
		//check for collisions between entities
		for (Entity e1 : activeEntities) {
			for (Entity e2 : activeEntities) {
				if (e1.getBoundingRectangle().overlaps(e2.getBoundingRectangle())) {
					if (e1.onCollision(e1))
						deadEntities.add(e1);
					if (e2.onCollision(e1))
						deadEntities.add(e2);
				}//end checking for collisions
			}//end e2 loop
		}//end e1 loop
		
		for (Entity entity : deadEntities) {
			activeEntities.remove(entity);
			entity.onDestroy();
		}
			
		
	}
}
