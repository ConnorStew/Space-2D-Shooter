package backend.animations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

/**
 * This class is used to create an explosion animations for use when an entity dies.
 * @author Connor Stewart
 */
public class ExplosionAnimation extends AnimationHandler {
	
	/** The explosion animations texture. */
	private final static Texture texture = new Texture(Gdx.files.internal("misc/Explosion.png"));

	/**
	 * Creates a new explosion animation.
	 * @param x the x coordinate to draw the explosion animation at
	 * @param y the y coordinate to draw the explosion animation at
	 * @param width the width of the explosion animation
	 * @param height the height of the explosion animation
	 */
	public ExplosionAnimation(float x, float y, float width, float height) {
		super(12, 1, texture, x, y, width * 2, height * 2);
	}

}
