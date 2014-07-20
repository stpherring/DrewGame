package android.drewgame.game.graphics;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;

public class GLGraphics 
{
	GLSurfaceView glView;
	GLES20 gl;
	
	public GLGraphics(GLSurfaceView glView)
	{
		this.glView = glView;
	}
	
	public GLES20 getGLES20()
	{
		return gl;
	}
	
	public void setGL(GLES20 gl)
	{
		this.gl = gl;
	}
	
	public int getWidth()
	{
		return glView.getWidth();
	}
	
	public int getHeight()
	{
		return glView.getHeight();
	}

    public static int loadShader(int type, String shaderCode)
    {
        int shader = GLES20.glCreateShader(type);

        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }
	
}
