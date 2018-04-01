package backend.enemies;

import backend.entities.Entity;
import backend.entities.Player;
import ui.SPGame;

/**
 * An enemy that flies from one side of the screen to the other. <br>
 * This enemy cannot be damaged and can only be destroyed by colliding with it.
 * @author Connor Stewart
 */
public class Asteroid extends Enemy {

	/**
	 * Create an asteroid at an x and y location.
	 * @param x the x location to spawn the asteroid at
	 * @param y the y location to spawn the asteroid at
	 * @param game the game this enemy has been spawned in
	 */
	public Asteroid(float x, float y, SPGame game) {
		super(x, y, 0, 10, 5, 0, 4, "enemies/asteroid.png", game);
	}

	@Override
	public void onDestroy() {}

	@Override
	public void update(float delta) {
		moveForward(speed * delta);
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
	    //destroy the asteroid if it collides with the player
		return (collidedWith instanceof Player);
	}


}