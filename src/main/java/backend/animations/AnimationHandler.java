package backend.animations;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * This class handles displaying animations.
 * @author Connor Stewart
 */
public class AnimationHandler {
	
	/** The class that handles storing the animation. */
	private Animation<TextureRegion> animation;
	
	/** Elapsed time of the animation. */
	private float stateTime;
	
	/** The x position to draw the animation at. */
	private float drawX;
	
	/** The y position to draw the animation at. */
	private float drawY;

	/** The width of the animation. */
	private float width;

	/** The height of the animation. */
	private float height;
	
	/**
	 * Create an animation from a spritesheet.
	 * @param frameColumns the amount of columns in the spritesheet
	 * @param frameRows the amount of rows in the spritesheet
	 * @param spriteSheet the spritesheet to base the animation from
	 * @param x the x coordinate to draw the animation at
	 * @param y the y coordinate to draw the animation at
	 * @param width the width of the animation
	 * @param height the height of the animation
	 */
	AnimationHandler(int frameColumns, int frameRows, Texture spriteSheet, float x, float y, float width, float height) {
		this.width = width;
		this.height = height;
		drawX = x;
		drawY = y;
		
		//split the spritesheet into individual textures
		TextureRegion[][] tmp = TextureRegion.split(spriteSheet, 
				spriteSheet.getWidth() / frameColumns,
				spriteSheet.getHeight() / frameRows);

		TextureRegion[] frames = new TextureRegion[frameColumns * frameRows];
		int index = 0;
		for (int i = 0; i < frameRows; i++) {
			for (int j = 0; j < frameColumns; j++) {
				frames[index++] = tmp[i][j];
			}
		}
		
		animation = new Animation<TextureRegion>(0.050f, frames);
	}
	
	/**
	 * Gets the current frame the animation is on.
	 * @return the texture region of the current frame
	 */
	private TextureRegion getCurrentFrame() {
		return animation.getKeyFrame(stateTime, true);
	}
	
	/**
	 * Updates this animation.
	 * @param delta the time since the last frame was rendered
	 * @return whether this animation should be stopped
	 */
	public boolean update(float delta) {
		stateTime += delta;
		return animation.isAnimationFinished(stateTime);
	}
	
	/**
	 * Draw this animation.
	 * @param batch the sprite batch to draw this animation with
	 */
	public void draw(SpriteBatch batch) {
		batch.draw(getCurrentFrame(), drawX, drawY, width, height);
	}

}
