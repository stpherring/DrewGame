package android.drewgame.game.input;

import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

// 293
public class MultiTouchHandler implements TouchHandler
{
	private static final int MAX_TOUCHPOINTS = 10;
	boolean[] isTouched = new boolean[MAX_TOUCHPOINTS];
	int[] touchX = new int[MAX_TOUCHPOINTS];
	int[] touchY = new int[MAX_TOUCHPOINTS];
	int[] id = new int[MAX_TOUCHPOINTS];
	Pool<TouchEvent> touchEventPool;
	List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
	List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
	float scaleX;
	float scaleY;
	
	public MultiTouchHandler(View view, float scaleX, float scaleY)
	{
		PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>()
		{
			public TouchEvent createObject()
			{
				return new TouchEvent();
			}
		};
		
		touchEventPool = new Pool<TouchEvent>(factory, 100);
		
		view.setOnTouchListener(this);
		
		this.scaleX = scaleX;
		this.scaleY = scaleY;
	}
	
	public boolean onTouch(View v, MotionEvent event)
	{
		synchronized(this)
		{
			int action = event.getAction() & MotionEvent.ACTION_MASK;
			@SuppressWarnings("deprecation")
			int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK)
			>> MotionEvent.ACTION_POINTER_ID_SHIFT;
			int pointerCount = event.getPointerCount();
			TouchEvent touchEvent;
			
			for(int i = 0; i < MAX_TOUCHPOINTS; i++)
			{
				if(i >= pointerCount)
				{
					isTouched[i] = false;
					id[i] = -1;
					continue;
				}
				int pointerId = event.getPointerId(i);
				
				if(event.getAction() != MotionEvent.ACTION_MOVE && i != pointerIndex)
				{
					// If the event is up/down/cancel/out, mask the id to see if we should
					// process it for this touch
					continue;
				}
				switch(action)
				{
					case MotionEvent.ACTION_DOWN:
					case MotionEvent.ACTION_POINTER_DOWN:
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DOWN;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int)(event.getX() * scaleX);
						touchEvent.y = touchY[i] = (int)(event.getY() * scaleY);
						isTouched[i] = true;
						id[i] = pointerId;
						touchEventsBuffer.add(touchEvent);
						break;
					
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_POINTER_UP:
					case MotionEvent.ACTION_CANCEL:
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_UP;
						touchEvent.pointer = pointerId;
						touchEvent.x = touchX[i] = (int)(event.getX() * scaleX);
						touchEvent.y = touchY[i] = (int)(event.getY() * scaleY);
						isTouched[i] = false;
						id[i] = -1;
						touchEventsBuffer.add(touchEvent);
						break;
					
					case MotionEvent.ACTION_MOVE:
						touchEvent = touchEventPool.newObject();
						touchEvent.type = TouchEvent.TOUCH_DRAGGED;
						touchEvent.pointer = pointerId;
						touchEvent.x = (int)(event.getX(i) * scaleX);
						touchEvent.y = (int)(event.getY(i) * scaleY);
						isTouched[i] = true;
						id[i] = pointerId;
						touchEventsBuffer.add(touchEvent);
						break;
				}
			}
			
			return true;
			
		}
	}
	
	@Override
	public boolean isTouchDown(int pointer) 
	{
		synchronized(this)
		{
			int index = getIndex(pointer);
			if(index < 0 || index >= MAX_TOUCHPOINTS)
			{
				return false;
			}
			else
			{
				return isTouched[index];
			}
		}
	}

	@Override
	public int getTouchX(int pointer) 
	{
		synchronized(this)
		{
			int index = getIndex(pointer);
			if(index < 0 || index >= MAX_TOUCHPOINTS)
			{
				return 0;
			}
			else
			{
				return touchX[index];
			}
		}
	}

	@Override
	public int getTouchY(int pointer) 
	{
		synchronized(this)
		{
			int index = getIndex(pointer);
			if(index < 0 || index >= MAX_TOUCHPOINTS)
			{
				return 0;
			}
			else
			{
				return touchY[index];
			}
		}
	}

	@Override
	public List<TouchEvent> getTouchEvents() 
	{
		synchronized(this)
		{
			int len = touchEvents.size();
			for(int i = 0; i < len; i++)
			{
				touchEventPool.free(touchEvents.get(i));
			}
			touchEvents.clear();
			touchEvents.addAll(touchEventsBuffer);
			touchEventsBuffer.clear();
			

			return touchEvents;
		}
	}

	private int getIndex(int pointerId)
	{
		for(int i = 0; i < MAX_TOUCHPOINTS; i++)
		{
			if(id[i] == pointerId)
			{
				return i;
			}
		}
		
		return -1;
	}
}
