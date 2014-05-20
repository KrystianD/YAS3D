package pl.jakd.tg_project;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.math.Vector3;

public class Utils
{
	public static Random rand = new Random ();

	public enum ECollisionResult
	{
		PLAYER_COLLIDED,
		PLAYER_NOT_COLLIDED
	}

	public static float intersectPlane (Vector3 n, Vector3 p0, Vector3 l0, Vector3 l)
	{
		// assuming vectors are all normalized
		float denom = n.dot (l);
		if (denom > 1e-6)
		{
			Vector3 p0l0 = new Vector3 (p0);
			p0l0.sub (l0);
			float d = p0l0.dot (n) / denom;
			return d;
		}
		return 999999;
	}

	public static ECollisionResult checkCollision (PlayerSnake player, ArrayList<Enemy> enemies, ArrayList<Wall> walls)
	{
		for (Enemy e : enemies)
		{
			for (Enemy ec : enemies)
			{
				if (e == ec)
					continue;
				if (ec.collideWithPoint (e.getCurrentPosition ()))
				{
					e.reset ();
				}
			}
		}

		for (Enemy e : enemies)
		{
			if (player.collideWithPoint (e.getCurrentPosition ()))
			{
				e.reset ();
			}
			else if (e.collideWithPoint (player.getCurrentPosition ()))
			{
				return ECollisionResult.PLAYER_COLLIDED;
			}
		}

		for (Wall w : walls)
		{
			if (w.collideWithPoint (player.getCurrentPosition ()))
			{
				if (player.getLives () > 0)
				{
					player.shrink ();
					while (w.collideWithPoint (player.getCurrentPosition ()))
					{
						player.calc ();
					}
					return ECollisionResult.PLAYER_NOT_COLLIDED;
				}
				else
				{
					return ECollisionResult.PLAYER_COLLIDED;
				}
			}
			
			if(player.collideWithPoint (w.getCurrentPosition ())){
				
			}
			

			for (Enemy e : enemies)
			{
				if (w.collideWithPoint (e.getCurrentPosition ()))
				{
					e.reset ();
				}
			}
		}

		return ECollisionResult.PLAYER_NOT_COLLIDED;
	}
	public static Vector3 randSpherePoint ()
	{
		return new Vector3 (rand.nextFloat () * 2 - 1,
				rand.nextFloat () * 2 - 1, rand.nextFloat () * 2 - 1).nor ();
	}
}
