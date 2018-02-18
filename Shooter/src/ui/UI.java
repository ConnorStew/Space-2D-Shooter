package ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 * Handles displaying the current screen in the game.
 * @author Connor Stewart
 */
public class UI extends Game {

	/** Singleton instance of the main game. */
	private static final UI instance = new UI();
	
	/** The style for buttons within the game. */
	static TextButton.TextButtonStyle buttonStyle;
	
	/** The style for labels within the game. */
	static Label.LabelStyle labelStyle;
	
	/** The style for lists within the game. */
	static List.ListStyle lstStyle;
	
	/** The style for text fields within the game. */
	static TextFieldStyle tfs;
	
	/** The style for scroll panes within the game. */
	static ScrollPane.ScrollPaneStyle scrStyle;

	/** The font for use within the game. */
	static BitmapFont font;
	
	/** {@link #getInstance()} should be used to obtain an instance of this class.  */
	private UI(){};
	
	@Override
	public void create() {
		loadFont();
		initialiseStyles();
		
		//default to the menu screen
		setScreen(MenuScreen.getInstance());
	}

	public void render() {
		getScreen().render(Gdx.graphics.getDeltaTime());
	}
	
	public void dispose() {
		getScreen().dispose();
	}
	
	/**
	 * Loads the font.
	 */
	private void loadFont() {
		//load the font
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Star Trek Enterprise Future.ttf"));
		
		//setting font size
		FreeTypeFontParameter fontParameter = new FreeTypeFontParameter();
		fontParameter.size = 100; 
		
		//creating the font based on the font parameters
		font = generator.generateFont(fontParameter);
		
		//dispose the generator since its finished being used
		generator.dispose();
	}
	
	/**
	 * Loads the UI styles.
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
		buttonStyle.font = font;
		buttonStyle.up = b;
		
		
		

		
		labelStyle = new Label.LabelStyle();
		labelStyle.font = font;
		labelStyle.fontColor = Color.WHITE;
		
		scrStyle = new ScrollPane.ScrollPaneStyle();
		
		//a sprite for a black background
		Sprite s = new Sprite(new Texture(new Pixmap(2000, 50, Pixmap.Format.RGB888)));
		s.setColor(Color.WHITE);
		s.setAlpha(0.5f);
		
		//invisible colour sprite
		//Sprite invisible = new Sprite(new Texture(new Pixmap(2000, 50, Pixmap.Format.Alpha)));
		
		//initialising list style
		lstStyle = new List.ListStyle();
		lstStyle.font = font;
		lstStyle.selection = new SpriteDrawable(s);
		lstStyle.background = new SpriteDrawable(s);
		
		//initialising the text field
		tfs = new TextFieldStyle();
		tfs.font = font;
		tfs.fontColor = Color.WHITE;
		tfs.background = new SpriteDrawable(s);
	}

	/**
	 * @return the singleton instance of this class
	 */
	public static Game getInstance() {
		return instance;
	}

}
