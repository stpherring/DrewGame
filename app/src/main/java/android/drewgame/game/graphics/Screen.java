package android.drewgame.game.graphics;

import android.drewgame.game.game.Game;

public abstract class Screen
{
	protected final Game game;
	
	public Screen(Game game)
	{
		this.game = game;
	}
	
	public abstract void update(float deltaTime);
	
	public abstract void present(float[] mMVPMatrix);
	
	public abstract void pause();
	
	public abstract void resume();
	
	public abstract void dispose();
}

