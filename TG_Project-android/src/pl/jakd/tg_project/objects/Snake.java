package pl.jakd.tg_project.objects;

import java.util.ArrayList;
import java.util.Random;

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
	public static final int SNAKE_START_LENGTH = 5;

	protected static final float SPHERES_DISTANCE = 0.01f;

	public enum ECalcResult
	{
		COLLIDED,
		NOT_COLLIDED
	}

	public ArrayList<Vector3> tail = new ArrayList<Vector3> ();

	protected Vector3 moveDir;
	protected short length = SNAKE_START_LENGTH; // start length
	protected Random rand = new Random ();

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
	}

	public abstract ECalcResult calc ();

	class AdvancePositionResult
	{
		public ECalcResult result;
		public int collidedIndex;
	}

	protected AdvancePositionResult advancePosition ()
	{
		AdvancePositionResult result = new AdvancePositionResult ();

		Vector3 dir2 = new Vector3 (moveDir).mul (SPHERES_DISTANCE);
		Vector3 newPt = new Vector3 (getCurrentPosition ()).add (dir2);
		newPt.nor ();

		moveDir = new Vector3 (newPt).sub (getCurrentPosition ());
		moveDir.nor ();

		tail.add (0, newPt);
		if (tail.size () > length)
			tail.remove (tail.size () - 1);

		for (int i = 10; i < tail.size (); i++)
		{
			if (tail.get (i).dst2 (getCurrentPosition ()) < (SNAKE_SPHERE_SIZE * SNAKE_SPHERE_SIZE))
			{
				result.collidedIndex = i;
				result.result = ECalcResult.COLLIDED;
				return result;
			}
		}
		result.result = ECalcResult.NOT_COLLIDED;
		return result;
	}

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

	public int collideWithPoint (Vector3 point)
	{
		for (Vector3 v : tail)
			if (point.dst2 (v) < SNAKE_SPHERE_SIZE * SNAKE_SPHERE_SIZE)
				return tail.indexOf (v);
		return -1;
	}

	public void cutTo (int colsPos)
	{
		if (colsPos < SNAKE_START_LENGTH)
			colsPos = SNAKE_START_LENGTH;
		tail = new ArrayList<Vector3> (tail.subList (0, colsPos));
		length = (short)tail.size ();
	}
}
