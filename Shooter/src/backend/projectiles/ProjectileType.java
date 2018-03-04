package backend.projectiles;

/**
 * An enumeration to identify who fired this projectile.
 * @author 549601
 *
 */
public enum ProjectileType {
	
	/* A projectile that was fired by the player. */
	PLAYER(), 
	
	/** A projectile that was fired by an enemy. */
	ENEMEY(),
	
	/** A projectile filed by a player in multiplayer. */
	PVP();
	
}