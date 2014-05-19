package pl.jakd.tg_project;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Enemy extends Snake
{
	public static final float DIFF = 0.1f;
	public Vector3 currentTarget = null;
	public float lastDistanceToTarget;
	public long timeNoChangeDist;
	public int needNewFood = 0;

	public Enemy (ModelInstance snakePartInstance)
	{
		super (new Vector3 (), new Vector3 (), snakePartInstance);
	}

	public ECalcResult calc ()
	{

		//long start = System.currentTimeMillis ();
		if (currentTarget != null)
		{
			float distance = currentTarget.dst (getCurrentPosition ());
			if (Math.abs (distance - lastDistanceToTarget) < DIFF)
			{
				//Log.d ("KD", "ENEMY DISTANCE =" + (distance - lastDistanceToTarget));
				needNewFood++;
			}
			else
			{
				needNewFood = 0;
			}
			lastDistanceToTarget = distance;
		}

		return advancePosition ();
	}

	public Vector3 getMoveDir ()
	{
		return moveDir;
	}

	public void setMoveDir (Vector3 moveDir)
	{
		this.moveDir = moveDir;
	}

	public void reset ()
	{
		Vector3 startPos = Utils.randSpherePoint ();
		Vector3 p = Utils.randSpherePoint ();

		tail.clear ();
		length = SNAKE_START_LENGTH;
		
		moveDir = startPos.crs (p).nor ();
		tail.add (startPos);
	}

}
