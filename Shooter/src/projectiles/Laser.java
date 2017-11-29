package projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * A fast moving medium damage projectile.
 * @author Connor Stewart
 */
public class Laser extends Projectile {
	
	/** The sound plays when a laser is fired. */
	private static final Sound laserSound = Gdx.audio.newSound(Gdx.files.internal("laserfire01.ogg"));

	/**
	 * Create a laser at the x and y location with the rotation specified.
	 * @param x the x coordinate for the laser
	 * @param y the y coordinate of the laser
	 * @param r the rotation to start the laser at
	 */
	public Laser(float x, float y, float r) {
		super(x, y, r, 4, 20, 1, "laser.png");
		laserSound.setVolume(laserSound.play(), 0.2f);
	}

	@Override
	public void onDestroy() {}

}
