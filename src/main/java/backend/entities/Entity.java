package backend.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

/**
 * Used to represent an entity that can interact with other entities.
 * @author Connor Stewart
 */
public abstract class Entity extends InanimateEntity {
	
	/** The speed of this entity. */
	protected double speed;
	
	/** The health of this entity. */
	protected double health;
	
	/** Whether this entity has health. */
	private boolean hasHealth = true;
	
	/** The maximum health of this entity. */
	private double maxHealth;
	
	/** The id of this entity for use in a multiplayer game. */
	private int multiplayerID;
	
	/** The base speed of this entity. */
	public final double DEFAULT_SPEED;

	/**
	 * Creates a new instance of entity. <br>
	 * If this maxHealth parameter is set to zero this entity will have no health system.
	 * @param imageLocation the path to the image file for this entity
	 * @param maxHealth the maximum health of this entity
	 * @param speed the speed of this entity (pixels per second)
	 */
	public Entity(String imageLocation, double maxHealth, double speed) { 
		super(imageLocation);
		DEFAULT_SPEED = speed;
		health = maxHealth;
		this.speed = speed;
		if (maxHealth != 0)
			this.maxHealth = maxHealth;
		else
			hasHealth = false;
	}
	
	/**
	 * Called when this entity collides with another entity.
	 * @param collidedWith the entity the enemy collided with
	 * @return whether this entity should be destroyed
	 */
	public abstract boolean onCollision(Entity collidedWith);
	
	/**
	 * Called when this entity is removed from the game.
	 */
	public abstract void onDestroy();

	/**
	 * Moves this entity.
	 * @param delta the time since the last frame was rendered
	 */
	public abstract void update(float delta);
	
	/**
	 * Moves the entity forward
	 * @param pixels the amount of pixels to move the entity by
	 */
	public void moveForward(double pixels) {
		translateX((float) (Math.cos(Math.toRadians(getRotation())) * pixels));
		translateY((float) (Math.sin(Math.toRadians(getRotation())) * pixels));
	}
	
	/**
	 * Rotates this entity towards an entity.
	 * @param target the entity to face towards
	 */
	public void rotateTowards(Entity target) {
		rotateTowards(target.getCenterX(), target.getCenterY());
	}
	
	/** 
	 * Moves towards an entity at this entities speed.
	 * @param target the entity to move towards
	 */
	protected void moveTowards(Entity target, float delta) {
		rotateTowards(target);
		moveForward(speed * delta);
	}

	/**
	 * Rotates this entity towards a set of coordinates.
	 * @param target the entity to face towards
	 */
	public void rotateTowards(float targetX, float targetY) {
		double xDistance = getCenterX() - targetX;
		double yDistance = getCenterY() - targetY;
		double tanc = yDistance / xDistance;
		double angle = Math.toDegrees(Math.atan(tanc));
		
		//tan only goes to 180 so reverse the angle when its on the left hand side
		if (xDistance > 0)
			setRotation((float) angle - 180);
		else
			setRotation((float) angle);
		
		//set the entity back to their position for rotation
		setOriginCenter();
	}
	
	/**
	 * Draw a health bar under this entity.
	 * @param sr the ShapeRenderer thats rending this entity
	 * @param cam camera thats rendering this entity
	 */
	public void drawHP(ShapeRenderer sr, OrthographicCamera cam) {
		//get the position of this entity relative to the camera thats rendering it
		Vector3 entityPos = new Vector3(getX(), getY(), 0);
		
		//get the percentage of current health left
		double percentage = health / maxHealth;
		
		sr.setProjectionMatrix(cam.combined);
		
		float width = getWidth() * 1.5f;
		float height = 0.5f;
		float depth = 0;
		float xPos = entityPos.x - 0.5f;
		float yPos = entityPos.y - 1;
		float zPos = 0;
		
		//draw the outline
		sr.setColor(Color.RED);
		sr.box(xPos, yPos, zPos, (float) width, height, depth);
		
		//draw the health bar
		sr.setColor(Color.GREEN);
		sr.box(xPos, yPos, zPos, (float) (width * percentage), height, depth);
	}
	
	/**
	 * Sets the entities health.
	 * @param newHealth the new health
	 */
	public void setHealth(double newHealth) {
		health = newHealth;
	}

	/**
	 * @return the current health of this entity
	 */
	public double getHealth() {
		return health;
	}
	
	/**
	 * @return whether this enemy has a health system
	 */
	public boolean hasHealth() {
		return hasHealth;
	}
	
	/**
	 * Reduces this entities health.
	 * @param reduction the amount to reduce this entities health by
	 */
	public void reduceHealth(double reduction) {
		health -= reduction;
	}
	
	/**
	 * Reduces this entities speed.
	 * @param reduction the amount to reduce this entities speed by
	 */
	public void reduceSpeed(double reduction) {
		speed -= reduction;
	}
	
	/**
	 * Sets this entities speed to its default.
	 */
	public void resetSpeed() {
		speed = DEFAULT_SPEED;
	}

	public double getSpeed() {
		return speed;
	}
	
	/**
	 * Gets the distance between this entity and the target.
	 * @param target the target entity.
	 */
	public double distanceBetween(Entity target) {
		float myX = getCenterX();
		float myY = getCenterY();
		
		float targetX = target.getCenterX();
		float targetY = target.getCenterY();
		
		//using distance formula to get the distance
		double distance = Math.sqrt(Math.pow((targetX - myX), 2) + Math.pow((targetY - myY), 2));
		
		return distance;
	}
	
	public int getMultiplayerID() {
		return multiplayerID;
	}

	public void setMultiplayerID(int id) {
		multiplayerID = id;
	}
	
}
