package android.drewgame.game.input;

import android.content.Context;
import android.view.View;

import java.util.List;

public class AndroidInput implements Input
{
	TouchHandler touchHandler;
	
	public AndroidInput(Context context, View view, float scaleX, float scaleY)
	{
		touchHandler = new MultiTouchHandler(view, scaleX, scaleY);
	}
	
	@Override
	public boolean isTouchDown(int pointer) 
	{
		return touchHandler.isTouchDown(pointer);
	}

	@Override
	public int getTouchX(int pointer) 
	{
		return touchHandler.getTouchX(pointer);
	}

	@Override
	public int getTouchY(int pointer) 
	{
		return touchHandler.getTouchY(pointer);
	}

	@Override
	public List<TouchEvent> getTouchEvents()
	{
		return touchHandler.getTouchEvents();
	}

}
