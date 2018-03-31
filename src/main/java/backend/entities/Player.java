package backend.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import backend.enemies.Enemy;
import backend.projectiles.Projectile;
import backend.projectiles.ProjectileType;
import backend.weapons.PlayerHeavyWeapon;
import backend.weapons.PlayerLightWeapon;
import backend.weapons.Weapon;
import ui.ControlGame;
import ui.SPGame;
import ui.ScoreScreen;

/**
 * Class used to represent that player character.
 * @author Connor Stewart
 */
public class Player extends Entity {
	
	/** The amount of speed (pixels per second) to lose each second. */
	protected final double DRAG = 5;
	
	/** The maximum speed the player can travel at. */
	protected final double MAX_SPEED = 15;
	
	/** The maximum amount of health the player can have. */
	protected final static int MAX_HEALTH= 20;
	
	/** The players current speed. */
	protected static int speed = 20;
	
	/** The amount of x pixels the player is moving per second. */
	protected double xDelta = 0;
	
	/** The amount of y pixels the player is moving per second. */
	protected double yDelta = 0;
	
	protected Weapon leftWeapon;
	
	protected Weapon rightWeapon;

	/**
	 * Create a player object.
	 * @param x the players starting x coordinate
	 * @param y the players starting y coordinate
	 */
	public Player(float x, float y) {
		super("misc/ship.png", MAX_HEALTH, speed);
		setSize(3, 3);
		setPosition(x, y);
		setOriginCenter();
		
		leftWeapon = new PlayerLightWeapon();
		rightWeapon = new PlayerHeavyWeapon();
	}

	/**
	 * Handles the players firing.
	 * @param delta the time since the last frame was rendered.
	 * @return null if the validation isn't met or a projectile object to be fired
	 */
	public Projectile fire(float delta) {
		//if the lmb is pressed and the light weapon is above or equal to the cooldown time
		if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
			return leftWeapon.fire(getCenterX(), getCenterY(), getRotation());
		
		//if the R is pressed and the heavy weapon is above or equal to the cooldown time
		if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
			return rightWeapon.fire(getCenterX(), getCenterY(), getRotation());
			
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
			//if colliding with a projectile thats not the players
			if (!((Projectile) collidedWith).getType().equals(ProjectileType.PLAYER)) {
				reduceHealth(((Projectile) collidedWith).getDamage());
			}
		}
		
		if (getHealth() <= 0)
			return true;
		
		return false;
	}

	@Override
	public void onDestroy() {
		ControlGame.getInstance().setScreen(new ScoreScreen(SPGame.getInstance().getScore()));
	}

	@Override
	public void update(float delta) {
		leftWeapon.update(delta);
		rightWeapon.update(delta);
		
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
		
		float maxHeight = SPGame.GAME_HEIGHT;
		float maxWidth = SPGame.GAME_WIDTH;
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

	public Weapon getLeftWeapon() {
		return leftWeapon;
	}

	public void setLeftWeapon(Weapon toSet) {
		leftWeapon = toSet;
	}
	
	public Weapon getRightWeapon() {
		return rightWeapon;
	}

	public void setRightWeapon(Weapon rightWeapon) {
		this.rightWeapon = rightWeapon;
	}


}