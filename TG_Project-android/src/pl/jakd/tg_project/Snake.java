package pl.jakd.tg_project;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public abstract class Snake
{
	public static final float SNAKE_SPHERE_SIZE = 0.5f / 15f;
	
	public ArrayList<Vector3> tail = new ArrayList<Vector3> ();

	public Vector3 moveDir;
	private ModelInstance snakePartInstance;
	private short length = 2; // start length
	
	public Snake (Vector3 startPos, Vector3 startDir, ModelInstance snakePartInstance)
	{
		tail.add (startPos);
		moveDir = startDir;
		this.snakePartInstance = snakePartInstance;
	}

	public void render (ModelBatch modelBatch, Quaternion worldQuat, Environment env)
	{
		for (int i = 0; i < tail.size (); i++)
		{
			snakePartInstance.transform.idt ();
			snakePartInstance.transform.rotate (worldQuat);
			snakePartInstance.transform.translate (tail.get (i));
			// instSnakePart.transform.scale (1, 1.0f / 5.0f, 1);
			modelBatch.render (snakePartInstance, env);
		}
	}

	public void calc (float snakeAngleInc)
	{

		Vector3 currentSnakePosition = getCurrentPosition ();
		Vector3 snakePosNorm = new Vector3 (currentSnakePosition);
		snakePosNorm.nor ();

		// Log.d ("KD", "st " + leftPressed + " " + rightPressed +
		// " " + lastPressed);

		moveDir.rotateRad (snakePosNorm, snakeAngleInc * 5);

		Vector3 dir2 = new Vector3 (moveDir).mul (0.01f);
		Vector3 newPt = new Vector3 (currentSnakePosition).add (dir2);
		newPt.nor ();

		moveDir = new Vector3 (newPt).sub (currentSnakePosition);
		moveDir.nor ();

		tail.add (0, newPt);
		if (tail.size () > length)
			tail.remove (tail.size () - 1);
	}
	
	public Vector3 getCurrentPosition ()
	{
		return tail.get (0);
	}
	
	public void grow(){
		length+=5;
	}

}