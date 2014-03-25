package pl.jakd.tg_project;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
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

public class LevelScreen extends ScreenAdapter implements SensorEventListener, InputProcessor
{
	public OrthographicCamera cam1;
	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model[] = new Model[10];
	public ModelInstance instance[] = new ModelInstance[10];
	public Model model2, modelSnakePart, modelFood;
	public ModelInstance instance2, instSnakePart, instFood;
	public Environment environment;
	public CameraInputController camController;
	public BitmapFont font;
	public SpriteBatch batch;

	private Quaternion quat = new Quaternion ();

	private GameSnake game;
	private Context ctx;
	private SensorManager mSensorManager;
	private Sensor mAccel, mGyro, mMagnet;

	float aX = 0, aY = 0, aZ = 0, gX = 0, gY = 0, gZ = 0, mX = 0, mY = 0, mZ = 0;
	Boolean hasA = false, hasG = false, hasM = false;
	Mad mad;

	public LevelScreen (GameSnake game, Context ctx)
	{
		this.game = game;
		this.ctx = ctx;

		mad = new Mad ();
		//System.loadLibrary ("mad");
		Gdx.input.setCatchBackKey(true);
	}

	Vector3 foodPoints[] = new Vector3[100];

	Vector3 dir = new Vector3 (0, 0, 1);
	Vector3 snakePos = new Vector3 (1, 0, 0.2f);

	final float snakeSphereSize = 0.5f / 15f;
	final float foodSphereSize = 0.5f / 15f;

	PointLight light = new PointLight ();

