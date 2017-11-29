package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import enemies.Enemy;
import projectiles.Laser;
import projectiles.Missile;
import projectiles.Projectile;
import ui.GameScreen;

/**
 * Class used to represent that player character.
 * @author Connor Stewart
 */
public class Player extends Entity {
	
	/** The cooldown in seconds of the light weapon. */
	private final double lightCD = 0.3;
	
	/** The cooldown in seconds of the light weapon. */
	private final double heavyCD = 1.5;
	
	/** The amount of speed (pixels per second) to lose each second. */
	private final double drag = 5;
	
	/** The maximum speed the player can travel at. */
	private final double maxSpeed = 15;
	
	/** The amount of time since the last light weapon was fired. */
	private double lightTimer = 0;
	
	/** The amount of time since the last heavy weapon was fired. */
	private double heavyTimer = 0;

	/** The amount of x pixels the player is moving per second. */
	private double xDelta = 0;
	
	/** The amount of y pixels the player is moving per second. */
	private double yDelta = 0;
	
	/**
	 * Create a player object.
	 * @param x the players starting x coordinate
	 * @param y the players starting y coordinate
	 */
	public Player(float x, float y) {
		super("ship.png", 20, 20);
		setSize(3, 3);
		setPosition(x, y);
		setOriginCenter();
	}
	
	/**
	 * Handles the players firing.
	 * @param delta the time since the last frame was rendered.
	 * @return null if the validation isn't met or a projectile object to be fired
	 */
	public Projectile fire(float delta) {
		lightTimer += delta;
		heavyTimer += delta;
		
		//if the lmb is pressed and the light weapon is above or equal to the cooldown time
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lightTimer >= lightCD) {
			lightTimer = 0;
			return new Laser(getCenterX(), getCenterY(), getRotation());
		}
		
		//if the rmb is pressed and the heavy weapon is above or equal to the cooldown time
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && heavyTimer >= heavyCD) {
			heavyTimer = 0;
			return new Missile(getCenterX(), getCenterY(), getRotation());
		}
		
		return null; //return nothing if the validation is not passed
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Enemy)
			reduceHealth(((Enemy) collidedWith).getDamage());
		
		if (getHealth() <= 0)
			return true;
		
		return false;
	}

	@Override
	public void onDestroy() {
		Gdx.app.exit();
	}

	@Override
	public void update(float delta) {

		//increase momentum on button press
		if (Gdx.input.isKeyPressed(Input.Keys.W) && yDelta < maxSpeed)
			yDelta += (speed * delta);
		
		if (Gdx.input.isKeyPressed(Input.Keys.S) && yDelta > -maxSpeed)
			yDelta -= (speed * delta);
		
		if (Gdx.input.isKeyPressed(Input.Keys.A) && xDelta > -maxSpeed)
			xDelta -= (speed * delta);
		
		if (Gdx.input.isKeyPressed(Input.Keys.D) && xDelta < maxSpeed)
			xDelta += (speed * delta);
		
		//apply drag
		if (xDelta > 0)
			xDelta -= (drag * delta);
		
		if (xDelta < 0)
			xDelta += (drag * delta);
		
		if (yDelta > 0)
			yDelta -= (drag * delta);
		
		if (yDelta < 0)
			yDelta += (drag * delta);
		
		float maxHeight = GameScreen.getMapHeight();
		float maxWidth = GameScreen.getMapWidth();
		float minHeight = 0;
		float minWidth = 0;
		
		//store the x before moving
		float tempX = getX();
		float tempY = getY();
		
		//move
		translateY((float) (delta * yDelta));
		translateX((float) (delta * xDelta));
		
		//move back if new position is invalid
		if (!(getY() < maxHeight - getHeight() && getY() > minHeight)) {
			yDelta = 0;
			setY(tempY);
		}
		
		if (!(getX() > minWidth && getX() < maxWidth - getWidth())) {
			xDelta = 0;
			setX(tempX);
		}

	}

}