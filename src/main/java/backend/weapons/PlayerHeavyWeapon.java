package backend.weapons;

import backend.projectiles.Missile;
import backend.projectiles.Projectile;

/**
 * This weapon is used by the players right fire in single player.
 * @author Connor Stewart
 */
public class PlayerHeavyWeapon extends Weapon {

	public PlayerHeavyWeapon() {
		super(1.5f);
	}

	@Override
	protected Projectile getProjectile(float x, float y, float r) {
		return new Missile(x, y, r);
	}

}
