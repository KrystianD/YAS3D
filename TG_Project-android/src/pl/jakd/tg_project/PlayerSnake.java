package pl.jakd.tg_project;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class PlayerSnake extends Snake
{
	private float snakeAngleInc;

	public PlayerSnake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		super (startPos, startDir, snakePartInstance);
	}

	public void calc ()
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

	public void setMoveAngle (float angle)
	{
		this.snakeAngleInc = angle;
	}
}
