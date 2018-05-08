package ui;

import backend.entities.InanimateEntity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * This is a generic class used to define rending a game.
 * @author Connor Stewart
 */
abstract public class GameScreen extends BaseScreen {

    /** Font used to display score. */
    BitmapFont font;

    /** Used to render the entities. */
    SpriteBatch batch;

    /** Shape renderer used to render health bars. */
    ShapeRenderer sr;

    /** The camera to render the game. */
    OrthographicCamera cam;

    /** The background image. */
    InanimateEntity map;

    public void show() {
        //instantiate shape renderer
        sr = new ShapeRenderer();

        //instantiate sprite batch
        batch = new SpriteBatch();

        //instantiate score font
        font = new BitmapFont();
        font.getData().setScale(0.2f);
        font.setUseIntegerPositions(false);

        //instantiate camera
        cam = new OrthographicCamera(30, 30);
        cam.zoom = 2;
    }

    public void render(float delta) {
        //clear the last frame that was rendered
        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cam.update();
    }

    public void resize(int width, int height) {
        cam.update();
    }

    public void dispose() {
        batch.dispose();
        sr.dispose();
        font.dispose();
    }
}
