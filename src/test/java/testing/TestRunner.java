package testing;

import backend.enemies.Runner;

public class TestRunner extends Runner {
	
	public TestRunner(float x, float y) {
		super(x, y);
	}

	@Override
	public void update(float delta) {
		//dont move
	}

}
