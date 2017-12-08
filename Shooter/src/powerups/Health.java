package powerups;

import ui.GameScreen;

/**
 * This powerup heals the player.
 * @author Connor Stewart
 */
public class Health extends Powerup {
	
	/** The amount to heal the player by when they pick this powerup up. */
	private int healAmount = 5;

	public Health(int x, int y) {
		super("powerups/health.png");
		setScale(0.05f);
		
		setPosition(x, y);
	}

	@Override
	public void onDestroy() {}


	@Override
	void effect() {
		GameScreen.getPlayer().heal(healAmount);
	}

}
