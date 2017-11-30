package enemies;

import entities.Entity;
import ui.GameScreen;

/**
 * An enemy that keeps a distance away from the player and spawns some {@link Runner}s.
 * @author Connor Stewart
 */
class Dropship extends Enemy {

	/** The dropship's default pixels per second. */
	private static final int SPEED = 5;
	
	/** Points awarded for killing a dropship. */
	private static final int POINTS = 50;
	
	/** Damage the dropship does on collision with the player. */
	private static final int DAMAGE = 0;
	
	/** Dropship maximum health. */
	private static final int MAX_HEALTH = 50;
	
	/** Distance in pixels for dropships to keep away from the player. */
	private static final double DISTANCE = 25;
	
	/** The seconds in between spawning enemies. */
	private static final double SPAWN_DELAY = 2;
	
	/** The time since an enemy was spawned. */
	private double spawnTimer = 0;
	
	/**
	 * Create a dropship at an x and y location.
	 * @param x the x location to spawn the dropship at
	 * @param y the y location to spawn the dropship at
	 */
	Dropship(float x, float y) {
		super(x, y, POINTS, SPEED, DAMAGE, MAX_HEALTH, "dropship.png");
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (takeProjectileDamage(collidedWith))
				return true; //destroy this enemy
		
		return false;
	}

	@Override
	public void onDestroy() {}

	@Override
	public void update(float delta) {
		spawnTimer += delta; //update spawn timer every update
		
		//move towards the player, but keep distance
		if (distanceBetween(GameScreen.getPlayer()) > Dropship.DISTANCE) {
			moveTowards(GameScreen.getPlayer(), delta);
		} else { //can only spawn when not moving
			if (spawnTimer > SPAWN_DELAY) {
				spawnTimer = 0; //reset spawn timer
				GameScreen.addEntity(new Runner(getCenterX(), getCenterY()));
			}
		}
	}
	
}
