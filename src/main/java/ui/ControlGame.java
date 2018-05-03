package ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

/**
 * This class is used to control which screen is being displayed and dispose of old screens.
 */
public class ControlGame extends Game {

    /** The singleton instance of this class. */
    private static ControlGame instance = new ControlGame();

    /** The screen that is currently being shown. */
    private static Screen currentScreen;

    public void create() {
        setScreen(new MenuScreen());
    }

    public void render() {
        getScreen().render(Gdx.graphics.getDeltaTime());
    }

    public void dispose() {
        getScreen().dispose();
    }

    @Override
    public void setScreen(Screen screen) {
        if (currentScreen != null)
            currentScreen.dispose();

        super.setScreen(screen);
        currentScreen = screen;
    }

    /**
     * @return the singleton instance of this class
     */
    public static ControlGame getInstance() {
        return instance;
    }

}
