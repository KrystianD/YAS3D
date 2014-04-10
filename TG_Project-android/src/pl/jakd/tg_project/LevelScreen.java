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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
	public OrthographicCamera cam1;
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
	}

	PointLight light = new PointLight ();

	@Override
	public void show ()
	{
		Log.d ("KD", "SHOW!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		Log.d ("KD", this.toString ());

		font = new BitmapFont ();
		batch = new SpriteBatch ();
		modelBatch = new ModelBatch ();

		cam1 = new OrthographicCamera (1, 1);
		cam1.near = -100f;
		cam1.far = 300f;
		cam1.position.set (0f, 0f, 1);
		cam1.lookAt (0, 0, 0);
		cam1.update ();

		cam = new PerspectiveCamera (50, Gdx.graphics.getWidth (),
				Gdx.graphics.getHeight ());
		cam.position.set (3f, 3f, 3f);
		cam.position.set (0f, 0f, 30f);
		cam.position.set (0f, 0f, 0f);
		cam.lookAt (0, 0, 0);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update ();

		camController = new CameraInputController (cam);
		// Gdx.input.setInputProcessor (camController);
		Gdx.input.setInputProcessor (this);

		ModelBuilder modelBuilder = new ModelBuilder ();

		//cross
		/*model[0] = modelBuilder.createBox (1f, 1f, 5f, new Material (
				ColorAttribute.createDiffuse (Color.RED)), Usage.Position
				| Usage.Normal);
		instance[0] = new ModelInstance (model[0]);

		model[1] = modelBuilder.createBox (1f, 5f, 1f, new Material (
				ColorAttribute.createDiffuse (Color.GREEN)), Usage.Position
				| Usage.Normal);
		instance[1] = new ModelInstance (model[1]);

		model[2] = modelBuilder.createBox (5f, 1f, 1f, new Material (
				ColorAttribute.createDiffuse (Color.BLUE)), Usage.Position
				| Usage.Normal);
		instance[2] = new ModelInstance (model[2]);
		 */

		// model2 = modelBuilder.createBox (2f, 2f, 2f, new Material
		// (ColorAttribute.createDiffuse (Color.RED)), Usage.Position |
		// Usage.Normal);
		// instance2 = new ModelInstance (model2);

		//Create world

		Model worldModel = modelBuilder.createSphere (0.1f, 0.1f, 0.1f, 10, 10,
				new Material (ColorAttribute.createDiffuse (Color.BLUE)),
				Usage.Position | Usage.Normal);
		worldInstance = new ModelInstance (worldModel);

		//Create player
		Model modelPlayerSnakePart = modelBuilder.createSphere (PlayerSnake.SNAKE_SPHERE_SIZE,
				PlayerSnake.SNAKE_SPHERE_SIZE, PlayerSnake.SNAKE_SPHERE_SIZE, 10, 10, new Material (
						ColorAttribute.createDiffuse (Color.GREEN)),
				Usage.Position | Usage.Normal);
		ModelInstance instSnakePart = new ModelInstance (modelPlayerSnakePart);
		player = new PlayerSnake (new Vector3 (1, 0, 0.2f), new Vector3 (0, 0, 1), instSnakePart);

		//Create opponents
		Model modelEnemySnakePart = modelBuilder.createSphere (PlayerSnake.SNAKE_SPHERE_SIZE,
				PlayerSnake.SNAKE_SPHERE_SIZE, PlayerSnake.SNAKE_SPHERE_SIZE, 10, 10, new Material (
						ColorAttribute.createDiffuse (Color.BLUE)),
				Usage.Position | Usage.Normal);
		ModelInstance instEnemySnakePart = new ModelInstance (modelEnemySnakePart);
		for (int i = 0; i < 2; i++)
		{
			enemies.add (new Enemy (new Vector3 (1, i, 0.2f), new Vector3 (0, 0, 1), instEnemySnakePart));
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
		// environment.add (new DirectionalLight ().set (0.8f, 0.8f, 0.8f, -1f,
		// -0.8f, -0.2f));
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
		// mad (1, 1, 1, 2, 2, 2, 3, 3, 3);
		Gdx.gl.glViewport (0, 0, Gdx.graphics.getWidth (),
				Gdx.graphics.getHeight ());
		Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		worldQuat.set (mad.q1, mad.q2, mad.q3, -mad.q0);

		Vector3 lightPos = new Vector3 (2, 0, 0);
		lightPos.mul (worldQuat);
		light.position.set (lightPos);

		modelBatch.begin (cam);

		/*for (int i = 0; i < 3; i++)
		{
			instance[i].transform.idt ();
			instance[i].transform.scl (0.3f);
			instance[i].transform.rotate (worldQuat);
			// modelBatch.render (instance[i], environment);
		}*/

		player.render (modelBatch, worldQuat, environment);

		foodManager.render (modelBatch, worldQuat, environment);

		for (Enemy e : enemies)
		{
			e.render (modelBatch, worldQuat, environment);
		}

		/*instance[1].transform.idt ();
		instance[1].transform.translate (-15f, 0, 0);
		modelBatch.render (instance[1], environment);
		 */

		/*
		 * Vector3 ang = latlongToMeters (new Vector2 (x, 0.3f));
		 * 
		 * instance[1].transform.idt (); instance[1].transform.rotate (quat);
		 * instance[1].transform.rotateRad (0, 0, -1, ang.x);
		 * instance[1].transform.rotateRad (0, 1, 0, ang.y);
		 * instance[1].transform.translate (-15f, 0, 0); modelBatch.render
		 * (instance[1], environment);
		 */

		/*
		 * instance.transform.idt (); instance.transform.rotate (quat);
		 * modelBatch.render (instance, environment); instance2.transform.idt
		 * (); instance2.transform.rotate (quat); instance2.transform.translate
		 * (0, 0, 1.5f); modelBatch.render (instance2, environment);
		 */

		modelBatch.end ();

		modelBatch.begin (cam1);

		/*
		 * Vector3 sn = new Vector3 (snakePos); sn.mul (quat);
		 * 
		 * Vector3 sn2 = new Vector3 (sn); //sn2.nor ();
		 * 
		 * float d = intersectPlane (new Vector3 (0, 1, 0), new Vector3 (0,
		 * 0.3f, 0), new Vector3 (0, 0, 0), sn2);
		 * 
		 * sn2.mul (d);
		 * 
		 * Log.d ("KD", "D " + d + " " + sn2);
		 * 
		 * sn.x = Math.min (1, Math.max (-1, sn.x)); sn.y = Math.min (1,
		 * Math.max (-1, sn.y)); //Log.d ("KD", "" + snakePos + " " + sn);
		 * 
		 * instance2.transform.idt (); instance2.transform.translate (sn.x / 2f,
		 * sn.y / 2f, 0); instance2.transform.translate (sn2.x, sn2.y, 0); if (d
		 * != 999999 && sn2.z < 2) { modelBatch.render (instance2, environment);
		 * }
		 */

		modelBatch.end ();

	}

	float intersectPlane (Vector3 n, Vector3 p0, Vector3 l0, Vector3 l)
	{
		// assuming vectors are all normalized
		float denom = n.dot (l);
		if (denom > 1e-6)
		{
			Vector3 p0l0 = new Vector3 (p0);
			p0l0.sub (l0);
			float d = p0l0.dot (n) / denom;
			return d;
		}
		return 999999;
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

			gX = gY = gZ = 0;

		}
		//stabilize World
		else if (hasA && hasM && !isStabilized)
		{
			float diff = 0.0005f;
			mad.MadgwickAHRSupdate (0, 0, 0, aX, aY, aZ, mX, mY, mZ, 0.02f * 100);

			/*Log.d ("KD",
					"" + (Math.abs (mad.q0 - oq0) <= diff) + " "
							+ (Math.abs (mad.q1 - oq1) <= diff) + " "
							+ (Math.abs (mad.q2 - oq2) <= diff) + " "
							+ (Math.abs (mad.q3 - oq3) <= diff));
			*/

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
		player.calc (snakeAngInc);

		//check player collision
		foodManager.checkCollison (player);

		//calc enemies
		for (Enemy e : enemies)
		{
			if (foodManager.checkCollison (e) || e.currentTarget == null)
			{
				e.currentTarget = foodManager.foodPositions.get (rand.nextInt () % foodManager.foodPositions.size ());
			}
			if (e.currentTarget.dot (e.moveDir) > 0)
			{
				e.calc (0.02f);
			}
			else
			{
				e.calc (-0.02f);
			}

		}

		// send data
		int type = Byte.SIZE * 1;
		int lengths = Short.SIZE * 3;
		int playerTailSize = 3 * Float.SIZE * player.tail.size ();
		int foodSize = 3 * Float.SIZE * foodManager.foodPositions.size ();
		int enemiesSize = 0;
		for (Enemy e : enemies)
		{
			enemiesSize += Short.SIZE * 1 + 3 * Float.SIZE * e.tail.size (); // length + vectors
		}

		ByteBuffer bArray = ByteBuffer.allocate (type + lengths + playerTailSize + foodSize + enemiesSize);
		bArray.order (ByteOrder.LITTLE_ENDIAN);

		bArray.put (Sender.TYPE_OBJ); // type

		bArray.putShort ((short)player.tail.size ()); // player size
		bArray.putShort ((short)foodManager.foodPositions.size ()); // food size
		bArray.putShort ((short)enemies.size ()); // enemies count

		//put player
		for (Vector3 v : player.tail)
		{
			bArray.putFloat (v.x);
			bArray.putFloat (v.y);
			bArray.putFloat (v.z);
		}
		//put food
		for (Vector3 v : foodManager.foodPositions)
		{
			bArray.putFloat (v.x);
			bArray.putFloat (v.y);
			bArray.putFloat (v.z);
		}
		//put enemies
		for (Enemy e : enemies)
		{
			bArray.putShort ((short)e.tail.size ()); //current enemy length
			for (Vector3 v : e.tail)
			{
				bArray.putFloat (v.x);
				bArray.putFloat (v.y);
				bArray.putFloat (v.z);
			}
		}

		sender.sendData (bArray.array ());

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
		// Log.d ("KD", "do " + leftPressed + " " + rightPressed + " " +
		// lastPressed);
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
		// Log.d ("KD", "up " + leftPressed + " " + rightPressed + " " +
		// lastPressed);
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