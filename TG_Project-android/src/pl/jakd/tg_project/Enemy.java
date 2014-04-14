package pl.jakd.tg_project;

import android.util.Log;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Enemy extends Snake
{
	public static final float DIFF = 0.1f;
	public Vector3 currentTarget = null;
	public float lastDistanceToTarget;
	public long timeNoChangeDist;
	public boolean needNewFood = true;

	public Enemy (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		super (startPos, startDir, snakePartInstance);
	}

	public void calc ()
	{

		//long start = System.currentTimeMillis ();
		if (currentTarget != null)
		{
			float distance = currentTarget.dst (getCurrentPosition ());
			if (Math.abs (distance - lastDistanceToTarget) < DIFF)
			{
				//Log.d ("KD", "ENEMY DISTANCE =" + (distance - lastDistanceToTarget));
				needNewFood = true;
			}
			lastDistanceToTarget = distance;
		}

		Vector3 currentSnakePosition = getCurrentPosition ();

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

	public Vector3 getMoveDir ()
	{
		return moveDir;
	}

	public void setMoveDir (Vector3 moveDir)
	{
		this.moveDir = moveDir;
	}

}
