package pl.jakd.tg_project;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class FoodManager
{
	public static final float FOOD_SPHERE_SIZE = 0.5f / 15f;

	public ArrayList<Vector3> foodPositions = new ArrayList<Vector3> ();

	private ModelInstance foodInstance;
	private Random rand = new Random ();

	public FoodManager (int count, ModelInstance foodInstance)
	{
		this.foodInstance = foodInstance;

		//create food
		for (int i = 0; i < count; i++)
		{
			foodPositions.add (new Vector3 (rand.nextFloat () * 2 - 1,
					rand.nextFloat () * 2 - 1, rand.nextFloat () * 2 - 1).nor ());
		}
	}

	public void render (ModelBatch modelBatch, Quaternion worldQuat, Environment env)
	{
		for (int i = 100 - 1; i >= 0; i--)
		{
			foodInstance.transform.idt ();
			foodInstance.transform.rotate (worldQuat);
			foodInstance.transform.translate (foodPositions.get (i));
			modelBatch.render (foodInstance, env);
		}
	}

	public boolean checkCollison (Snake snake)
	{
		for (int i = 0; i < foodPositions.size (); i++)
		{
			float lensq = new Vector3 (snake.getCurrentPosition ()).sub (foodPositions.get (i)).len2 ();

			float maxDiff = FOOD_SPHERE_SIZE / 2f + PlayerSnake.SNAKE_SPHERE_SIZE / 2f;
			maxDiff *= 1.1;
			if (lensq < maxDiff * maxDiff)
			{
				Vector3 food = foodPositions.get (i);
				food.x = rand.nextFloat () * 2 - 1;
				food.y = rand.nextFloat () * 2 - 1;
				food.z = rand.nextFloat () * 2 - 1;
				food.nor ();
				return true;
			}
		}
		return false;
	}

}