package logic;

import java.awt.Point;
import java.util.Random;

import enemies.Asteroid;
import enemies.Dropship;
import enemies.Laser;
import enemies.Runner;
import ui.GameScreen;

/**
 * Spawns enemies.
 * @author Connor Stewart
 */
public class Spawner {
	
	/** Time in between orbs spawning. */
	private final static float RUNNER_SPAWN_INTERVAL = 5;
	
	/** The time since an orb has spawned. */
	private float runnerSpawnTimer = 0;

	/** Time in between asteroids spawning. */
	private final static float ASTEROID_SPAWN_INTERVAL = 1;
	
	/** The time since an asteroid has spawned. */
	private float asteroidSpawnTimer = 0;
	
	/** Time in between dropship spawning. */
	private final static float DROPSHIP_SPAWN_INTERVAL = 30;
	
	/** The time since a dropship has spawned. */
	private float dropshipSpawnTimer = 20; //first spawn 10 seconds after the game starts
	
	/** Time in between laser spawning. */
	private final static float LASER_SPAWN_INTERVAL = 10;
	
	/** The time since a laser has spawned. */
	private float laserSpawnTimer = 0;
	
	/** Random object to generate spawn points. */
	private static final Random RND = new Random();
	
	/**
	 * Spawns enemies based on spawn timers.
	 * @param delta the time since the last frame was rendered
	 */
	public void spawnEnemies(float delta) {
		spawnRunner(delta);
		spawnAsteroid(delta);
		spawnDropship(delta);
		spawnLaser(delta);
	}
	
	/**
	 * Generates a point for the enemy to spawn on.
	 * @return the point for the enemy to spawn on
	 */
	private Point getSpawnLocation() {
		int x = 0;
		int y = 0;
		int maxHeight = (int) GameScreen.getMapHeight();
		int maxWidth = (int) GameScreen.getMapWidth();

		//pick a side to spawn on
		switch(RND.nextInt(4)) {
			case 0: //left
				x = 0;
				y = RND.nextInt(maxHeight);
				break;
			case 1: //right
				x = RND.nextInt(maxWidth);
				y = maxWidth;
				break;
			case 2: //top
				x = RND.nextInt(maxWidth);
				y = maxHeight;
				break;
			case 3: //bottom
				x = RND.nextInt(maxWidth);
				y = 0;
				break;
		}
		
		return new Point(x,y);
	}
	
	/**
	 * Spawn a laser.
	 * @param delta the time since the last frame was rendered
	 */
	private void spawnLaser(float delta) {
		laserSpawnTimer += delta;
		
		if (laserSpawnTimer >= LASER_SPAWN_INTERVAL) {
			laserSpawnTimer = 0;
			
			Point spawnLoc = getSpawnLocation();
			GameScreen.addEntity(new Laser(spawnLoc.x, spawnLoc.y));
		}
		
	}
	
	/**
	 * Spawn a dropship.
	 * @param delta the time since the last frame was rendered
	 */
	private void spawnDropship(float delta) {
		dropshipSpawnTimer += delta;
		
		if (dropshipSpawnTimer >= DROPSHIP_SPAWN_INTERVAL) {
			dropshipSpawnTimer = 0;
			
			Point spawnLoc = getSpawnLocation();
			GameScreen.addEntity(new Dropship(spawnLoc.x, spawnLoc.y));
		}
		
	}
	
	/**
	 * Spawn a runner.
	 * @param delta the time since the last frame was rendered
	 */
	private void spawnRunner(float delta) {
		runnerSpawnTimer += delta;

		if (runnerSpawnTimer >= RUNNER_SPAWN_INTERVAL) {
			runnerSpawnTimer = 0;

			Point spawnLoc = getSpawnLocation();
			GameScreen.addEntity(new Runner(spawnLoc.x, spawnLoc.y));
		}
	}

	/**
	 * Spawn an asteroid.
	 * @param delta the time since the last frame was rendered
	 */
	private void spawnAsteroid(float delta) {
		asteroidSpawnTimer += delta;
		
		if (asteroidSpawnTimer >= ASTEROID_SPAWN_INTERVAL) {
			asteroidSpawnTimer = 0;

			Point spawnLoc = getSpawnLocation();
			
			int maxHeight = (int) GameScreen.getMapHeight();
			int maxWidth = (int) GameScreen.getMapWidth();
			float rotation;
			
			if (spawnLoc.y == maxWidth) {
				rotation = -180;
			} else if (spawnLoc.y  == maxHeight) {
				rotation = -90;
			} else if (spawnLoc.y == 0) {
				rotation = 90;
			} else {
				rotation = 0;
			}
			
			Asteroid toAdd = new Asteroid(spawnLoc.x, spawnLoc.y);
			toAdd.rotate(rotation);
			
			GameScreen.addEntity(toAdd);
		}
		
	}
	
}

