package projectiles;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

/**
 * A slow moving high damage projectile.
 * @author Connor Stewart
 */
public class Missile extends Projectile {
	
	/** The sound played when a missile is fired. */
	private static final Sound missileSound = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));

	/**
	 * Create a missile at the x and y location with the rotation specified.
	 * @param x the x coordinate for the missile
	 * @param y the y coordinate of the missile
	 * @param r the rotation to start the missile at
	 */
	public Missile(float x, float y, float r) {
		super(x, y, r, 15, 10, 2, "missile.png");
		missileSound.setVolume(missileSound.play(), 0.2f);
	}

	@Override
	public void onDestroy() {}

}
