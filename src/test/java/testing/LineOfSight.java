package testing;

import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import backend.entities.Entity;
import backend.entities.Player;
import backend.projectiles.Projectile;

public class LineOfSight implements ApplicationListener  {
	
	private Player player;
	
	private BitmapFont font;
	
	/** Used to render the entities. */
	private SpriteBatch batch;
	
	/** The camera to render the game. */
	private OrthographicCamera cam;
	
	private TestRunner toTest;
	
	private ShapeRenderer sr;
	
	private CopyOnWriteArrayList<Entity> activeEntities;
	
	@Override
	public void create() {
		player = new Player(0,0);
		toTest = new TestRunner(10,10);
		sr = new ShapeRenderer();
		//instantiate sprite batch
		batch = new SpriteBatch();
		
		//instantiate camera
		cam = new OrthographicCamera(30, 30);
		cam.position.set(player.getX(), player.getY(), 0);
		cam.zoom = 2;
		
		font = new BitmapFont();
		font.getData().setScale(0.1f);
		font.setUseIntegerPositions(false);
	}

	@Override
	public void resize(int width, int height) {}

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
		
		//MATH	
		
		//player position
		float px = player.getCenterX();
		float py = player.getCenterY();
		
		//test position
		float tx = toTest.getCenterX();
		float ty = toTest.getCenterY();
		
		//the maximum distance the player can detect an enemy at
		float viewDistance = 10;
		
		//the point at the end of the line of sight
		Vector2 los = getPointOnLine(viewDistance, px, py, mousePos.x, mousePos.y);
		
		batch.begin();
		
		toTest.draw(batch);
		player.draw(batch);
		
		//line between the player and enemy
		writeTextBetweenPoints(String.valueOf(Math.round(player.distanceBetween(toTest))), px, py, tx, ty);
		
		//vision line in front of the player
		writeTextBetweenPoints(String.valueOf(Math.round(distanceBetween(px, py, los.x, los.y))), px, py, los.x, los.y);
		
		//line between enemy and los
		writeTextBetweenPoints(String.valueOf(Math.round(distanceBetween(tx, ty, los.x, los.y))), tx, ty, los.x, los.y);
		
		writeXY(px, py);
		writeXY(tx, ty);
		writeXY(los.x, los.y);
		
		float viewAngle = Math.round(Math.toDegrees(Math.atan2(los.y-py, los.x-px) - Math.atan2(ty-los.y, tx-los.x)));
		
		if (viewAngle < 260 && viewAngle > -260 || viewAngle < 75 && viewAngle > -75)
			System.out.println("Can see.");
		else
			System.out.println("Nope");
		font.draw(batch, String.valueOf(viewAngle), 10, 10);
		
		batch.end();
		
		sr.begin();
		
		//line between the player and enemy
		sr.setColor(Color.GREEN);
		sr.line(px, py, tx, ty);
		
		//vision line in front of the player
		sr.setColor(Color.BLUE);
		sr.line(px, py, los.x, los.y);
		
		//line between enemy and los
		sr.setColor(Color.RED);
		sr.line(tx, ty, los.x, los.y);
		
		//font.draw(batch, str, x, y)
		
		sr.end();
	}
	
	private void writeTextBetweenPoints(String toWrite, float x1, float y1, float x2, float y2) {
		float fontX = (float) x1 - ((x1 - x2) / 2);
		float fontY = (float) y1 - ((y1 - y2) / 2);

		font.draw(batch, toWrite, fontX, fontY);
	}
	
	private void writeXY(float x, float y) {
		font.draw(batch, "x:" + Math.round(x) + ", y:" + Math.round(y) , x, y);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {

	}
	
	/**
	 * Checks for user input and reacts accordingly.
	 * @param delta the time since the last frame was rendered
	 */
	private void checkInput(float delta) {
		Projectile potentialProjectile = player.fire(delta);
		if (potentialProjectile != null)
			activeEntities.add(potentialProjectile);
	}
	

	/**
	 * Gets the distance between two points using the distance formula.
	 * @param x1 the first x coordinate
	 * @param y1 the first y coordinate
	 * @param x2 the second x coordinate
	 * @param y2 the second y coordinate
	 * @return the distance between the first and second points
	 */
	public static double distanceBetween(float x1, float y1, float x2, float y2) {
		return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
	}
		
	/**
	 * Gets a point a certain distance from x1/y1 to x2/y2.
	 * @param distance the amount of pixels along the line to get the point of
	 * @param x1 the first x coordinate
	 * @param y1 the first y coordinate
	 * @param x2 the second x coordinate
	 * @param y2 the second y coordinate
	 * @return the point the required distance away from x1/y1
	 */
	public static Vector2 getPointOnLine(float distance, float x1, float y1, float x2, float y2) {
			
		float distanceRatio = (float) (distance / distanceBetween(x1, y1, x2, y2));
			
		float xCoord = ((((1-distanceRatio)* x1) + (distanceRatio * x2)));
		float yCoord = ((((1-distanceRatio)* y1) + (distanceRatio * y2)));
			
		return new Vector2(xCoord, yCoord);
	}

}
