package ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * This class is the superclass for all screens. <br>
 * This class was created to reduce the bloat of the pause, resume and hide Screen methods because they are not used.
 */
public abstract class BaseScreen implements Screen {

    /** The style for buttons within the game. */
    TextButton.TextButtonStyle buttonStyle;

    /** The style for labels within the game. */
    Label.LabelStyle labelStyle;

    /** The style for lists within the game. */
    List.ListStyle lstStyle;

    /** The style for text fields within the game. */
    TextField.TextFieldStyle tfs;

    /** The style for scroll panes within the game. */
    ScrollPane.ScrollPaneStyle scrStyle;

    /** The starTrekFont for use within the game. */
    BitmapFont starTrekFont;

    public void show() {
        loadFont();
        initialiseStyles();
    }

    /**
     * Loads the starTrekFont.
     */
    private void loadFont() {
        //load the starTrekFont
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Star Trek Enterprise Future.ttf"));

        //setting starTrekFont size
        FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        fontParameter.size = 100;

        //creating the starTrekFont based on the starTrekFont parameters
        starTrekFont = generator.generateFont(fontParameter);

        //dispose the generator since its finished being used
        generator.dispose();
    }

    /**
     * Loads the MyGame styles.
     */
    private void initialiseStyles() {
        //a sprite for a black background
        Sprite buttonBackground = new Sprite(new Texture(new Pixmap(0, 0, Pixmap.Format.RGB888)));
        buttonBackground.setColor(Color.WHITE);
        buttonBackground.setAlpha(0.5f);

        SpriteDrawable b = new SpriteDrawable(buttonBackground);
        b.setBottomHeight(-15);
        b.setTopHeight(-30);
        buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = starTrekFont;
        buttonStyle.up = b;

        labelStyle = new Label.LabelStyle();
        labelStyle.font = starTrekFont;
        labelStyle.fontColor = Color.WHITE;

        scrStyle = new ScrollPane.ScrollPaneStyle();

        //a sprite for a black background
        Sprite s = new Sprite(new Texture(new Pixmap(2000, 50, Pixmap.Format.RGB888)));
        s.setColor(Color.WHITE);
        s.setAlpha(0.5f);

        //initialising list style
        lstStyle = new List.ListStyle();
        lstStyle.font = starTrekFont;
        lstStyle.selection = new SpriteDrawable(s);
        lstStyle.background = new SpriteDrawable(s);

        //initialising the text field
        tfs = new TextField.TextFieldStyle();
        tfs.font = starTrekFont;
        tfs.fontColor = Color.WHITE;
        tfs.background = new SpriteDrawable(s);
    }

    public void pause() {}
    public void resume() {}
    public void hide() {}

}