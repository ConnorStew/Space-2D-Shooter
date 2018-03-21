package backend.weapons;

import backend.projectiles.Beam;
import backend.projectiles.Projectile;

/**
 * This weapon is used by the players left fire in single player.
 * @author Connor Stewart
 */
public class PlayerLightWeapon extends Weapon {

	public PlayerLightWeapon() {
		super(0.3f);
	}

	@Override
	protected Projectile getProjectile(float x, float y, float r) {
		return new Beam(x, y, r);
	}

}
