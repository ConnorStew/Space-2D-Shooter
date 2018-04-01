package backend.weapons;

import backend.projectiles.LockOn;
import backend.projectiles.Projectile;
import ui.SPGame;

/**
 * Weapon for use with the {@link backend.effects.BetterWeapon}
 * @author Connor Stewart
 */
public class PlayerAutoWeapon extends Weapon {

	private final SPGame screen;

	public PlayerAutoWeapon(SPGame screen) {
		super(0.05f);
		this.screen = screen;
	}

	@Override
	protected Projectile getProjectile(float x, float y, float r) {
		return new LockOn(x, y, r, screen);
	}

}
