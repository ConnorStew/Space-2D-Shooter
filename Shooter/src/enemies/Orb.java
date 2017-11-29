package enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import entities.Entity;
import entities.Player;
import projectiles.Projectile;
import ui.GameScreen;

/**
 * An enemy that flies towards the player and deals damage on contact.
 * @author Connor Stewart
 */
class Orb extends Enemy {
	
	/** The sound plays when this enemy dies. */
	private static final Sound deathSound = Gdx.audio.newSound(Gdx.files.internal("atari_boom.wav"));
	
	/** The volume to play the orbs death sound at. */
	private static final float deathSoundVolume = 0.2f;
	
	/** The orbs pixels per second. */
	private static final int speed = 15;
	
	/** Points awarded for killing the orb. */
	private static final int points = 10;
	
	/** Damage the orb does on collision with the player. */
	private static final int damage = 2;
	
	/** Orbs maximum health. */
	private static final int maxHealth = 20;

	Orb(float x, float y) {
		super(x, y, points, speed, damage, maxHealth, "enemy.png");
	}

	@Override
	public void onDestroy() {
		deathSound.setVolume(deathSound.play(), deathSoundVolume);
	}
	
	@Override
	public void update(float delta) {
		//go towards the player
		rotateTowards(GameScreen.getPlayer());
		moveForward(delta * speed);
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player)
			return true;
		
		if (collidedWith instanceof Asteroid)
			return true;
		
		if (collidedWith instanceof Projectile) {
			reduceHealth(((Projectile) collidedWith).getDamage());
			
			if (health <= 0) { //remove the enemy if it has no health left
				GameScreen.addToScore(points); //add this enemies points to the score
				return true; //destroy this enemy
			}
		}
		
		return false;
	}
	
}