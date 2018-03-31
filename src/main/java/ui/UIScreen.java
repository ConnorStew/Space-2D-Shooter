package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.StretchViewport;

/**
 * This class is used as a base for menu screens.
 */
abstract public class UIScreen extends BaseScreen {

    /** The stage to display elements. */
    Stage stage;

    /** Button cooldown in milliseconds. */
    private final double BUTTON_COOLDOWN = 0.5;

    /** The time since a button was pressed. */
    private double timeSinceButtonPressed = BUTTON_COOLDOWN;

    public void show() {
        super.show();

        //initialising the stage which will stretch
        stage = new Stage(new StretchViewport(900, 700));

        //allowing the stage to receive input events
        Gdx.input.setInputProcessor(stage);
    }

    public void render(float delta) {

        timeSinceButtonPressed = timeSinceButtonPressed + delta;

        Gdx.gl.glClearColor(0.2f, 0.2f, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta); //update actors
        stage.draw(); //draw actors
    }

    boolean validateButtonPress() {
        if (timeSinceButtonPressed > BUTTON_COOLDOWN) {
            timeSinceButtonPressed = 0;
            return true;
        } else {
            return false;
        }
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
    }
}