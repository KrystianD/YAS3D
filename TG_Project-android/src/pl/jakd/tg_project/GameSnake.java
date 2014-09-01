package pl.jakd.tg_project;

import pl.jakd.tg_project.screens.HighscoresScreen;
import pl.jakd.tg_project.screens.LevelScreen;
import pl.jakd.tg_project.screens.LevelScreen.Difficulty;
import pl.jakd.tg_project.screens.LevelSelectScreen;
import pl.jakd.tg_project.screens.MainMenu;
import android.content.Context;
import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

/**
 * klasa reprezentująca grę
 */
public class GameSnake extends Game
{
	public static final byte NO_SPHERE = 0;
	public static final byte SPHERE = 1;

	private Context context;
	private FPSLogger fpsLogger;
	private Sender sender;

	/**
	 * @param context kontekst aplikacji
	 * @param sender obiekt obsługujący wysyłanie danych (na czas prezenacji)
	 */
	public GameSnake (Context context, Sender sender)
	{
		this.context = context;
		this.sender = sender;
	}

	/**
	 * @return obiekt głównego menu gry
	 */
	public MainMenu getMainMenu ()
	{
		return new MainMenu (this);
	}

	/**
	 * @param difficulty trudność gry
	 * @return obiekt planszy gry
	 */
	public LevelScreen getLevelScreen (Difficulty difficulty)
	{
		return new LevelScreen (this, context, difficulty, sender);
	}

	/**
	 * @param score aktualny wynik
	 * @param difficulty trudność
	 * @return obiekt ekranu najwyższych wyników
	 */
	public HighscoresScreen getHigscoresScreen (int score, Difficulty difficulty)
	{
		return new HighscoresScreen (this, score, difficulty);
	}

	/**
	 * @return ekran wyboru poziomu trudności
	 */
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

	/**
	 * @return kontekst aplikacji
	 */
	public Context getContext ()
	{
		return context;
	}
}
