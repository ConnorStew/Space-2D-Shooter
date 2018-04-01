package backend.enemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import backend.entities.Entity;
import backend.entities.Player;
import ui.SPGame;

/**
 * An enemy that flies towards the player and deals damage on contact.
 * @author Connor Stewart
 */
public class Runner extends Enemy {
	
	/** The sound plays when this enemy dies. */
	private static final Sound DEATH_SOUND = Gdx.audio.newSound(Gdx.files.internal("sounds/atari_boom.wav"));
	
	/** The volume to play the orbs death sound at. */
	private static final float DEATH_SOUND_VOLUME = 0.2f;

	/**
	 * Create a runner at an x and y location.
	 * @param x the x location to spawn the runner at
	 * @param y the y location to spawn the runner at
	 * @param game the game this enemy has been spawned in
	 */
	public Runner(float x, float y, SPGame game) {
		super(x, y, 10, 15, 2, 20, 2, "enemies/runner.png", game);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//play the death sound when a runner is destroyed
		DEATH_SOUND.setVolume(DEATH_SOUND.play(), DEATH_SOUND_VOLUME);
	}
	
	@Override
	public void update(float delta) {
		moveTowards(GAME.getPlayer(), delta); //go towards the player
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player) //destroy the runner if it collides with the player
			return true;
		
		if (collidedWith instanceof Asteroid) //destroy the runner if it collides with an asteroid
			return true;

		return takeProjectileDamage(collidedWith);
	}

}