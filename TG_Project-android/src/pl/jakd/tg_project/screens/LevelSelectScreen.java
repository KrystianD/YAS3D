package pl.jakd.tg_project.screens;

import pl.jakd.tg_project.GameSnake;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class LevelSelectScreen extends ScreenAdapter
{
	// setup the dimensions of the menu buttons
	private static final float BUTTON_WIDTH = 600f;
	private static final float BUTTON_HEIGHT = 120f;
	private static final float BUTTON_SPACING = 40f;

	private GameSnake game;

	private Skin skin;
	private Stage stage;

	public LevelSelectScreen (GameSnake game)
	{
		this.game = game;
		FileHandle skinFile = Gdx.files.internal ("uiskin.json");
		skin = new Skin (skinFile);

		stage = new Stage (0, 0, true);

		Gdx.input.setCatchBackKey (true);
	}

	@Override
	public void resize (int width, int height)
	{
		super.resize (width, height);
		stage.setViewport (width, height);

		final float buttonX = (width - BUTTON_WIDTH) / 2;
		float currentY = 480f;

		// label "welcome"
		Label welcomeLabel = new Label ("SELECT LEVEL", skin);
		welcomeLabel.setX ((width - welcomeLabel.getWidth ()) / 2);
		welcomeLabel.setY (currentY + 100);
		stage.addActor (welcomeLabel);

		// easy
		TextButton level1TextButton = new TextButton ("EASY", skin);
		level1TextButton.setX (buttonX);
		level1TextButton.setY (currentY);
		level1TextButton.setWidth (BUTTON_WIDTH);
		level1TextButton.setHeight (BUTTON_HEIGHT);
		level1TextButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				game.setScreen (game.getLevelScreen (1));
				return true;
			}
		});
		stage.addActor (level1TextButton);

		// medium
		TextButton level2TextButton = new TextButton ("MEDIUM", skin);
		level2TextButton.setX (buttonX);
		level2TextButton.setY (currentY -= BUTTON_HEIGHT + BUTTON_SPACING);
		level2TextButton.setWidth (BUTTON_WIDTH);
		level2TextButton.setHeight (BUTTON_HEIGHT);
		level2TextButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				game.setScreen (game.getLevelScreen (2));
				return true;
			}
		});
		stage.addActor (level2TextButton);

		// hard
		TextButton level3TextButton = new TextButton ("HARD", skin);
		level3TextButton.setX (buttonX);
		level3TextButton.setY (currentY -= BUTTON_HEIGHT + BUTTON_SPACING);
		level3TextButton.setWidth (BUTTON_WIDTH);
		level3TextButton.setHeight (BUTTON_HEIGHT);
		level3TextButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				game.setScreen (game.getLevelScreen (3));
				return true;
			}
		});
		stage.addActor (level3TextButton);
	}

	@Override
	public void show ()
	{
		Gdx.input.setInputProcessor (stage);
	}

	@Override
	public void hide ()
	{
		super.hide ();
	}

	@Override
	public void render (float delta)
	{
		if (Gdx.input.isKeyPressed (Keys.BACK))
		{
			while (Gdx.input.isKeyPressed (Keys.BACK))
				; // hack
			game.setScreen (game.getMainMenu ());
		}

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