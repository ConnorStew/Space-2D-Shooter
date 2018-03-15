package backend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * Used to represent an entity that does not interact with other entity.
 * @author Connor Stewart
 */
public class InanimateEntity extends Sprite {
	
	/**
	 * Creates a new entity.
	 * @param imageLocation the path to the image file for this entity
	 */
	public InanimateEntity(String imageLocation) {
		super(new Texture(Gdx.files.internal(imageLocation)));
	}
	
	/**
	 * Creates an entity with a specific size
	 * @param imageLocation the path to the image file for this entity
	 * @param gameWidth the width of the entity
	 * @param gameHeight the height of the entity
	 */
	public InanimateEntity(String imageLocation, float gameWidth, float gameHeight) {
		this(imageLocation);
		setSize(gameWidth, gameHeight);
	}
	
	/**
	 * @return the x coordinate of the center of the entity
	 */
	public float getCenterX() {
		return getX() + (getWidth() / 2);
	}
	
	/**
	 * @return the y coordinate of the center of the entity
	 */
	public float getCenterY() {
		return getY() + (getHeight() / 2);
	}

}
