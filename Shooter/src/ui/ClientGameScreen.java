package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;

import backend.ClientEngine;
import backend.entities.Entity;
import backend.entities.InanimateEntity;
import backend.entities.MultiplayerPlayer;
import networking.client.Client;

public class ClientGameScreen implements Screen {
	
	/** Font used to display score. */
	private BitmapFont font;

	/** Used to render the sprites/entities. */
	private SpriteBatch batch;
	
	/** Shape renderer used to render health bars. */
	private ShapeRenderer sr;
	
	/** The camera to render the game. */
	private OrthographicCamera cam;
	
	/** The background image. */
	private InanimateEntity map;
	
	/** The engine this screen is displaying. */
	private ClientEngine engine;
	
	public ClientGameScreen(Client client, ClientEngine engine) {
		this.engine = engine;
		engine.setPlayer(new MultiplayerPlayer(50, 50, engine, client.getNickname()));
	}

	@Override
	public void show() {
		//instantiate map
		map = new InanimateEntity("redPlanet.png", 100, 100);
				
		//instantiate font for the score
		font = new BitmapFont();
		font.getData().setScale(0.2f);
		font.setUseIntegerPositions(false);
		
		//instantiate shape renderer
		sr = new ShapeRenderer();
		
		//instantiate sprite batch
		batch = new SpriteBatch();
		
		//instantiate camera
		cam = new OrthographicCamera(30, 30);
		cam.position.set(engine.getPlayer().getX(), engine.getPlayer().getY(), 0);
		cam.zoom = 2;
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		engine.update(delta);
		
		MultiplayerPlayer player = engine.getPlayer();
		
		//get the font coordinates according to the current camera position
		Vector3 fontCord = new Vector3(player.getCenterX(), player.getCenterY(), 0);
		cam.unproject(fontCord);
		

		//update camera
		cam.update();
		
		//the mouse position relative to the camera
		Vector3 mousePos = new Vector3(Gdx.input.getX(),Gdx.input.getY(),0);
		cam.unproject(mousePos);

		//set the camera as the view
		batch.setProjectionMatrix(cam.combined);
		
		//rotate the player towards the mouse
		player.rotateTowards(mousePos.x, mousePos.y);
		player.setRotation(player.getRotation() - 90); //-90 due to how the player sprite is drawn

		//validate camera movement
		if (player.getCenterY() - cam.viewportHeight > 0 && player.getCenterY() + cam.viewportHeight < map.getHeight())
			cam.position.y = player.getCenterY();
		
		if (player.getCenterX() - cam.viewportWidth > 0 && player.getCenterX() + cam.viewportWidth < map.getWidth())
			cam.position.x = player.getCenterX();
		
		//start drawing sprites
		batch.begin();
		
		//draw background
		map.draw(batch);
		
		font.draw(batch, player.getPlayerName(), player.getCenterX(), player.getCenterY());

		//draw 
		for (Entity entity : engine.getActiveEntities())
			entity.draw(batch);
		
		//stop drawing sprites
		batch.end();
		
		//start drawing shapes
		sr.begin(ShapeRenderer.ShapeType.Filled);
		
		//draw health bars
		for (Entity entity : engine.getActiveEntities())
			if (entity.hasHealth())
				entity.drawHP(sr, cam); //draw health bar

		
		
		
		
		//stop drawing shapes
		sr.end();
	}

	@Override
	public void resize(int width, int height) {}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {}

	@Override
	public void dispose() {}

}
