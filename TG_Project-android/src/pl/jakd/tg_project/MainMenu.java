package pl.jakd.tg_project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class MainMenu extends ScreenAdapter
{
	// setup the dimensions of the menu buttons
	private static final float BUTTON_WIDTH = 600f;
	private static final float BUTTON_HEIGHT = 120f;
	private static final float BUTTON_SPACING = 40f;

	private GameSnake game;

	private Skin skin;
	private Stage stage;

	public MainMenu (GameSnake game)
	{
		this.game = game;
		FileHandle skinFile = Gdx.files.internal ("uiskin.json");
		skin = new Skin (skinFile);

		stage = new Stage (0, 0, true);
	}

	@Override
	public void resize (int width, int height)
	{
		super.resize (width, height);
		stage.setViewport (width, height);

		final float buttonX = (width - BUTTON_WIDTH) / 2;
		float currentY = 480f;

		// label "welcome"
		Label welcomeLabel = new Label ("Welcome to YAS3D!", skin);
		welcomeLabel.setX ((width - welcomeLabel.getWidth ()) / 2);
		welcomeLabel.setY (currentY + 100);
		stage.addActor (welcomeLabel);

		// button "start game"
		TextButton startGameButton = new TextButton ("Start Game", skin);
		startGameButton.setX (buttonX);
		startGameButton.setY (currentY);
		startGameButton.setWidth (BUTTON_WIDTH);
		startGameButton.setHeight (BUTTON_HEIGHT);
		startGameButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				game.setScreen (game.getLevelScreen ());
				return true;
			}
		});
		stage.addActor (startGameButton);

		
		TextButton highscoresButton = new TextButton ("Highscores", skin);
		highscoresButton.setX (buttonX);
		highscoresButton.setY (currentY -= BUTTON_HEIGHT + BUTTON_SPACING);
		highscoresButton.setWidth (BUTTON_WIDTH);
		highscoresButton.setHeight (BUTTON_HEIGHT);
		highscoresButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				game.setScreen (game.getHigscoresScreen ());
				return true;
			}
		});
		stage.addActor (highscoresButton);

		/*// button "hall of fame"
		TextButton hallOfFameButton = new TextButton ("Highscores", skin);
		hallOfFameButton.setX (buttonX);
		hallOfFameButton.setY (currentY -= BUTTON_HEIGHT + BUTTON_SPACING);
		hallOfFameButton.setWidth (BUTTON_WIDTH);
		hallOfFameButton.setHeight (BUTTON_HEIGHT);
		stage.addActor (hallOfFameButton);*/
	}

	@Override
	public void show ()
	{
		Gdx.input.setInputProcessor (stage);
	}

	@Override
	public void render (float delta)
	{
		stage.act (delta);

		Gdx.gl.glClearColor (0f, 0f, 0f, 1f);
		Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT);

		stage.draw ();
	}
	@Override
	public void dispose ()
	{
		super.dispose ();
		stage.dispose ();
	}
}