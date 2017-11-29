package enemies;

import entities.Entity;
import projectiles.Projectile;
import ui.GameScreen;

class Dropship extends Enemy {

	/** The dropship's default pixels per second. */
	private static final int speed = 5;
	
	/** Points awarded for killing a dropship. */
	private static final int points = 50;
	
	/** Damage the dropship does on collision with the player. */
	private static final int damage = 0;
	
	/** Dropship maximum health. */
	private static final int maxHealth = 50;
	
	/** Distance in pixels for dropships to keep away from the player. */
	private static final double distance = 25;
	
	/** The seconds in between spawning enemies. */
	private static final double spawnDelay = 2;
	
	/** The time since an enemy was spawned. */
	private double spawnTimer = 0;
	
	Dropship(float x, float y) {
		super(x, y, points, speed, damage, maxHealth, "dropship.png");
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Projectile) {
			reduceHealth(((Projectile) collidedWith).getDamage());
			
			if (health <= 0) { //remove the enemy if it has no health left
				GameScreen.addToScore(points); //add this enemies points to the score
				return true; //destroy this enemy
			}
		}
		
		return false;
	}

	@Override
	public void onDestroy() {}

	@Override
	public void update(float delta) {
		spawnTimer += delta;
		//move towards the player, but keep distance
		if (distanceBetween(GameScreen.getPlayer()) > Dropship.distance) {
			spawnTimer = 0; //reset timer if dropship movesddd
			moveTowards(GameScreen.getPlayer(), delta);
		} else {
			//spawn enemies if not moving
			if (spawnTimer > spawnDelay) {
				spawnTimer = 0; //reset spawn timerw
				GameScreen.addEntity(new Orb(getCenterX(), getCenterY()));
			}
			
		}
	}
	
}
