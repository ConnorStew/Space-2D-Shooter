package testing;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Vector3;
import backend.entities.Entity;
import backend.entities.Player;
import backend.projectiles.Projectile;

public class RectangleLOS implements ApplicationListener {
	
private Player player;
	
	private BitmapFont font;
	
	private CopyOnWriteArrayList<Entity> activeEntities;
	
	/** Used to render the entities. */
	private SpriteBatch batch;
	
	/** The camera to render the game. */
	private OrthographicCamera cam;
	
	private TestRunner toTest;
	
	private ShapeRenderer sr;
	
	private static RectangleLOS instance;
	
	@Override
	public void create() {
		instance = this;
		player = new Player(0,0);
		toTest = new TestRunner(10,10);
		sr = new ShapeRenderer();
		//instantiate sprite batch
		batch = new SpriteBatch();
		activeEntities = new CopyOnWriteArrayList<Entity>();
		//instantiate camera
		cam = new OrthographicCamera(30, 30);
		cam.position.set(player.getX(), player.getY(), 0);
		cam.zoom = 2;
		
		font = new BitmapFont();
		font.getData().setScale(0.1f);
		font.setUseIntegerPositions(false);
	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void render() {
		float delta = Gdx.graphics.getDeltaTime();
		
		checkInput(delta);
		
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//set the camera as the view
		player.update(delta);
		cam.update();
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);
		
		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);
		
		//rotate the player towards the mouse
		player.rotateTowards(mousePos.x, mousePos.y);
		player.setRotation(player.getRotation() - 90); //-90 due to how the player sprite is drawn

		cam.position.y = player.getCenterY();
		cam.position.x = player.getCenterX();
		
		sr.setAutoShapeType(true);
		sr.setProjectionMatrix(cam.combined);
		
		//move entities
		for (Entity entity : activeEntities)
			entity.update(delta);
		
		//player position
		float px = player.getCenterX();
		float py = player.getCenterY();
		
		//test position
		float tx = toTest.getCenterX();
		float ty = toTest.getCenterY();
		
		
		batch.begin();
		
		for (Entity entity : activeEntities)
			entity.draw(batch);
		
		toTest.draw(batch);
		player.draw(batch);

		batch.end();
		
		int viewDistance = 10;
		int viewWidth = 5;
		
		float[] vertices = {px, py,px - viewWidth, py + viewDistance,px + viewWidth, py+ viewDistance};
		Polygon vision = new Polygon(vertices);
		vision.setRotation(player.getRotation());
		vision.setOrigin(px, py);
		sr.begin();
		
		sr.polygon(vision.getTransformedVertices());
		
		sr.end();
		
		if (vision.contains(tx, ty)) {
			System.out.println("Seen!");
		} else {
			System.out.println("Can't See!");
		}

	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void dispose() {}

	
	/**
	 * Checks for user input and reacts accordingly.
	 * @param delta the time since the last frame was rendered
	 */
	private void checkInput(float delta) {
		Projectile potentialProjectile = player.fire(delta);
		if (potentialProjectile != null)
			activeEntities.add(potentialProjectile);
	}

	public static RectangleLOS getInstance() {
		return instance;
	}
	


}
