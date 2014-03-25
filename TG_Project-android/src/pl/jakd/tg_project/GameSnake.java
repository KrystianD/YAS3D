package pl.jakd.tg_project;

import android.content.Context;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.FPSLogger;

public class GameSnake extends Game {
	private Context context;
	private FPSLogger fpsLogger;

	public GameSnake(Context context) {
		this.context = context;
	}

	public MainMenu getMainMenu() {
		return new MainMenu(this);
	}

	public LevelScreen getLevelScreen() {
		return new LevelScreen(this, context);
	}

	@Override
	public void create() {
		fpsLogger = new FPSLogger();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		//setScreen(new LevelScreen(this,context));
		setScreen(getMainMenu());
	}

	@Override
	public void render() {
		super.render();
		fpsLogger.log();
	}
	
	public Context getContext(){
		return context;
	}
}
