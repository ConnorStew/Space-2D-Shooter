package backend;

import java.util.concurrent.CopyOnWriteArrayList;

import backend.effects.Effect;
import backend.entities.Entity;
import backend.entities.Player;
import backend.logic.Spawner;
import backend.projectiles.Projectile;

/**
 * The Engine class handles all the calculations needed for the game.
 * @author Connor Stewart
 */
public class SinglePlayerEngine extends Engine {

	/** Manager spawning enemies. */
	private Spawner spawner;
	
	/** The players score. */
	private int score;

	private Player player;
	

	public SinglePlayerEngine() {
		//instantiate logic entities
		spawner = new Spawner(this);

		
		player = new Player(50, 50, this);
		
		//reset score
		score = 0;
		
		
		//add the player entity
		activeEntities.add(player);
	}
	
	public void update(float delta) {
		//poll for user input
		checkInput(delta);
		
		//spawn enemies
		spawner.spawnEnemies(delta);
		
		//check for collisions between entities
		for (Entity e1 : activeEntities) {
			for (Entity e2 : activeEntities) {
				if (e1.getBoundingRectangle().overlaps(e2.getBoundingRectangle())) {
					if (e1.onCollision(e1)) {
						e1.onDestroy();
						activeEntities.remove(e1);
					}
					if (e2.onCollision(e1)) {
						e2.onDestroy();
						activeEntities.remove(e2);
					}
				}//end checking for collisions
			}//end e2 loop
		}//end e1 loop
		
		//loop through effects
		for (Effect effect : activeEffects)
			effect.time(delta, this);
		
		//move entities
		for (Entity entity : activeEntities)
			entity.update(delta);

	}
	
	/**
	 * Checks for user input and reacts accordingly.
	 * @param delta the time since the last frame was rendered
	 */
	private void checkInput(float delta) {
		Projectile potentialProjectile = player.fire(delta);
		if (potentialProjectile != null)
			activeEntities.add(potentialProjectile);
	}
	
	/**
	 * Adds an amount to the score.
	 * @param points the amount of points to add to the score.
	 */
	public void addToScore(int points) {
		score += points;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public int getScore() {
		return score;
	}

	public CopyOnWriteArrayList<Entity> getActiveEntities() {
		return activeEntities;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

}
