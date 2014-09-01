package pl.jakd.tg_project.utils;

import java.util.ArrayList;
import java.util.Random;

import pl.jakd.tg_project.objects.Enemy;
import pl.jakd.tg_project.objects.PlayerSnake;
import pl.jakd.tg_project.objects.Wall;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * klasa zawierająca pomocnicze metody
 */
public class Utils
{
	public static Random rand = new Random ();

	/**
	 * wynik kolizji gracza z elementami na planszy (za wyjątkiem obiektów food) 
	 */
	public enum ECollisionResult
	{
		PLAYER_COLLIDED,
		PLAYER_NOT_COLLIDED
	}
	
	/**
	 * sprawdza, czy gracz koliduje z obiektami na planszy
	 * @param player obiekt gracza
	 * @param enemies lista przeciwników
	 * @param walls lista ścian
	 * @return wynik kolizji
	 */
	public static ECollisionResult checkCollision (PlayerSnake player, ArrayList<Enemy> enemies, ArrayList<Wall> walls)
	{
		for (Enemy e : enemies)
		{
			for (Enemy ec : enemies)
			{
				if (e == ec)
					continue;
				if (ec.collideWithPoint (e.getCurrentPosition ()) != -1)
				{
					e.reset ();
				}
			}
		}

		for (Enemy e : enemies)
		{
			if (player.collideWithPoint (e.getCurrentPosition ()) != -1)
			{
				e.reset ();
			}
			else if (e.collideWithPoint (player.getCurrentPosition ()) != -1)
			{
				return ECollisionResult.PLAYER_COLLIDED;
			}
		}

		for (Wall w : walls)
		{
			if (w.collideWithPoint (player.getCurrentPosition ()) != -1)
			{
				if (player.getLives () > 0)
				{
					player.shrink ();
					while (w.collideWithPoint (player.getCurrentPosition ()) != -1)
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

			int colsPos = player.collideWithPoint (w.getCurrentPosition ());
			if (colsPos != -1)
			{
				player.cutTo (colsPos);
			}

			for (Enemy e : enemies)
			{
				if (w.collideWithPoint (e.getCurrentPosition ()) != -1)
				{
					e.reset ();
				}

				colsPos = e.collideWithPoint (w.getCurrentPosition ());
				if (colsPos != -1)
				{
					e.cutTo (colsPos);
				}
			}
		}

		return ECollisionResult.PLAYER_NOT_COLLIDED;
	}
	
	/**
	 * losuje punkt na sferze
	 * @return losowy punkt na sferze
	 */
	public static Vector3 randSpherePoint ()
	{
		return new Vector3 (rand.nextFloat () * 2 - 1,
				rand.nextFloat () * 2 - 1, rand.nextFloat () * 2 - 1).nor ();
	}

	/**
	 * przekształca pozycję z współrzędnych geograficznych na metry
	 * @param pos pozycja we współrzędnych geograficznych
	 * @return pozycja w metrach
	 */
	public static Vector2 latlongToMeters (Vector2 pos)
	{
		float longtitude = pos.y;
		float latitude = pos.x / (float)Math.cos (longtitude);
		return new Vector2 (latitude, longtitude);
	}

	
	/**
	 * Tworzy i zwraca obiekt mesh planszy otaczającej gracza
	 * @return mesh planszy
	 */
	public static Mesh createUniverse ()
	{
		double a, b;
		double R = 10;
		double space = 40;
		int n = 0;
		short ni = 0;
		double PI = Math.PI;
		float[] vert = new float[(int)((180.0 / space) * (360.0 / space) * 4.0) * 5];
		short[] indices = new short[(int)((180.0 / space) * (360.0 / space) * 4.0)];
		float D = 5;

		for (b = 0; b <= 180 - space; b += space)
		{
			for (a = 0; a <= 360 - space; a += space)
			{
				vert[n++] = (float)(R * Math.sin ((a) / 180 * Math.PI) * Math.sin ((b) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((a) / 180 * Math.PI) * Math.sin ((b) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((b) / 180 * Math.PI));
				vert[n++] = (float)((2 * b) / 360) * D;
				vert[n++] = (float)((a) / 360) * D;
				indices[ni] = ni;
				ni++;

				vert[n++] = (float)(R * Math.sin ((a) / 180 * Math.PI) * Math.sin ((b + space) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((a) / 180 * Math.PI) * Math.sin ((b + space) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((b + space) / 180 * Math.PI));
				vert[n++] = (float)((2 * (b + space)) / 360) * D;
				vert[n++] = (float)((a) / 360) * D;
				indices[ni] = ni;
				ni++;

				vert[n++] = (float)(R * Math.sin ((a + space) / 180 * Math.PI) * Math.sin ((b) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((a + space) / 180 * Math.PI) * Math.sin ((b) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((b) / 180 * Math.PI));
				vert[n++] = (float)((2 * b) / 360) * D;
				vert[n++] = (float)((a + space) / 360) * D;
				indices[ni] = ni;
				ni++;

				vert[n++] = (float)(R * Math.sin ((a + space) / 180 * Math.PI) * Math.sin ((b + space) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((a + space) / 180 * Math.PI) * Math.sin ((b + space) / 180 * PI));
				vert[n++] = (float)(R * Math.cos ((b + space) / 180 * Math.PI));
				vert[n++] = (float)((2 * (b + space)) / 360) * D;
				vert[n++] = (float)((a + space) / 360) * D;
				indices[ni] = ni;
				ni++;
			}
		}

		Mesh mesh = new Mesh (true, vert.length / 5, indices.length,
				new VertexAttribute (Usage.Position, 3, ShaderProgram.POSITION_ATTRIBUTE),
				new VertexAttribute (Usage.TextureCoordinates, 2, ShaderProgram.TEXCOORD_ATTRIBUTE + "0"));

		mesh.setIndices (indices);
		mesh.setVertices (vert);

		return mesh;
	}
}
