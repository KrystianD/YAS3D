package pl.jakd.tg_project.screens;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import pl.jakd.tg_project.GameSnake;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

public class HighscoresScreen extends ScreenAdapter
{
	public static final int HIGHSCORE_LIST_SIZE = 10;
	public static final String HIGHSCORE_FILE = "highscores.txt";

	private static final float BUTTON_WIDTH = 200f;
	private static final float BUTTON_HEIGHT = 60f;
	private static final float BUTTON_SPACING = 20f;

	public String playerName = "player";
	public int newScore = Integer.MIN_VALUE;

	private int levelNumber;
	private GameSnake game;
	private Stage stage;
	private Skin skin;
	private ArrayList<MyPair> highscoreList;
	private BufferedWriter bw;
	private FileOutputStream fos;
	private File file;

	private Label highscoreLabel;

	public HighscoresScreen (GameSnake game, int score, int levelNumber)
	{
		this.game = game;
		this.levelNumber = levelNumber;
		stage = new Stage (0, 0, true);

		FileHandle skinFile = Gdx.files.internal ("uiskin.json");
		skin = new Skin (skinFile);

		Gdx.input.setInputProcessor (stage);
		Gdx.input.setCatchBackKey (true);

		highscoreList = new ArrayList<MyPair> ();

		openHighscoresFile (levelNumber);

		readHighscores ();
		if (score != Integer.MIN_VALUE)
		{
			this.newScore = score;
			addNewHighscore (score);
		}
		//changeLevel (levelNumber);
	}

	private void openHighscoresFile (int level)
	{
		file = new File (game.getContext ().getFilesDir (), HIGHSCORE_FILE + level);
		try
		{
			Log.d ("KD", "filexists" + file.exists ());

			if (!file.exists ())
				file.createNewFile ();
			fos = new FileOutputStream (file, true);
			bw = new BufferedWriter (new OutputStreamWriter (fos));
		}
		catch (Exception e)
		{
			Log.d ("KD", "cannot access highscore file");
		}
	}

	@Override
	public void show ()
	{

	}

	@Override
	public void dispose ()
	{
		stage.dispose ();
	}

