package pl.jakd.tg_project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

public class HighscoresScreen extends ScreenAdapter implements InputProcessor
{
	private GameSnake game;
	private Stage stage;

	public HighscoresScreen (GameSnake game)
	{
		this.game = game;
		stage = new Stage (0, 0, true);

		Gdx.input.setInputProcessor (this);
		Gdx.input.setCatchBackKey (true);
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

	}

	@Override
	public void resume ()
	{

	}

	@Override
	public void hide ()
	{

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
}