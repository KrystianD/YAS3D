package pl.jakd.tg_project;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;

public class LevelScreen extends ScreenAdapter implements SensorEventListener,
		InputProcessor
{
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model[] = new Model[10];
	public ModelInstance instance[] = new ModelInstance[10];
	public ModelInstance worldInstance;
	public Environment environment;
	public CameraInputController camController;
	public BitmapFont font;
	public SpriteBatch batch;

	private Quaternion worldQuat = new Quaternion ();
	private GameSnake game;
	private SensorManager mSensorManager;
	private Sensor mAccel, mGyro, mMagnet;

	private Timer calcTimer = new Timer ();
	private Random rand = new Random ();

	private Sender sender;
	private PlayerSnake player;
	private FoodManager foodManager;
	private ArrayList<Enemy> enemies = new ArrayList<Enemy> ();

	private byte type;

	float aX = 0, aY = 0, aZ = 0, gX = 0, gY = 0, gZ = 0, mX = 0, mY = 0,
			mZ = 0;
	Boolean hasA = false, hasG = false, hasM = false;
	Mad mad;

	public LevelScreen (GameSnake game, Context ctx, Sender sender, byte type)
	{
		this.game = game;

		mad = new Mad ();
		// System.loadLibrary ("mad");
		Gdx.input.setCatchBackKey (true);

		this.sender = sender;
		this.type = type;

		mSensorManager = (SensorManager)ctx
				.getSystemService (Context.SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
		mGyro = mSensorManager.getDefaultSensor (Sensor.TYPE_GYROSCOPE);
		mMagnet = mSensorManager.getDefaultSensor (Sensor.TYPE_MAGNETIC_FIELD);

		mSensorManager.registerListener (this, mAccel,
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener (this, mGyro,
				SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener (this, mMagnet,
				SensorManager.SENSOR_DELAY_FASTEST);

		FreeTypeFontGenerator generator = new FreeTypeFontGenerator (Gdx.files.internal ("fonts/arial.ttf"));
		font = generator.generateFont (45);
		generator.dispose ();

	}

	PointLight light = new PointLight ();

	@Override
	public void show ()
	{
		batch = new SpriteBatch ();
		modelBatch = new ModelBatch ();

		cam = new PerspectiveCamera (50, Gdx.graphics.getWidth (),
				Gdx.graphics.getHeight ());

		cam.position.set (0f, 0f, 0f);
		cam.lookAt (0, 0, 0);
		cam.near = 0.1f;
		cam.far = 1.1f;
		cam.update ();

		camController = new CameraInputController (cam);
		Gdx.input.setInputProcessor (this);

		ModelBuilder modelBuilder = new ModelBuilder ();

		//Create world
		Model worldModel = modelBuilder.createSphere (0.1f, 0.1f, 0.1f, 10, 10,
				new Material (ColorAttribute.createDiffuse (Color.BLUE)),
				Usage.Position | Usage.Normal);
		worldInstance = new ModelInstance (worldModel);

		//Create player
		Model modelPlayerSnakePart = modelBuilder.createSphere (PlayerSnake.SNAKE_SPHERE_SIZE,
				PlayerSnake.SNAKE_SPHERE_SIZE, PlayerSnake.SNAKE_SPHERE_SIZE, 9, 9, new Material (
						ColorAttribute.createDiffuse (Color.GREEN)),
				Usage.Position | Usage.Normal);
		ModelInstance instSnakePart = new ModelInstance (modelPlayerSnakePart);
		player = new PlayerSnake (new Vector3 (1, 0, 0.2f), new Vector3 (0, 0, 1), instSnakePart);

		//Create opponents
		Model modelEnemySnakePart = modelBuilder.createSphere (PlayerSnake.SNAKE_SPHERE_SIZE,
				PlayerSnake.SNAKE_SPHERE_SIZE, PlayerSnake.SNAKE_SPHERE_SIZE, 9, 9, new Material (
						ColorAttribute.createDiffuse (Color.BLUE)),
				Usage.Position | Usage.Normal);
		ModelInstance instEnemySnakePart = new ModelInstance (modelEnemySnakePart);
		for (int i = 0; i < 3; i++)
		{
			Enemy e = new Enemy (instEnemySnakePart);
			e.reset ();
			enemies.add (e);
		}

		//Create food
		Model foodModel = modelBuilder.createSphere (FoodManager.FOOD_SPHERE_SIZE, FoodManager.FOOD_SPHERE_SIZE,
				FoodManager.FOOD_SPHERE_SIZE, 10, 10,
				new Material (ColorAttribute.createDiffuse (Color.RED)),
				Usage.Position | Usage.Normal);
		ModelInstance foodInstance = new ModelInstance (foodModel);

		foodManager = new FoodManager (100, foodInstance);

		environment = new Environment ();
		environment.set (new ColorAttribute (ColorAttribute.AmbientLight, 0.4f,
				0.4f, 0.4f, 1f));
		environment.add (light.set (0.8f, 0.8f, 0.8f, 2f, 0f, 0f, 5));

		calcTimer.scheduleTask (new Timer.Task ()
		{
			@Override
			public void run ()
			{
				calc ();
			}
		}, 0.02f, 0.02f);
	}

	@Override
	public void dispose ()
	{
		modelBatch.dispose ();
	}

	@Override
	public void pause ()
	{
	}

	Vector2 latlongToMeters (Vector2 pos)
	{
		float longtitude = pos.y;
		float latitude = pos.x / (float)Math.cos (longtitude);
		return new Vector2 (latitude, longtitude);
	}

	float x = 0;

	@Override
	public void render (float delta)
	{
		long start = System.currentTimeMillis ();

		// mad (1, 1, 1, 2, 2, 2, 3, 3, 3);
		Gdx.gl.glViewport (0, 0, Gdx.graphics.getWidth (),
				Gdx.graphics.getHeight ());
		Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		Gdx.gl.glEnable (GL20.GL_CULL_FACE);
		Gdx.gl.glCullFace (GL20.GL_BACK);

		worldQuat.set (mad.q1, mad.q2, mad.q3, -mad.q0);

		Vector3 lightPos = new Vector3 (2, 0, 0);
		lightPos.mul (worldQuat);
		light.position.set (lightPos);

		modelBatch.begin (cam);

		long a;
		a = System.currentTimeMillis ();
		player.render (modelBatch, worldQuat, environment, cam.frustum);
		//Log.d ("KD", "PLAYER RENDER = " + (System.currentTimeMillis () - a));

		a = System.currentTimeMillis ();
		foodManager.render (modelBatch, worldQuat, environment, cam.frustum);
		//Log.d ("KD", "FOOD RENDER = " + (System.currentTimeMillis () - a));

		a = System.currentTimeMillis ();
		for (Enemy e : enemies)
		{
			e.render (modelBatch, worldQuat, environment, cam.frustum);
		}
		//Log.d ("KD", "ENEMIES RENDER = " + (System.currentTimeMillis () - a));

		a = System.currentTimeMillis ();

		modelBatch.end ();

		SpriteBatch spriteBatch = new SpriteBatch ();
		spriteBatch.begin ();

		String status = "Points: " + player.getScore () + "\nLives: " + player.getLives ();
		font.drawMultiLine (spriteBatch, status, 10, (float)Gdx.app.getGraphics ().getHeight ());

		spriteBatch.end ();
	}

	@Override
	public void resize (int width, int height)
	{

	}

	@Override
	public void resume ()
	{

	}

	@Override
	public void onAccuracyChanged (Sensor sensor, int accuracy)
	{

	}

	long lastCalc = 0;

	int xMax = -99999;
	int xMin = 99999;

	@Override
	public void onSensorChanged (SensorEvent event)
	{
		if (event.sensor.getType () == Sensor.TYPE_ACCELEROMETER)
		{
			aX = event.values[1];
			aY = -event.values[0];
			aZ = -event.values[2];
			hasA = true;
		}
		if (event.sensor.getType () == Sensor.TYPE_GYROSCOPE)
		{
			gX = -event.values[1];
			gY = event.values[0];
			gZ = event.values[2];
			hasG = true;
		}
		if (event.sensor.getType () == Sensor.TYPE_MAGNETIC_FIELD)
		{
			mX = -event.values[1];
			mY = event.values[0];
			mZ = event.values[2];
			hasM = true;
		}

	}

	private float oq0 = 0, oq1 = 0, oq2 = 0, oq3 = 0;
	private boolean isStabilized = false;
	private long lastSend = 0;

	private void sendSensorsPacket (long ticks)
	{
		ByteBuffer bArray = ByteBuffer.allocate (1 * 1 + 13 * 4 + 1 * 8 + 1 * 2);
		bArray.order (ByteOrder.LITTLE_ENDIAN);

		bArray.put (Sender.TYPE_SENSORS);
		bArray.putFloat (aX);
		bArray.putFloat (aY);
		bArray.putFloat (aZ);
		bArray.putFloat (gX);
		bArray.putFloat (gY);
		bArray.putFloat (gZ);
		bArray.putFloat (mX);
		bArray.putFloat (mY);
		bArray.putFloat (mZ);
		bArray.putFloat (mad.q0);
		bArray.putFloat (mad.q1);
		bArray.putFloat (mad.q2);
		bArray.putFloat (mad.q3);
		bArray.putLong (ticks);
		bArray.put (type);
		bArray.put (isStabilized ? (byte)1 : (byte)0);
		sender.sendData (bArray.array ());
	}

	private void buffAppendXYZ (ByteBuffer buff, Vector3 v)
	{
		float lat = (float)Math.acos (v.z);
		float lon = (float)Math.atan2 (v.y, v.x);

		short latU = (short)(lat * 5000.0f);
		short lonU = (short)(lon * 5000.0f);

		buff.putShort (latU);
		buff.putShort (lonU);
	}

	public void calc ()
	{

		long ticks = System.currentTimeMillis ();
		//calc World orientation
		if (hasA && hasM && isStabilized)
		{
			if (lastCalc == 0)
				lastCalc = ticks;
			float diff = (float)(ticks - lastCalc) / 1000.0f;

			if (diff < 0.015)
				return;

			lastCalc = ticks;

			mad.MadgwickAHRSupdate (gX, gY, gZ, aX, aY, aZ, mX, mY, mZ, diff);

			sendSensorsPacket (ticks);

			gX = gY = gZ = 0;

		}
		//stabilize World
		else if (hasA && hasM && !isStabilized)
		{
			float diff = 0.005f;
			mad.MadgwickAHRSupdate (0, 0, 0, aX, aY, aZ, mX, mY, mZ, 0.02f * 100);

			if ((Math.abs (mad.q0 - oq0) <= diff)
					&& (Math.abs (mad.q1 - oq1) <= diff)
					&& (Math.abs (mad.q2 - oq2) <= diff)
					&& (Math.abs (mad.q3 - oq3) <= diff))
			{
				isStabilized = true;
			}

			oq0 = mad.q0;
			oq1 = mad.q1;
			oq2 = mad.q2;
			oq3 = mad.q3;

			sendSensorsPacket (ticks);
		}

		long start = System.currentTimeMillis ();

		// calculate player position
		float snakeAngInc = 0f;
		if (leftPressed && rightPressed)
		{
			snakeAngInc = lastPressed * 0.02f;
		}
		else if (leftPressed || rightPressed)
		{
			if (leftPressed)
				snakeAngInc = -0.02f;
			if (rightPressed)
				snakeAngInc = 0.02f;
		}
		player.setMoveAngle (snakeAngInc);
		player.calc ();

		//check player collision
		if (foodManager.checkCollison (player))
		{
			player.grow ();
		}

		//calc enemies
		for (Enemy e : enemies)
		{
			if (e.needNewFood > 200)
			{
				Log.d ("KD", "need food " + e.needNewFood);
				e.needNewFood = 0;
				e.currentTarget = foodManager.foodPositions.get (rand.nextInt (foodManager.foodPositions.size ()));
			}

			boolean collided = foodManager.checkCollison (e);

			if (collided || e.currentTarget == null)
			{
				if (collided)
				{
					e.grow ();
				}
				e.currentTarget = foodManager.foodPositions.get (rand.nextInt (foodManager.foodPositions.size ()));
			}

			Vector3 a = new Vector3 (e.currentTarget).sub (e.getCurrentPosition ()).nor ();
			float ratio = 0.92f;

			e.setMoveDir (new Vector3 (e.getMoveDir ()).mul (ratio).add (a.mul (1 - ratio)));
			e.calc ();
		}

		//check all collision
		//Utils.ECollisionResult collisionResult = Utils.checkCollision(player,enemies);

		// sending data
		if (System.currentTimeMillis () - lastSend > 1000 / 35)
		{
			lastSend = System.currentTimeMillis ();

			int type = Byte.SIZE * 1;

			// send player data
			int playerSize = Short.SIZE * 1;
			int playerTailSize = 2 * Short.SIZE * player.tail.size ();
			int frustrumSize = 8 * 3 * Float.SIZE;

			ByteBuffer bBuff = ByteBuffer.allocate (type + playerSize + frustrumSize + playerTailSize);
			bBuff.order (ByteOrder.LITTLE_ENDIAN);

			bBuff.put (Sender.TYPE_PLAYER); // type

			bBuff.putShort ((short)player.tail.size ()); // player size
			for (int i = 0; i < 8; i++)
			{
				bBuff.putFloat (cam.frustum.planePoints[i].x);
				bBuff.putFloat (cam.frustum.planePoints[i].y);
				bBuff.putFloat (cam.frustum.planePoints[i].z);
			}

			for (Vector3 v : player.tail)
			{
				buffAppendXYZ (bBuff, v);
			}
			sender.sendData (bBuff.array ());

			//send food data
			int foodSize = Short.SIZE * 1;
			int foodDataSize = 2 * Short.SIZE * foodManager.foodPositions.size ();

			bBuff = ByteBuffer.allocate (type + foodSize + foodDataSize);
			bBuff.order (ByteOrder.LITTLE_ENDIAN);

			bBuff.put (Sender.TYPE_FOOD);
			bBuff.putShort ((short)foodManager.foodPositions.size ());

			for (Vector3 v : foodManager.foodPositions)
			{
				buffAppendXYZ (bBuff, v);
			}
			sender.sendData (bBuff.array ());

			//send enemies data
			int enemyIdSize = Short.SIZE * 1;
			int enemySize = Short.SIZE * 1;
			int enemyTailSize;

			for (Enemy e : enemies)
			{
				enemyTailSize = 2 * Short.SIZE * e.tail.size ();

				bBuff = ByteBuffer.allocate (type + enemyIdSize + enemySize + enemyTailSize);
				bBuff.order (ByteOrder.LITTLE_ENDIAN);

				bBuff.put (Sender.TYPE_ENEMY);
				bBuff.putShort ((short)enemies.indexOf (e));
				bBuff.putShort ((short)e.tail.size ());

				for (Vector3 v : e.tail)
				{
					buffAppendXYZ (bBuff, v);
				}
				sender.sendData (bBuff.array ());
			}
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

	@Override
	public boolean keyTyped (char arg0)
	{
		return false;
	}

	@Override
	public boolean keyUp (int arg0)
	{
		return false;
	}

	@Override
	public boolean mouseMoved (int arg0, int arg1)
	{
		return false;
	}

	@Override
	public boolean scrolled (int arg0)
	{
		return false;
	}

	private boolean leftPressed = false, rightPressed = false;
	private int lastPressed = 0;

	@Override
	public boolean touchDown (int arg0, int arg1, int arg2, int arg3)
	{
		if (arg0 < Gdx.app.getGraphics ().getWidth () / 2)
		{
			leftPressed = true;
			lastPressed = -1;
		}
		else
		{
			rightPressed = true;
			lastPressed = 1;
		}
		return false;
	}

	@Override
	public boolean touchDragged (int arg0, int arg1, int arg2)
	{
		return false;
	}

	@Override
	public boolean touchUp (int arg0, int arg1, int arg2, int arg3)
	{
		if (arg0 < Gdx.app.getGraphics ().getWidth () / 2)
			leftPressed = false;
		else
			rightPressed = false;
		return false;
	}

	@Override
	public void hide ()
	{
		calcTimer.stop ();
		calcTimer.clear ();
	}
}