package backend.projectiles;

/**
 * A fast moving low damage projectile.
 * @author Connor Stewart
 */
public class Ball extends Projectile {
	
	/** The amount of damage this projectile does. */
	private static final int DAMAGE = 2;
	
	/** The amount of pixels per seconds this laser moves at. */
	private static final int SPEED = 20;
	
	/** The size of the laser. */
	private static final int SIZE = 1;
	
	public Ball(float x, float y, float r) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "laserBeam.png", ProjectileType.ENEMEY);
	}

	public Ball(float x, float y, float r, int id, int firedByID) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "laserBeam.png", ProjectileType.ENEMEY, id, firedByID);
	}

	public Ball(float x, float y, float r, int id, ProjectileType pType, int firedByID) {
		super(x, y, r, DAMAGE, SPEED, SIZE, "laserBeam.png", pType, id, firedByID);
	}

	@Override
	public void onDestroy() {}


}
