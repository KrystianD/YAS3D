package pl.jakd.tg_project;

import java.util.ArrayList;

import android.util.Log;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class Snake
{
	public static final float SNAKE_SPHERE_SIZE = 0.5f / 15f;

	public ArrayList<Vector3> tail = new ArrayList<Vector3> ();

	public Vector3 moveDir;
	private ModelInstance snakePartInstance;
	private short length = 2; // start length

	public Snake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		tail.add (startPos);
		moveDir = startDir;
		this.snakePartInstance = snakePartInstance;
	}

	public void render (ModelBatch modelBatch, Quaternion worldQuat, Environment env)
	{
		//long start = System.currentTimeMillis ();
		for (Vector3 v : tail)
		{
			snakePartInstance.transform.idt ();
			snakePartInstance.transform.rotate (worldQuat);
			snakePartInstance.transform.translate (v);
			// instSnakePart.transform.scale (1, 1.0f / 5.0f, 1);
			modelBatch.render (snakePartInstance, env);
		}
		//Log.d ("KD", "SNAKE RENDER TIME = " + (System.currentTimeMillis () - start));
	}

	public void calc (float snakeAngleInc)
	{

		//long start = System.currentTimeMillis ();

		Vector3 currentSnakePosition = getCurrentPosition ();

		moveDir.rotateRad (currentSnakePosition, snakeAngleInc * 5);

		Vector3 dir2 = new Vector3 (moveDir).mul (0.01f);
		Vector3 newPt = new Vector3 (currentSnakePosition).add (dir2);
		newPt.nor ();

		moveDir = new Vector3 (newPt).sub (currentSnakePosition);
		moveDir.nor ();

		tail.add (0, newPt);
		if (tail.size () > length)
			tail.remove (tail.size () - 1);

		//Log.d ("KD", "SNAKE CALC TIME = " + (System.currentTimeMillis () - start));
	}

	public Vector3 getCurrentPosition ()
	{
		return tail.get (0);
	}

	public void grow ()
	{
		length += 5;
	}

}
