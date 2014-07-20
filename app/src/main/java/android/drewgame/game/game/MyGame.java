package android.drewgame.game.game;

import android.app.Activity;
import android.content.Context;
import android.drewgame.game.audio.AndroidAudio;
import android.drewgame.game.audio.Audio;
import android.drewgame.game.fileio.AndroidFileIO;
import android.drewgame.game.fileio.FileIO;
import android.drewgame.game.graphics.GLGraphics;
import android.drewgame.game.graphics.MyGLSurfaceView;
import android.drewgame.game.graphics.Screen;
import android.drewgame.game.input.AndroidInput;
import android.drewgame.game.input.Input;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.Matrix;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public abstract class MyGame extends Activity implements Game, Renderer
{
	private static final String TAG = "MyGame";
	enum GLGameState
	{
		Initialized,
		Running,
		Paused,
		Finished,
		Idle
	}
	
	GLSurfaceView glView;
	GLGraphics glGraphics;
	Audio audio;
	FileIO fileIO;
	Input input;
	Screen screen;
	GLGameState state = GLGameState.Initialized;
	Object stateChanged = new Object();
	long startTime = System.nanoTime();
	WakeLock wakeLock;
    float[] mProjectionMatrix;
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
							 WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		glView = new MyGLSurfaceView(this);
		setContentView(glView);
		
		glGraphics = new GLGraphics(glView);
		fileIO = new AndroidFileIO(this);
		audio = new AndroidAudio(this);
		input = new AndroidInput(this, glView, 1, 1);
		
		PowerManager powerManager = (PowerManager)getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "GLGame");
		
	}
	
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
        GLES20.glClearColor(0, 0, 0, 1);
		
		synchronized(stateChanged)
		{
			if(state == GLGameState.Initialized)
			{
				screen = getStartScreen();
			}
			
			state = GLGameState.Running;
			screen.resume();
			startTime = System.nanoTime();
		}
	}
	
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
        GLES20.glViewport(0, 0, width, height);
        Log.d(TAG, "width: " + width + " height: " + height);
        float ratio = 1.0f * width / height;
        Log.d(TAG, "ratio: " + ratio);
        mProjectionMatrix = new float[16];

        /*
         * This projection matrix is applied to object coordinates
         * in the onDrawFrame() method
         */
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
    }
	
	public void onDrawFrame(GL10 gl)
	{
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        float[] mMVPMatrix = new float[16];
        float[] mViewMatrix = new float[16];
        Matrix.setLookAtM(mViewMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        GLGameState state;
		
		synchronized(stateChanged)
		{
			state = this.state;
		}
		
		if(state == GLGameState.Running)
		{
			float deltaTime = (System.nanoTime() - startTime) / 1000000000.0f;
			
			startTime = System.nanoTime();
			
			screen.update(deltaTime);
			screen.present(mMVPMatrix);
		}
		
		if(state == GLGameState.Paused)
		{
			screen.pause();
			
			synchronized(stateChanged)
			{
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
		
		if(state == GLGameState.Finished)
		{
			screen.pause();
			screen.dispose();
			
			synchronized(stateChanged)
			{
				this.state = GLGameState.Idle;
				stateChanged.notifyAll();
			}
		}
	}
	
	public void onResume()
	{
		super.onResume();
		
		glView.onResume();
		wakeLock.acquire();
	}
	
	public void onPause()
	{
		synchronized(stateChanged)
		{
			if(isFinishing())
			{
				state = GLGameState.Finished;
			}
			else
			{
				state = GLGameState.Paused;
			}
			while(true)
			{
				try
				{
					stateChanged.wait(); break;
				}
				catch(InterruptedException e)
				{
					Log.e(TAG, e.getLocalizedMessage());
				}
			}
		}
		wakeLock.release();
		glView.onPause();
		super.onPause();
	}
	
	public GLGraphics getGLGraphics()
	{
		return glGraphics;
	}
	
	public Input getInput()
	{
		return input;
	}
	
	public FileIO getFileIO()
	{
		return fileIO;
	}
	
	public Audio getAudio()
	{
		return audio;
	}
	
	public void setScreen(Screen newScreen)
	{
		if(screen == null)
		{
			throw new IllegalArgumentException("Screen cannot be null");
		}
		
		this.screen.pause();
		this.screen.dispose();
		newScreen.resume();
		newScreen.update(0);
		this.screen = newScreen;
	}
	
	public Screen getCurrentScreen()
	{
		return screen;
	}

}
