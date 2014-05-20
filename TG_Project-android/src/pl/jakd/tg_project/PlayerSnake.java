package pl.jakd.tg_project;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class PlayerSnake extends Snake
{
	public static final int STARTING_LIVES = 3;
	
	private float snakeAngleInc;
	private int lives = STARTING_LIVES;
	private int score = 0;

	public PlayerSnake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		super (startPos, startDir, snakePartInstance);
	}

	public ECalcResult calc ()
	{
		moveDir.rotateRad (getCurrentPosition (), snakeAngleInc * 5);

		AdvancePositionResult apr = advancePosition ();
		if (apr.result == ECalcResult.COLLIDED)
		{
			if (lives-- > 0)
			{
				tail = new ArrayList<Vector3> (tail.subList (0, apr.collidedIndex));
				length = (short)tail.size ();
			}
			else
			{
				return ECalcResult.COLLIDED;
			}
		}
		return ECalcResult.NOT_COLLIDED;
	}

	public void setMoveAngle (float angle)
	{
		this.snakeAngleInc = angle;
	}

	public int getScore ()
	{
		score = tail.size () - SNAKE_START_LENGTH;
		return score;
	}

	public int getLives ()
	{
		return lives;
	}

	public void shrink ()
	{
		lives--;
		tail = new ArrayList<Vector3> (tail.subList (0, SNAKE_START_LENGTH));
		length = (short)tail.size ();
	}
}
