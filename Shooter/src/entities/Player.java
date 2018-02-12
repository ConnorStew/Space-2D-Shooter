package entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import enemies.Enemy;
import projectiles.Beam;
import projectiles.Missile;
import projectiles.Projectile;
import projectiles.ProjectileType;
import ui.GameScreen;
import ui.UI;

/**
 * Class used to represent that player character.
 * @author Connor Stewart
 */
public class Player extends Entity {
	
	/** The cooldown in seconds of the light weapon. */
	private final double LGIHT_CD = 0.3;
	
	/** The cooldown in seconds of the light weapon. */
	private final double HEAVY_CD = 1.5;
	
	/** The amount of speed (pixels per second) to lose each second. */
	private final double DRAG = 5;
	
	/** The maximum speed the player can travel at. */
	private final double MAX_SPEED = 15;
	
	/** The maximum amount of health the player can have. */
	private final static int MAX_HEALTH= 20;
	
	/** The players current speed. */
	private static int speed = 20;
	
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
		super("res/ship.png", MAX_HEALTH, speed);
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
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && lightTimer >= LGIHT_CD) {
			lightTimer = 0;
			return new Beam(getCenterX(), getCenterY(), getRotation());
		}
		
		//if the rmb is pressed and the heavy weapon is above or equal to the cooldown time
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && heavyTimer >= HEAVY_CD) {
			heavyTimer = 0;
			return new Missile(getCenterX(), getCenterY(), getRotation());
		}
		
		return null; //return nothing if the validation is not passed
	}
	
	/**
	 * Heals the player by an amount.
	 * @param healAmount the amount to heal the player by
	 */
	public void heal(int healAmount) {
		if (health + healAmount > MAX_HEALTH)
			health = MAX_HEALTH; //heal to max health if the player will be overhealed
		else
			health += healAmount;
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Enemy)
			reduceHealth(((Enemy) collidedWith).getDamage());
		
		if (collidedWith instanceof Projectile) {
			//if colliding with an enemy projectile
			if (((Projectile) collidedWith).getType().equals(ProjectileType.ENEMEY)) {
				reduceHealth(((Projectile) collidedWith).getDamage());
			}
		}
		
		if (getHealth() <= 0)
			return true;
		
		return false;
	}

	@Override
	public void onDestroy() {
		UI.changeScreen(UI.SCORE_SCREEN);
	}

	@Override
	public void update(float delta) {

		//increase momentum on button press
		if (Gdx.input.isKeyPressed(Input.Keys.W) && yDelta < MAX_SPEED)
			yDelta += (speed * delta);
		
		if (Gdx.input.isKeyPressed(Input.Keys.S) && yDelta > -MAX_SPEED)
			yDelta -= (speed * delta);
		
		if (Gdx.input.isKeyPressed(Input.Keys.A) && xDelta > -MAX_SPEED)
			xDelta -= (speed * delta);
		
		if (Gdx.input.isKeyPressed(Input.Keys.D) && xDelta < MAX_SPEED)
			xDelta += (speed * delta);
		
		//apply drag
		if (xDelta > 0)
			xDelta -= (DRAG * delta);
		
		if (xDelta < 0)
			xDelta += (DRAG * delta);
		
		if (yDelta > 0)
			yDelta -= (DRAG * delta);
		
		if (yDelta < 0)
			yDelta += (DRAG * delta);
		
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