	@Override
	public void pause ()
	{

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
	public void resize (int width, int height)
	{
		super.resize (width, height);
		stage.setViewport (width, height);

		final float buttonX = (width - BUTTON_WIDTH * 3 - BUTTON_SPACING * 2) / 2;
		float currentY = height;

		Texture backgroundTexture = new Texture (Gdx.files.internal ("universe.jpg"));
		Image background = new Image (backgroundTexture);
		background.sizeBy (width, height);
		stage.addActor (background);

		Label welcomeLabel = new Label ("Highscores", skin);
		welcomeLabel.setX ((width - welcomeLabel.getWidth ()) / 2);
		welcomeLabel.setY (currentY -= 100);
		stage.addActor (welcomeLabel);

		currentY -= BUTTON_HEIGHT;
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
				changeLevel (1);
				return true;
			}
		});
		stage.addActor (level1TextButton);

		// medium
		TextButton level2TextButton = new TextButton ("MEDIUM", skin);
		level2TextButton.setX (buttonX + BUTTON_SPACING + BUTTON_WIDTH);
		level2TextButton.setY (currentY);
		level2TextButton.setWidth (BUTTON_WIDTH);
		level2TextButton.setHeight (BUTTON_HEIGHT);
		level2TextButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				changeLevel (2);
				return true;
			}
		});
		stage.addActor (level2TextButton);

		// hard
		TextButton level3TextButton = new TextButton ("HARD", skin);
		level3TextButton.setX (buttonX + 2 * (BUTTON_SPACING + BUTTON_WIDTH));
		level3TextButton.setY (currentY);
		level3TextButton.setWidth (BUTTON_WIDTH);
		level3TextButton.setHeight (BUTTON_HEIGHT);
		level3TextButton.addListener (new InputListener ()
		{
			@Override
			public boolean handle (Event event)
			{
				changeLevel (3);
				return true;
			}
		});
		stage.addActor (level3TextButton);

		highscoreLabel = new Label ("", skin);

		stage.addActor (highscoreLabel);

		setNewHighscores ();
		//changeLevel (levelNumber);
	}
	public void changeLevel (int level)
	{

		this.levelNumber = level;
		try
		{
			bw.close ();
			fos.close ();
		}
		catch (IOException e)
		{
		}
		openHighscoresFile (level);
		readHighscores ();
		setNewHighscores ();
	}
	@Override
	public void resume ()
	{

	}

	@Override
	public void hide ()
	{
		try
		{
			bw.close ();
		}
		catch (IOException e)
		{
		}
	}

	private void readHighscores ()
	{
		String line = null;
		highscoreList.clear ();
		try
		{
			BufferedReader br = new BufferedReader (new FileReader (file));
			Log.d ("KD", "read!");
			while ((line = br.readLine ()) != null)
			{
				Log.d ("KD", line);
				String[] tmp = line.split (" ");
				String name = "";
				for (int i = 0; i < tmp.length - 1; i++)
				{
					name += tmp[i] + " ";
				}
				name.trim ();
				highscoreList.add (new MyPair (name, Integer.valueOf (tmp[tmp.length - 1])));
			}
			br.close ();
			Collections.sort (highscoreList);
			Collections.reverse (highscoreList);
		}
		catch (Exception e)
		{
			Log.d ("KD", "błąd odczytu pliku");
		}
	}

	private void addNewHighscore (int score)
	{
		boolean isNewHighscore = false;

		MyPair minScore = null;
		try
		{
			minScore = highscoreList.get (highscoreList.size () - 1);
		}
		catch (Exception e)
		{
		}

		if (minScore == null || score > minScore.score)
		{
			isNewHighscore = true;
		}

		Log.d ("KD", "" + isNewHighscore);

		if (highscoreList.size () < HIGHSCORE_LIST_SIZE || isNewHighscore)
		{
			Log.d ("KD", "size" + (highscoreList.size () >= HIGHSCORE_LIST_SIZE));
			if (highscoreList.size () >= HIGHSCORE_LIST_SIZE)
			{
				Log.d ("KD", "size bef" + (highscoreList.size ()));
				highscoreList.remove (highscoreList.size () - 1);
				Log.d ("KD", "size aft" + (highscoreList.size ()));
			}

			Gdx.input.setOnscreenKeyboardVisible (true);

			MyTextInputListener listener = new MyTextInputListener (this);
			Gdx.input.getTextInput (listener, "New Highscore!", playerName);

		}
	}

	public void writeHighscores ()
	{
		String line;
		try
		{
			int i = 0;
			fos.getChannel ().truncate (0);
			for (MyPair p : highscoreList)
			{
				Log.d ("KD", "saves" + i++);
				line = p.name.trim () + " " + p.score + '\n';
				bw.append (line);
			}
			line = playerName.trim () + " " + newScore + '\n';
			bw.append (line);
			bw.flush ();
			Log.d ("KD", line);
			readHighscores ();
			Log.d ("KD", "after write " + highscoreList.toString ());
			setNewHighscores ();
		}
		catch (Exception e)
		{
			Log.d ("KD", "zapis nieudany");
		}
	}
	private void setNewHighscores ()
	{
		String highscores = "";
		for (MyPair p : highscoreList)
		{
			String highscore = String.format ("%d. %s %15d\n\n", highscoreList.indexOf (p) + 1, p.name, p.score);
			highscores += highscore;
		}
		highscoreLabel.setText (highscores);

		TextBounds bounds = highscoreLabel.getTextBounds ();
		float labelX = (Gdx.app.getGraphics ().getWidth () / 2) - (bounds.width / 2);
		float labelY = (Gdx.app.getGraphics ().getHeight () / 2) + (bounds.height / 2);

		highscoreLabel.setX (labelX);
		highscoreLabel.setY (labelY - 250);
	}

	private class MyTextInputListener implements TextInputListener
	{
		HighscoresScreen screen;

		public MyTextInputListener (HighscoresScreen screen)
		{
			this.screen = screen;
		}

		@Override
		public void input (String text)
		{
			screen.playerName = text;
			screen.writeHighscores ();
		}

		@Override
		public void canceled ()
		{
			screen.playerName = "player";
			screen.writeHighscores ();
		}
	}

}

class MyPair implements Comparable<MyPair>
{
	public final String name;
	public final int score;

	public MyPair (String name, int score)
	{
		this.name = name;
		this.score = score;
	}

	@Override
	public int compareTo (MyPair o)
	{
		return score < o.score ? -1 : score > o.score ? 1 : 0;
	}

	@Override
	public String toString ()
	{
		return "name: " + name + " score:" + score;
	}
}