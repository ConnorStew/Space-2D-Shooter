package effects;

import enemies.Dropship;
import entities.Entity;
import entities.Player;
import projectiles.Projectile;
import projectiles.ProjectileType;
import ui.GameScreen;

/**
 * This effect slows all enemies.
 * @author Connor Stewart
 */
public class Slow extends Effect {

	public Slow() {
		super(7); //ten second duration
	}
	
	@Override
	public void end() {
		//reset the speed of all enemies
		for (Entity e : GameScreen.getEntities()) {
			if (e.getSpeed() != e.DEFAULT_SPEED) { //only reset if the entities speed is not at default
				e.resetSpeed();
				if (e instanceof Dropship) { //double dropship spawn timers
					Dropship ds = (Dropship) e;
					
					//half the delay in between dropships spawning runners
					if (ds.getDelay() != Dropship.DEFAULT_DELAY) //if the delay is not at default
						ds.setDelay(Dropship.DEFAULT_DELAY); //reset it
				}
			}
				
		}
	}


	@Override
	public void update() {
		//slow all enemies
		for (Entity e : GameScreen.getEntities()) {
			if ((!(e instanceof Player ))) {//doesen't affect the player
				//dosen't affect player projectiles
				if (!(e instanceof Projectile && ((Projectile) e).getType().equals(ProjectileType.PLAYER))) {
					if (e.getSpeed() == e.DEFAULT_SPEED) { //only reset if the entities speed is at default
						e.reduceSpeed(e.DEFAULT_SPEED / 2);
					}
					if (e instanceof Dropship) { //double dropship spawn timers
						Dropship ds = (Dropship) e;
						
						//double the delay in between dropships spawning runners
						ds.setDelay(ds.getDelay() * 2);
					}
				}
			}

		}

	}

}
