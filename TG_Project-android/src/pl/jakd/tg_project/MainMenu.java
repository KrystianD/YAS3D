package pl.jakd.tg_project;

import android.util.Log;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;

public class MainMenu extends ScreenAdapter {
	// setup the dimensions of the menu buttons
	private static final float BUTTON_WIDTH = 300f;
	private static final float BUTTON_HEIGHT = 60f;
	private static final float BUTTON_SPACING = 10f;

	private GameSnake game;

	private Skin skin;
	private Stage stage;

	public MainMenu(GameSnake game) {
		this.game = game;
		FileHandle skinFile = Gdx.files.internal("uiskin.json");
		skin = new Skin(skinFile);
		
		stage = new Stage(0, 0, true);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		stage.setViewport(width, height);
		
		final float buttonX = (width - BUTTON_WIDTH) / 2;
		float currentY = 280f;

		
		// label "welcome"
		Label welcomeLabel = new Label("Welcome to YAS3D!", skin);
		welcomeLabel.setX((width - welcomeLabel.getWidth()) / 2);
		welcomeLabel.setY(currentY + 100);
		stage.addActor(welcomeLabel);

		// button "start game"
		TextButton startGameButton = new TextButton("Start game", skin);
		startGameButton.setX(buttonX);
		startGameButton.setY(currentY);
		startGameButton.setWidth(BUTTON_WIDTH);
		startGameButton.setHeight(BUTTON_HEIGHT);
		startGameButton.addListener(new EventListener() {
			@Override
			public boolean handle(Event event) {
				
				game.setScreen(new LevelScreen(game, game.getContext()));
				return true;
			}
		});
		stage.addActor(startGameButton);

		// button "options"
		TextButton optionsButton = new TextButton("Options", skin);
		optionsButton.setX(buttonX);
		optionsButton.setY(currentY -= BUTTON_HEIGHT + BUTTON_SPACING);
		optionsButton.setWidth(BUTTON_WIDTH);
		optionsButton.setHeight(BUTTON_HEIGHT);
		stage.addActor(optionsButton);

		// button "hall of fame"
		TextButton hallOfFameButton = new TextButton("Hall of Fame", skin);
		hallOfFameButton.setX(buttonX);
		hallOfFameButton.setY(currentY -= BUTTON_HEIGHT + BUTTON_SPACING);
		hallOfFameButton.setWidth(BUTTON_WIDTH);
		hallOfFameButton.setHeight(BUTTON_HEIGHT);
		stage.addActor(hallOfFameButton);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		stage.act(delta);

		Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		stage.draw();
	}
}