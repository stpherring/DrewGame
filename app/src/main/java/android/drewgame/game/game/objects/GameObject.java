package android.drewgame.game.game.objects;

import android.drewgame.game.graphics.Rectangle;
import android.drewgame.game.graphics.Vector2;

public class GameObject
{
	public final Vector2 position;
	public Rectangle bounds;
	
	public GameObject(float x, float y, float width, float height)
	{
		this.position = new Vector2(x, y);
		this.bounds = new Rectangle(x, y, width, height);
	}
}
