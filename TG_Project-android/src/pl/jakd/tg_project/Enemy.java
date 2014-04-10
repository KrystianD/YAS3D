package pl.jakd.tg_project;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

public class Enemy extends Snake
{	
	public Vector3 currentTarget = null;

	public Enemy (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		super (startPos, startDir, snakePartInstance);
	}
	
}
