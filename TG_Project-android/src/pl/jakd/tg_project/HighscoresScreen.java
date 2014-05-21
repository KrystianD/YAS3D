package pl.jakd.tg_project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont.TextBounds;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class HighscoresScreen extends ScreenAdapter implements InputProcessor
{
	public static final int HIGHSCORE_LIST_SIZE = 10;
	public static final String HIGHSCORE_FILE = "highscores.txt";

	public String playerName = "player";
	public int newScore = Integer.MIN_VALUE;

	private GameSnake game;
	private Stage stage;
	private Skin skin;
	private ArrayList<MyPair> highscoreList;
	private BufferedWriter bw;
	private FileOutputStream fos;
	private File file;

	private Label highscoreLabel;

	public HighscoresScreen (GameSnake game, int score)
	{
		this.game = game;
		stage = new Stage (0, 0, true);

		FileHandle skinFile = Gdx.files.internal ("uiskin.json");
		skin = new Skin (skinFile);

		Gdx.input.setInputProcessor (this);
		Gdx.input.setCatchBackKey (true);

		highscoreList = new ArrayList<MyPair> ();

		file = new File (game.getContext ().getFilesDir (), HIGHSCORE_FILE);
		try
		{
			Log.d ("KD", "filexists" + file.exists ());

			if (!file.exists ())
				file.createNewFile ();
			fos = new FileOutputStream (file, true);
			bw = new BufferedWriter (new OutputStreamWriter (fos));

			readHighscores ();

			//if (score != Integer.MIN_VALUE)
			{
				this.newScore = score;
				addNewHighscore (score);
			}
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

		float currentY = height;

		Label welcomeLabel = new Label ("Highscores", skin);
		welcomeLabel.setX ((width - welcomeLabel.getWidth ()) / 2);
		welcomeLabel.setY (currentY -= 100);
		stage.addActor (welcomeLabel);

		highscoreLabel = new Label ("", skin);

		stage.addActor (highscoreLabel);

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

	@Override
	public boolean keyDown (int keycode)
	{
		if (keycode == Keys.BACK)
		{
			Gdx.input.setCatchBackKey (false);
			game.setScreen (new MainMenu (game));
		}
		return true;
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
				highscoreList.add (new MyPair (tmp[0], Integer.valueOf (tmp[1])));
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

		MyPair p = null;
		try
		{
			p = highscoreList.get (highscoreList.size () - 1);
		}
		catch (Exception e)
		{
		}

		if (p == null || score > p.score)
		{
			isNewHighscore = true;
		}

		Log.d ("KD", "" + isNewHighscore);

		if (highscoreList.size () < HIGHSCORE_LIST_SIZE || isNewHighscore)
		{
			if (highscoreList.size () >= HIGHSCORE_LIST_SIZE)
				highscoreList.remove (p);

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
			fos.getChannel ().truncate (0);
			for (MyPair p : highscoreList)
			{
				line = p.name + " " + p.score + '\n';
				bw.append (line);
			}
			line = playerName + " " + newScore + '\n';
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
		highscoreLabel.setY (labelY - 150);
	}
	@Override
	public boolean keyTyped (char arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp (int arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved (int arg0, int arg1)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled (int arg0)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown (int arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged (int arg0, int arg1, int arg2)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp (int arg0, int arg1, int arg2, int arg3)
	{
		// TODO Auto-generated method stub
		return false;
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
}