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

	public LevelScreen getLevelScreen (int levelNumber)
	{
		return new LevelScreen (this, context, levelNumber);
	}

	public HighscoresScreen getHigscoresScreen (int score, int levelNumber)
	{
		return new HighscoresScreen (this, score, levelNumber);
	}

	public LevelSelectScreen getLevelSelectScreen ()
	{
		return new LevelSelectScreen (this);
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