	@Override
	public void show ()
	{
		font = new BitmapFont ();
		batch = new SpriteBatch ();
		modelBatch = new ModelBatch ();

		cam1 = new OrthographicCamera (1, 1);
		cam1.near = -100f;
		cam1.far = 300f;
		cam1.position.set (0f, 0f, 1);
		cam1.lookAt (0, 0, 0);
		cam1.update ();

		cam = new PerspectiveCamera (50, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
		cam.position.set (3f, 3f, 3f);
		cam.position.set (0f, 0f, 30f);
		cam.position.set (0f, 0f, 0f);
		cam.lookAt (0, 0, 0);
		cam.near = 0.1f;
		cam.far = 300f;
		cam.update ();

		camController = new CameraInputController (cam);
		//Gdx.input.setInputProcessor (camController);
		Gdx.input.setInputProcessor (this);

		ModelBuilder modelBuilder = new ModelBuilder ();
		model[0] = modelBuilder.createBox (1f, 1f, 5f, new Material (ColorAttribute.createDiffuse (Color.RED)), Usage.Position | Usage.Normal);
		instance[0] = new ModelInstance (model[0]);

		model[1] = modelBuilder.createBox (1f, 5f, 1f, new Material (ColorAttribute.createDiffuse (Color.GREEN)), Usage.Position | Usage.Normal);
		instance[1] = new ModelInstance (model[1]);

		model[2] = modelBuilder.createBox (5f, 1f, 1f, new Material (ColorAttribute.createDiffuse (Color.BLUE)), Usage.Position | Usage.Normal);
		instance[2] = new ModelInstance (model[2]);

		//model2 = modelBuilder.createBox (2f, 2f, 2f, new Material (ColorAttribute.createDiffuse (Color.RED)), Usage.Position | Usage.Normal);
		//instance2 = new ModelInstance (model2);

		model2 = modelBuilder.createSphere (
				0.1f, 0.1f, 0.1f, 10, 10,
				new Material (ColorAttribute.createDiffuse (Color.BLUE)), Usage.Position | Usage.Normal);
		instance2 = new ModelInstance (model2);

		modelSnakePart = modelBuilder.createSphere (
				snakeSphereSize, snakeSphereSize, snakeSphereSize, 10, 10,
				new Material (ColorAttribute.createDiffuse (Color.GREEN)), Usage.Position | Usage.Normal);
		instSnakePart = new ModelInstance (modelSnakePart);

		modelFood = modelBuilder.createSphere (
				foodSphereSize, foodSphereSize, foodSphereSize, 10, 10,
				new Material (ColorAttribute.createDiffuse (Color.RED)), Usage.Position | Usage.Normal);
		instFood = new ModelInstance (modelFood);

		environment = new Environment ();
		environment.set (new ColorAttribute (ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		//environment.add (new DirectionalLight ().set (0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
		environment.add (light.set (0.8f, 0.8f, 0.8f, 2f, 0f, 0f, 5));

		mSensorManager = (SensorManager)ctx.getSystemService (Context.SENSOR_SERVICE);
		mAccel = mSensorManager.getDefaultSensor (Sensor.TYPE_ACCELEROMETER);
		mGyro = mSensorManager.getDefaultSensor (Sensor.TYPE_GYROSCOPE);
		mMagnet = mSensorManager.getDefaultSensor (Sensor.TYPE_MAGNETIC_FIELD);

		mSensorManager.registerListener (this, mAccel, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener (this, mGyro, SensorManager.SENSOR_DELAY_FASTEST);
		mSensorManager.registerListener (this, mMagnet, SensorManager.SENSOR_DELAY_FASTEST);

		Random r = new Random ();

		for (int i = 0; i < 100; i++)
		{
			foodPoints[i] = new Vector3 (r.nextFloat () * 2 - 1, r.nextFloat () * 2 - 1, r.nextFloat () * 2 - 1);
			foodPoints[i] = foodPoints[i].nor ();
			//foodPoints[i] = foodPoints[i].mul (20);
		}

		Timer.schedule (new Timer.Task ()
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
	ArrayList<Vector3> points = new ArrayList<Vector3> ();
	float snakeAng = 0, snakeAngInc = 0;

	@Override
	public void render (float delta)
	{
		//mad (1, 1, 1, 2, 2, 2, 3, 3, 3);
		Gdx.gl.glViewport (0, 0, Gdx.graphics.getWidth (), Gdx.graphics.getHeight ());
		Gdx.gl.glClear (GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		quat.set (mad.q1, mad.q2, mad.q3, -mad.q0);

		batch.begin ();
		font.drawMultiLine (batch, getOrientationString (), 20, Gdx.graphics.getHeight () - 10);
		batch.end ();

		Vector3 lightPos = new Vector3 (2, 0, 0);
		lightPos.mul (quat);
		light.position.set (lightPos);

		modelBatch.begin (cam);

		for (int i = 0; i < 3; i++)
		{
			instance[i].transform.idt ();
			instance[i].transform.scl (0.3f);
			instance[i].transform.rotate (quat);
			//modelBatch.render (instance[i], environment);
		}

		for (int i = 0; i < 1; i++)
		{
			Vector3 snakePosNorm = new Vector3 (snakePos);
			snakePosNorm.nor ();

			//Log.d ("KD", "st " + leftPressed + " " + rightPressed + " " + lastPressed);
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
			else
			{
				snakeAngInc = 0;
			}

			dir.rotateRad (snakePosNorm, snakeAngInc * 5);

			Vector3 dir2 = new Vector3 (dir).mul (0.01f);
			Vector3 newPt = new Vector3 (snakePos).add (dir2);
			newPt.nor ();

			dir = new Vector3 (newPt).sub (snakePos);
			dir.nor ();

			snakePos = newPt;
			points.add (snakePos);
			if (points.size () > 100)
				points.remove (0);
		}

		for (int i = 100 - 1; i >= 0; i--)
		{
			float lensq = new Vector3 (snakePos).sub (foodPoints[i]).len2 ();

			float maxDiff = foodSphereSize / 2f + snakeSphereSize / 2f;
			if (lensq < maxDiff * maxDiff)
			{
				foodPoints[i].x = 10000;

				continue;
			}
			instFood.transform.idt ();
			instFood.transform.rotate (quat);
			instFood.transform.translate (foodPoints[i]);
			modelBatch.render (instFood, environment);
		}

		for (int i = 0; i < points.size (); i++)
		{
			instSnakePart.transform.idt ();
			instSnakePart.transform.rotate (quat);
			instSnakePart.transform.translate (points.get (i));

			//instSnakePart.transform.scale (1, 1.0f / 5.0f, 1);
			modelBatch.render (instSnakePart, environment);
		}

		instance[1].transform.idt ();
		instance[1].transform.translate (-15f, 0, 0);
		modelBatch.render (instance[1], environment);

		/*Vector3 ang = latlongToMeters (new Vector2 (x, 0.3f));

		instance[1].transform.idt ();
		instance[1].transform.rotate (quat);
		instance[1].transform.rotateRad (0, 0, -1, ang.x);
		instance[1].transform.rotateRad (0, 1, 0, ang.y);
		instance[1].transform.translate (-15f, 0, 0);
		modelBatch.render (instance[1], environment);*/

		/*instance.transform.idt ();
		instance.transform.rotate (quat);
		modelBatch.render (instance, environment);
		instance2.transform.idt ();
		instance2.transform.rotate (quat);
		instance2.transform.translate (0, 0, 1.5f);
		modelBatch.render (instance2, environment);*/

		modelBatch.end ();

		modelBatch.begin (cam1);

		/*Vector3 sn = new Vector3 (snakePos);
		sn.mul (quat);

		Vector3 sn2 = new Vector3 (sn);
		//sn2.nor ();

		float d = intersectPlane (new Vector3 (0, 1, 0), new Vector3 (0, 0.3f, 0), new Vector3 (0, 0, 0), sn2);

		sn2.mul (d);

		Log.d ("KD", "D " + d + " " + sn2);

		sn.x = Math.min (1, Math.max (-1, sn.x));
		sn.y = Math.min (1, Math.max (-1, sn.y));
		//Log.d ("KD", "" + snakePos + " " + sn);

		instance2.transform.idt ();
		instance2.transform.translate (sn.x / 2f, sn.y / 2f, 0);
		instance2.transform.translate (sn2.x, sn2.y, 0);
		if (d != 999999 && sn2.z < 2)
		{
			modelBatch.render (instance2, environment);
		}*/

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

	private String getOrientationString ()
	{
		StringBuilder builder = new StringBuilder ();
		builder.append ("\nazimuth: ");
		builder.append ((int)Gdx.input.getAzimuth ());
		builder.append ("\npitch: ");
		builder.append ((int)Gdx.input.getPitch ());
		builder.append ("\nroll: ");
		builder.append ((int)Gdx.input.getRoll ());
		return builder.toString ();
	}

	@Override
	public void onAccuracyChanged (Sensor sensor, int accuracy)
	{
		// TODO Auto-generated method stub

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

	public void calc ()
	{
		if (hasA && hasM)
		{
			long ticks = System.currentTimeMillis ();
			if (lastCalc == 0)
				lastCalc = ticks;
			float diff = (float)(ticks - lastCalc) / 1000.0f;
			lastCalc = ticks;

			//Log.d ("KD",
			//		String.format ("ax %10f, ay %10f, az %10f, mx %10f, my %10f, mz %10f, gx %10f, gy %10f, gz %10f", aX, aY, aZ, mX, mY, mZ, gX, gY, gZ));

			mad.MadgwickAHRSupdate (gX, gY, gZ, aX, aY, aZ, mX, mY, mZ, 0.02f);
			gX = gY = gZ = 0;
		}
	}
	@Override
	public boolean keyDown (int keycode)
	{
		if(keycode == Keys.BACK){
			Gdx.input.setCatchBackKey(false);
			game.setScreen(new MainMenu(game));
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

	Boolean leftPressed = false, rightPressed = false;
	int lastPressed = 0;

	@Override
	public boolean touchDown (int arg0, int arg1, int arg2, int arg3)
	{
		//Log.d ("KD", "do " + leftPressed + " " + rightPressed + " " + lastPressed);
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
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean touchUp (int arg0, int arg1, int arg2, int arg3)
	{
		//Log.d ("KD", "up " + leftPressed + " " + rightPressed + " " + lastPressed);
		if (arg0 < Gdx.app.getGraphics ().getWidth () / 2)
			leftPressed = false;
		else
			rightPressed = false;
		return false;
	}
}