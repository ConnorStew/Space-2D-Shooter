package backend.entities;

import backend.projectiles.Ball;
import backend.projectiles.Missile;
import backend.projectiles.Projectile;
import backend.projectiles.ProjectileType;
import networking.server.ServerGame;

/**
 * A MultiplayerPlayer is a player in a multiplayer game.
 * @author Connor Stewart
 */
public class MultiplayerPlayer extends Player {
	
	/** The players nickname. */
	private String playerName;
	
	/** Whether this player should fire. */
	private boolean shouldFire;
	
	/** How many players this player has killed. */
	private int kills;

	public MultiplayerPlayer(float x, float y, String playerName) {
		super(x, y);
		this.playerName = playerName;
	}
	
	public MultiplayerPlayer(float x, float y, String playerName, int id) {
		super(x, y, id);
		this.playerName = playerName;
	}

	@Override
	public void update(float delta) {
		lightTimer += delta;
		heavyTimer += delta;
		
		//apply drag
		if (xDelta > 0)
			xDelta -= (DRAG * delta);
		
		if (xDelta < 0)
			xDelta += (DRAG * delta);
		
		if (yDelta > 0)
			yDelta -= (DRAG * delta);
		
		if (yDelta < 0)
			yDelta += (DRAG * delta);
		
		float maxHeight = ServerGame.GAME_HEIGHT;
		float maxWidth = ServerGame.GAME_WIDTH;
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
	
	/**
	 * Handles the players firing in multiplayer.
	 * @param delta the time since the last frame was rendered.
	 * @param type the type of projectile to fire
	 * @param id 
	 * @return null if the validation isn't met or a projectile object to be fired
	 */
	public Projectile fire(float delta, String type, ProjectileType pType, int id) {
		if (type.equals("Light") && canFireLight()) {
			lightTimer = 0;
			return new Ball(getCenterX(), getCenterY(), getRotation(), id, pType, getMultiplayerID());
		}
		
		if (type.equals("Heavy") && canFireHeavy()) {
			heavyTimer = 0;
			return new Missile(getCenterX(), getCenterY(), getRotation(), id, pType, getMultiplayerID());
		}
		
		return null; //return nothing if the validation is not passed
	}
	
	public void moveUp(float delta) {
		if (yDelta < MAX_SPEED)
			yDelta += (speed * delta);
	}
	
	public void moveDown(float delta) {
		if (yDelta > -MAX_SPEED)
			yDelta -= (speed * delta);
	}
	
	public void moveLeft(float delta) {
		if (xDelta > -MAX_SPEED)
			xDelta -= (speed * delta);
	}
	
	public void moveRight(float delta) {
		if (xDelta < MAX_SPEED)
			xDelta += (speed * delta);
	}

	public String getPlayerName() {
		return playerName;
	}

	public boolean shouldFire() {
		return shouldFire;
	}

	public void setShouldFire(boolean shouldFire) {
		this.shouldFire = shouldFire;
	}

	public boolean canFireLight() {
		return lightTimer >= LIGHT_CD;
	}
	
	public boolean canFireHeavy() {
		return heavyTimer >= HEAVY_CD;
	}

	public void resetHealth() {
		health = MAX_HEALTH;
	}

	public int getKills() {
		return kills;
	}

	public void incrementKills() {
		kills++;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

}
