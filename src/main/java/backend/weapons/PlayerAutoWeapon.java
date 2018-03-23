package backend.weapons;

import backend.projectiles.LockOn;
import backend.projectiles.Projectile;

/**
 * Weapon for use with the {@link effects.BetterWeapon}
 * @author Connor Stewart
 */
public class PlayerAutoWeapon extends Weapon {

	public PlayerAutoWeapon() {
		super(0.05f);
	}

	@Override
	protected Projectile getProjectile(float x, float y, float r) {
		return new LockOn(x, y, r);
	}

}
