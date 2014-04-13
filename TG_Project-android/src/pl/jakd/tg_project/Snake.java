package pl.jakd.tg_project;

import java.util.ArrayList;

import android.util.Log;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class Snake
{
	public static final float SNAKE_SPHERE_SIZE = 0.5f / 15f;
	public static final int SNAKE_MAX_SIZE = 100;

	public ArrayList<Vector3> tail = new ArrayList<Vector3> ();

	protected Vector3 moveDir;
	protected short length = 100; // start length
	
	private ModelInstance snakePartInstance;
	private boolean isDead = false;
	
	
	public Snake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		tail.add (startPos);
		moveDir = startDir;
		this.snakePartInstance = snakePartInstance;
	}

	public void render (ModelBatch modelBatch, Quaternion worldQuat, Environment env, Frustum f)
	{
		//long start = System.currentTimeMillis ();
		for (Vector3 v : tail)
		{
			snakePartInstance.transform.idt ();
			snakePartInstance.transform.rotate (worldQuat);
			snakePartInstance.transform.translate (v);

			Vector3 vq = new Vector3 (v).mul (worldQuat);
			if (f.sphereInFrustum (vq, SNAKE_SPHERE_SIZE))
			{
				modelBatch.render (snakePartInstance, env);
			}
		}
		//Log.d ("KD", "SNAKE RENDER TIME = " + (System.currentTimeMillis () - start));
	}

	public abstract void calc ();

	public Vector3 getCurrentPosition ()
	{
		return tail.get (0);
	}

	public void grow ()
	{
		length += 5;
		if (length > SNAKE_MAX_SIZE)
			length = SNAKE_MAX_SIZE;
	}

}
