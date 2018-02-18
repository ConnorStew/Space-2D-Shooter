package backend.logic;

import java.awt.Point;
import java.util.Random;

import com.badlogic.gdx.math.Rectangle;

import backend.Engine;
import backend.SinglePlayerEngine;
import backend.enemies.Asteroid;
import backend.enemies.Dropship;
import backend.enemies.Laser;
import backend.enemies.Runner;
import backend.entities.Entity;
import backend.pickups.Health;
import backend.pickups.Time;

/**
 * Spawns enemies and powerups.
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
	private float dropshipSpawnTimer = DROPSHIP_SPAWN_INTERVAL - 10; //first spawn 10 seconds after the game starts
	
	/** Time in between laser spawning. */
	private final static float LASER_SPAWN_INTERVAL = 15;
	
	/** The time since a laser has spawned. */
	private float laserSpawnTimer = 0;
	
	/** Time in between a pickup spawning. */
	private final static float PICKUP_SPAWN_INTERVAL = 35;
	
	/** The time since a pickup has spawned. */
	private float pickupSpawnTimer = PICKUP_SPAWN_INTERVAL - 5; //a pickup spawns 5 seconds after the game starts
	
	/** Random object to generate spawn points. */
	private static final Random RND = new Random();
	
	private SinglePlayerEngine engine;
	
	public Spawner(SinglePlayerEngine engine) {
		this.engine = engine;
	}
	
	/**
	 * Spawns enemies based on spawn timers.
	 * @param delta the time since the last frame was rendered
	 */
	public void spawnEnemies(float delta) {
		spawnRunner(delta);
		spawnAsteroid(delta);
		spawnDropship(delta);
		spawnLaser(delta);
		spawnPickup(delta);
	}

	/**
	 * Generates a point for the enemy to spawn on.
	 * @return the point for the enemy to spawn on
	 */
	private Point getEnemySpawnLocation() {
		int x = 0;
		int y = 0;
		int maxHeight = Engine.GAME_HEIGHT;
		int maxWidth = Engine.GAME_WIDTH;

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
	 * Gets a random location on the map to spawn a pickup.
	 * @return the point on the map to spawn a pickup on
	 */
	private Point getPickupSpawnLocation() {
		boolean overlapping = false;
		
		int x = 0;
		int y = 0;
		
		int maxHeight = SinglePlayerEngine.GAME_HEIGHT;
		int maxWidth = SinglePlayerEngine.GAME_WIDTH;
		
		int pickupWidth = 50;
		int pickupHeight = 50;
		
		//loop until the new spawn location does not overlap with other pickups
		do {
			x = RND.nextInt(maxWidth); //pick spawn x
			y = RND.nextInt(maxHeight); //pick spawn y
			
			//validate that the pickup is visible on screen
			if (x < pickupWidth)
				x = pickupWidth;
			
			if (x > maxWidth - pickupWidth)
				x = maxWidth - pickupWidth;
			
			if (y < pickupHeight)
				x = pickupHeight;
			
			if (y > maxHeight - pickupHeight)
				y = maxHeight - pickupHeight;
			
			//make sure the pickup hasen't spawned on another pickup
			for (Entity entity : engine.getActiveEntities()) {
				if (entity.getBoundingRectangle().overlaps(new Rectangle(x, y, pickupWidth, pickupHeight))) {
					overlapping = true;
				} else {
					overlapping = false;
				}
			}
		} while ((overlapping == true));
		
		return new Point(x,y);
	}
	
	/**
	 * Spawns a pickup if off cooldown.
	 * @param delta the time since the last update
	 */
	private void spawnPickup(float delta) {
		pickupSpawnTimer += delta;
		
		if (pickupSpawnTimer >= PICKUP_SPAWN_INTERVAL) {
			pickupSpawnTimer = 0;
			
			Point spawnLoc = getPickupSpawnLocation();
			
			switch (RND.nextInt(2)) {
				case 0:
					 engine.addEntity(new Health(spawnLoc.x, spawnLoc.y, engine));
					return;
				case 1:
					 engine.addEntity(new Time(spawnLoc.x, spawnLoc.y, engine));
					return;					
			}
			
		}
		
	}

	
	/**
	 * Spawn a laser.
	 * @param delta the time since the last frame was rendered
	 */
	private void spawnLaser(float delta) {
		laserSpawnTimer += delta;
		
		if (laserSpawnTimer >= LASER_SPAWN_INTERVAL) {
			laserSpawnTimer = 0;
			
			Point spawnLoc = getEnemySpawnLocation();
			engine.addEntity(new Laser(spawnLoc.x, spawnLoc.y, engine));
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
			
			Point spawnLoc = getEnemySpawnLocation();
			 engine.addEntity(new Dropship(spawnLoc.x, spawnLoc.y, engine));
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

			Point spawnLoc = getEnemySpawnLocation();
			 engine.addEntity(new Runner(spawnLoc.x, spawnLoc.y, engine));
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

			Point spawnLoc = getEnemySpawnLocation();
			
			int maxHeight = SinglePlayerEngine.GAME_HEIGHT;
			int maxWidth = SinglePlayerEngine.GAME_WIDTH;
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
			
			Asteroid toAdd = new Asteroid(spawnLoc.x, spawnLoc.y, engine);
			toAdd.rotate(rotation);
			
			 engine.addEntity(toAdd);
		}
		
	}
	
}

