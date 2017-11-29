package logic;

import java.util.List;

import entities.Entity;

/**
 * Manages collisions between entities.
 * @author Connor Stewart
 */
public class CollisionManager {

	/**
	 * Checks if entities have collided with each other. <br>
	 * Calls onCollision on entities when they have collided. <br>
	 * Calls onDestroy when an enemy has been destroyed.
	 * @param delta the time since the last frame was rendered
	 */
	public void checkCollision(float delta, EntityManager em) {
		List<Entity> activeEntities = em.getActiveEntities();
		
		//check for collisions between entities
		for (Entity e1 : activeEntities) {
			for (Entity e2 : activeEntities) {
				if (e1.getBoundingRectangle().overlaps(e2.getBoundingRectangle())) {
					if (e1.onCollision(e1))
						em.removeEntity(e1);
					if (e2.onCollision(e1))
						em.removeEntity(e2);
				}//end checking for collisions
			}//end e2 loop
		}//end e1 loop
		
	}
}
