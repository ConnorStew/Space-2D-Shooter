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
		super(x, y);
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
	
	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Projectile) {
			Projectile projectile = (Projectile) collidedWith;
			//if colliding with a projectile thats not the players and is not fired by this player
			if (!projectile.getType().equals(ProjectileType.PLAYER) && projectile.getFiredByID() != getMultiplayerID())
				reduceHealth(projectile.getDamage());
		}
		
		if (getHealth() <= 0)
			return true;
		
		return false;
	}

}
