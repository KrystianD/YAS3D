package pl.jakd.tg_project.objects;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * klasa reprezentująca obiekt gracza w grze
 */
public class PlayerSnake extends Snake
{
	public static final int STARTING_LIVES = 3;

	private float snakeAngleInc;
	private int lives = STARTING_LIVES;
	private int score = 0;

	/**
	 * @param startPos pozycja początkowa gracza
	 * @param startDir początkowy kierunek ruchu gracza
	 * @param snakePartInstance wyświetlany element reprezentujący fragment węża
	 */
	public PlayerSnake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		super (startPos, startDir, snakePartInstance);
	}

	@Override
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

	/**
	 * ustawia kąt obrotu gracza
	 * @param angle kąt obrotu
	 */
	public void setMoveAngle (float angle)
	{
		this.snakeAngleInc = angle;
	}

	
	/**
	 * @return aktualny wynik gracza
	 */
	public int getScore ()
	{
		score = tail.size () - SNAKE_START_LENGTH;
		return score;
	}

	/**
	 * @return ilość żyć gracza
	 */
	public int getLives ()
	{
		return lives;
	}

	/**
	 * natychmiastowo zabija gracza
	 */
	public void kill ()
	{
		lives = 0;
	}

	/**
	 * zmniejsza ilość życia i ustawia początkową długość gracza
	 */
	public void shrink ()
	{
		lives--;
		cutTo (SNAKE_START_LENGTH);
	}
}
