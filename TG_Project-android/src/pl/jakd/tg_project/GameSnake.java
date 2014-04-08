package pl.jakd.tg_project;

import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class GameSnake extends Game
{
	public static final byte NO_SPHERE = 0;
	public static final byte SPHERE = 1;

	private Context context;
	private FPSLogger fpsLogger;
	private Sender sender;

	public GameSnake (Context context, Sender sender)
	{
		this.context = context;
		this.sender = sender;
	}

	public MainMenu getMainMenu ()
	{
		return new MainMenu (this);
	}

	public LevelScreen getLevelScreen (byte type)
	{
		return new LevelScreen (this, context, sender, type);
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
