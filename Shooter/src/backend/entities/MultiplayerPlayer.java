package backend.entities;

import backend.Engine;

/**
 * A MultiplayerPlayer is a player in a multiplayer game.
 * @author Connor Stewart
 */
public class MultiplayerPlayer extends Player {
	
	/** The players nickname. */
	private String playerName;

	public MultiplayerPlayer(float x, float y, Engine engine, String playerName) {
		super(x, y, engine);
		this.playerName = playerName;
	}
	
	@Override
	public void update(float delta) {
		//apply drag
		if (xDelta > 0)
			xDelta -= (DRAG * delta);
		
		if (xDelta < 0)
			xDelta += (DRAG * delta);
		
		if (yDelta > 0)
			yDelta -= (DRAG * delta);
		
		if (yDelta < 0)
			yDelta += (DRAG * delta);
		
		float maxHeight = Engine.GAME_HEIGHT;
		float maxWidth = Engine.GAME_WIDTH;
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

}
