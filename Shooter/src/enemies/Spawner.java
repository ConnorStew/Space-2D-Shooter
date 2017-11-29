package enemies;

import java.util.Random;

import ui.GameScreen;

/**
 * Spawns enemies.
 * @author Connor Stewart
 */
public class Spawner {
	
	/** Time in between orbs spawning. */
	private final float orbSpawnInterval = 2;
	
	/** The time since an orb has spawned. */
	private float orbSpawnTimer = 0;

	/** Time in between asteroids spawning. */
	private final float asteroidSpawnInterval = 1;
	
	/** The time since an asteroid has spawned. */
	private float asteroidSpawnTimer = 0;
	
	/** Time in between dropship spawning. */
	private final float dropshipSpawnInterval = 10;
	
	/** The time since a dropship has spawned. */
	private float dropshipSpawnTimer = 0;
	
	/** Random object to generate spawn points. */
	private static final Random rnd = new Random();
	
	/**
	 * Spawn an enemy based on spawn timers.
	 * @param delta the time since the last frame was rendered
	 * @param activeEntities the entities active in the game
	 */
	public void spawnEnemy(float delta) {
		spawnOrb(delta);
		spawnAsteroid(delta);
		spawnDropship(delta);
	}
	
	private void spawnDropship(float delta) {
		dropshipSpawnTimer += delta;
		
		System.out.println("Dropship spawn timer :" + asteroidSpawnTimer);
		
		if (dropshipSpawnTimer >= dropshipSpawnInterval) {
			dropshipSpawnTimer = 0;
			
			int x = 0;
			int y = 0;
			int maxHeight = (int) GameScreen.getMapHeight();
			int maxWidth = (int) GameScreen.getMapWidth();

			//pick a side to spawn on
			switch(rnd.nextInt(4)) {
				case 0: //left
					x = 0;
					y = rnd.nextInt(maxHeight);
					break;
				case 1: //right
					x = rnd.nextInt(maxWidth);
					y = maxWidth;
					break;
				case 2: //top
					x = rnd.nextInt(maxWidth);
					y = maxHeight;
					break;
				case 3: //bottom
					x = rnd.nextInt(maxWidth);
					y = 0;
					break;
			}
			
			GameScreen.addEntity(new Dropship(x, y));
		}
		
	}

	private void spawnAsteroid(float delta) {
		asteroidSpawnTimer += delta;
		
		System.out.println("Asteroid spawn timer :" + asteroidSpawnTimer);
		
		if (asteroidSpawnTimer >= asteroidSpawnInterval) {
			asteroidSpawnTimer = 0;
			
			int x = 0;
			int y = 0;
			int maxHeight = (int) GameScreen.getMapHeight();
			int maxWidth = (int) GameScreen.getMapWidth();
			float rotation = 0;
			
			//pick a side to spawn on
			switch(rnd.nextInt(4)) {
				case 0: //left
					x = 0;
					y = rnd.nextInt(maxHeight);
					break;
				case 1: //right
					x = rnd.nextInt(maxWidth);
					y = maxWidth;
					rotation = -180;
					break;
				case 2: //top
					x = rnd.nextInt(maxWidth);
					y = maxHeight;
					rotation = -90;
					break;
				case 3: //bottom
					x = rnd.nextInt(maxWidth);
					y = 0;
					rotation = 90;
					break;
			}
			
			Asteroid toAdd = new Asteroid(x, y);
			toAdd.rotate(rotation);
			
			GameScreen.addEntity(toAdd);
		}
		
	}
	
	private void spawnOrb(float delta) {
		orbSpawnTimer += delta;
		
		System.out.println("Orb spawn timer :" + orbSpawnTimer);
		
		int x = 0;
		int y = 0;
		int maxHeight = (int) GameScreen.getMapHeight();
		int maxWidth = (int) GameScreen.getMapWidth();
		
		//spawn
		if (orbSpawnTimer >= orbSpawnInterval) {
			orbSpawnTimer = 0;
			
			//pick a side to spawn on
			switch(rnd.nextInt(4)) {
				case 0: //left
					x = 0;
					y = rnd.nextInt(maxHeight);
					break;
				case 1: //right
					x = rnd.nextInt(maxWidth);
					y = maxWidth;
					break;
				case 2: //top
					x = rnd.nextInt(maxWidth);
					y = maxHeight;
					break;
				case 3: //bottom
					x = rnd.nextInt(maxWidth);
					y = 0;
					break;
			}
			
			GameScreen.addEntity(new Orb(x, y));
		}
	}
	
}

