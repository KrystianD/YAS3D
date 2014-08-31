package pl.jakd.tg_project.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * klasa reprezentująca ścianę w grze
 */
public class Wall extends Snake
{
	public static final float DIFF = 0.1f;
	public int speed;
	public int counter;

	ArrayList<Vector3> moveDirs = new ArrayList<Vector3> ();

	/**
	 * @param startPos pozycja startowa
	 * @param dir kierunek startowy
	 * @param start początek walla
	 * @param end długość walla
	 * @param speed prędkość poruszania ściany
	 * @param snakePartInstance wyświetlany element reprezentujący fragment węża
	 */
	public Wall (Vector3 startPos, Vector3 dir, float start, float end, int speed, ModelInstance snakePartInstance)
	{
		super (startPos, dir, snakePartInstance);
		this.speed = speed;

		tail.clear ();

		float step = dir.len () * SPHERES_DISTANCE;
		for (float a = 0.0f; a < end; a += step)
		{
			Vector3 dir2 = new Vector3 (moveDir).mul (SPHERES_DISTANCE);
			Vector3 newPt = new Vector3 (startPos).add (dir2);
			newPt.nor ();

			moveDir = new Vector3 (newPt).sub (startPos);
			moveDir.nor ();

			if (a >= start)
			{
				tail.add (0, newPt);
				moveDirs.add (0, moveDir);
			}
			startPos = newPt;
		}
	}
	
	@Override
	public ECalcResult calc ()
	{
		if (counter++ >= speed)
		{
			counter = 0;
			advancePosition ();
		}
		return ECalcResult.NOT_COLLIDED;
	}
}
