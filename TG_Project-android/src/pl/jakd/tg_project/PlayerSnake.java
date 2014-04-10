package pl.jakd.tg_project;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class PlayerSnake extends Snake
{
	public PlayerSnake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		super (startPos, startDir, snakePartInstance);
	}
}
