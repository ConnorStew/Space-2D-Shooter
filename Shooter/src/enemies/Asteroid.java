package enemies;

import entities.Entity;
import entities.Player;

class Asteroid extends Enemy {

	Asteroid(float x, float y) {
		super(x, y, 0, 15, 5, 0, "asteroid.png");
		System.out.println("testing");
	}

	@Override
	public void onDestroy() {}

	@Override
	public void update(float delta) {
		moveForward(speed * delta);
	}

	@Override
	public boolean onCollision(Entity collidedWith) {
		if (collidedWith instanceof Player)
			return true;
		else
			return false;
	}

}
