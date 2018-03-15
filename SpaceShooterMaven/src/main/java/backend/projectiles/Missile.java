package backend.projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * A slow moving high damage projectile.
 * @author Connor Stewart
 */
public class Missile extends Projectile {
	
	/** The amount of damage this projectile does. */
	private static final int DAMAGE = 15;
	
	/** The amount of pixels per seconds this laser moves at. */
	private static final int SPEED = 10;
	
	/** The size of the laser. */
	private static final int SIZE = 2;
	
	/** The sound played when a missile is fired. */
	private static final Sound missileSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
	
	/** The volume to play the missile sound at. */
	private static final float MISSILE_SOUND_VOLUME = 0.2f;

	/**
	 * Create a missile at the x and y location with the rotation specified.
	 * @param x the x coordinate for the missile
	 * @param y the y coordinate of the missile
	 * @param r the rotation to start the missile at
	 */
	public Missile(float x, float y, float r) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "missile.png", ProjectileType.PLAYER);
		missileSound.setVolume(missileSound.play(), MISSILE_SOUND_VOLUME);
	}

	public Missile(float x, float y, float r, int id, int firedByID) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "missile.png", ProjectileType.PLAYER, id, firedByID);
		missileSound.setVolume(missileSound.play(), MISSILE_SOUND_VOLUME);
	}

	public Missile(float x, float y, float r, int id, ProjectileType pType, int firedByID) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "missile.png", pType, id, firedByID);
		missileSound.setVolume(missileSound.play(), MISSILE_SOUND_VOLUME);
	}

	@Override
	public void onDestroy() {}

}
