package projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * A fast moving medium damage projectile.
 * @author Connor Stewart
 */
public class Beam extends Projectile {
	
	/** The amount of damage this projectile does. */
	private static final int DAMAGE = 4;
	
	/** The amount of pixels per seconds this laser moves at. */
	private static final int SPEED = 20;
	
	/** The size of the laser. */
	private static final int SIZE = 1;
	
	/** The sound plays when a laser is fired. */
	private static final Sound LASER_SOUND = Gdx.audio.newSound(Gdx.files.internal("res/laserfire01.ogg"));
	
	/** The volume to play the laser sound at. */
	private static final float LASER_SOUND_VOLUME = 0.2f;

	/**
	 * Create a laser at the x and y location with the rotation specified.
	 * @param x the x coordinate for the laser
	 * @param y the y coordinate of the laser
	 * @param r the rotation to start the laser at
	 */
	public Beam(float x, float y, float r) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "res/laser.png", ProjectileType.PLAYER);
		LASER_SOUND.setVolume(LASER_SOUND.play(), LASER_SOUND_VOLUME);
	}

	@Override
	public void onDestroy() {}

}
