package pl.jakd.tg_project;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class GameSnake extends Game
{
	private Context context;
	private FPSLogger fpsLogger;

	public GameSnake (Context context)
	{
		this.context = context;
	}

	public MainMenu getMainMenu ()
	{
		return new MainMenu (this);
	}

	public LevelScreen getLevelScreen ()
	{
		return new LevelScreen (this, context);
	}
	
	public HighscoresScreen getHigscoresScreen()
	{
		return new HighscoresScreen(this, Integer.MIN_VALUE);
	}

	@Override
	public void create ()
	{
		fpsLogger = new FPSLogger ();
	}

	@Override
	public void resize (int width, int height)
	{
		super.resize (width, height);
		setScreen (getMainMenu ());
	}

	@Override
	public void render ()
	{
		super.render ();
		fpsLogger.log ();
	}

	public Context getContext ()
	{
		return context;
	}
}
