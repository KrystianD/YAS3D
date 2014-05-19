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

	public ECalcResult calc ()
	{
		moveDir.rotateRad (getCurrentPosition (), snakeAngleInc * 5);
		return advancePosition ();
	}

	public void setMoveAngle (float angle)
	{
		this.snakeAngleInc = angle;
	}
}
