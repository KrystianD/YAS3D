package pl.jakd.tg_project.objects;

import java.util.ArrayList;

import pl.jakd.tg_project.utils.Utils;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * klasa reprezentująca przeciwnika w grze
 */
public class Enemy extends Snake
{
	public static final float DIFF = 0.1f;
	public Vector3 currentTarget = null;
	public float lastDistanceToTarget;
	public long timeNoChangeDist;
	public int needNewFood = 0;

	
	/**
	 * @param snakePartInstance wyświetlany element reprezentujący fragment węża
	 */
	public Enemy (ModelInstance snakePartInstance)
	{
		super (new Vector3 (), new Vector3 (), snakePartInstance);
	}

	@Override
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

		AdvancePositionResult apr = advancePosition ();
		if (apr.result == ECalcResult.COLLIDED)
		{
			tail = new ArrayList<Vector3> (tail.subList (0, apr.collidedIndex));
			length = (short)tail.size ();
		}
		return ECalcResult.NOT_COLLIDED;
	}

	
	/**
	 * zwraca kierunek poruszania się
	 * @return wektor kierunku ruchu
	 */
	public Vector3 getMoveDir ()
	{
		return moveDir;
	}

	/**
	 * ustawia kierunek ruchu
	 * @param moveDir wektor kierunku ruchu
	 */
	public void setMoveDir (Vector3 moveDir)
	{
		this.moveDir = moveDir;
	}

	/**
	 * resetuje przeciwnika
	 */
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
