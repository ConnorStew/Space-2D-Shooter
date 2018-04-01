package backend.entities;

import backend.projectiles.Projectile;
import backend.projectiles.ProjectileType;
import network.Network;

/**
 * A MultiplayerPlayer is a player in a multiplayer game.
 * @author Connor Stewart
 */
public class MultiplayerPlayer extends Player {
	
	/** The players nickname. */
	private String playerName;
	
	/** How many players this player has killed. */
	private int kills;

	public MultiplayerPlayer(float x, float y, String playerName) {
		super(x, y, null);
		this.playerName = playerName;
	}

	@Override
	public void update(float delta) {
		leftWeapon.update(delta);
		rightWeapon.update(delta);
		
		//apply drag
		if (xDelta > 0)
			xDelta -= (DRAG * delta);
		
		if (xDelta < 0)
			xDelta += (DRAG * delta);
		
		if (yDelta > 0)
			yDelta -= (DRAG * delta);
		
		if (yDelta < 0)
			yDelta += (DRAG * delta);
		
		float maxHeight = Network.GAME_HEIGHT;
		float maxWidth = Network.GAME_WIDTH;
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
	 * Increase this plays y delta.
	 * @param delta the time since the last frame was rendered
	 */
	public void moveUp(float delta) {
		if (yDelta < MAX_SPEED)
			yDelta += (speed * delta);
	}

    /**
     * Decreases this plays y delta.
     * @param delta the time since the last frame was rendered
     */
	public void moveDown(float delta) {
		if (yDelta > -MAX_SPEED)
			yDelta -= (speed * delta);
	}

    /**
     * Decreases this plays x delta.
     * @param delta the time since the last frame was rendered
     */
	public void moveLeft(float delta) {
		if (xDelta > -MAX_SPEED)
			xDelta -= (speed * delta);
	}

    /**
     * Increase this plays x delta.
     * @param delta the time since the last frame was rendered
     */
	public void moveRight(float delta) {
		if (xDelta < MAX_SPEED)
			xDelta += (speed * delta);
	}

    /**
     * Sets this players health to its maximum.
     */
    public void resetHealth() {
        health = MAX_HEALTH;
    }

    /**
     * Adds a kill to this players total kills.
     */
    public void incrementKills() {
        kills++;
    }

    /**
     * @return this players nickname used to identify them on the server
     */
	public String getPlayerName() {
		return playerName;
	}

    /**
     * @return the amount of other players this player has killed
     */
	public int getKills() {
		return kills;
	}

    /**
     * Sets the amount of kills this player has.
     * @param kills the amount of kills
     */
	public void setKills(int kills) {
		this.kills = kills;
	}
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Projectile) {
			Projectile projectile = (Projectile) collidedWith;
			//if colliding with a projectile that's not the players and is not fired by this player
			if (!projectile.getType().equals(ProjectileType.PLAYER) && projectile.getFiredByID() != getMultiplayerID())
				reduceHealth(projectile.getDamage());
		}
		
		return (getHealth() <= 0);
	}

}
