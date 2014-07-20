package android.drewgame.game.game.objects;

import android.drewgame.game.graphics.GLGraphics;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Stephen on 7/15/2014.
 */
public class MenuItem extends GameObject
{
    private static final String TAG = "Piece";

    private int mProgram;

    private FloatBuffer vertexBuffer;

    static final int COORDS_PER_VERTEX = 3;
    // 4 bytes per float
    static final int vertexStride = 4 * COORDS_PER_VERTEX;

    static float triangleCoords[] = {   // in counterclockwise order:
            -0.3f, 0.3f, 0.0f, // top left
            -0.3f, -0.3f, 0.0f, // bottom left
            0.3f, -0.3f, 0.0f,  // bottom right
            0.3f, 0.3f, 0.0f // top right

    };

    static final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    float[] color = new float[]{1.0f, 1.0f, 0.0f, 1.0f};


    public MenuItem(float x, float y)
    {
        super(x, y, .5f, .5f);
        initializeGL();
    }

    public void draw(float[] mMVPMatrixRef)
    {
        float[] mMVPMatrix = mMVPMatrixRef.clone();
        GLES20.glUseProgram(mProgram);

        int mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX, GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        int mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        int mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        //Log.d(TAG, "X: " + this.position.x + ", Y: " + this.position.y);
        Matrix.translateM(mMVPMatrix, 0, this.position.x, this.position.y, 0);
        GLES20.glUniformMatrix4fv(mMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
        GLES20.glDisableVertexAttribArray(mPositionHandle);

    }

    /**
     * Initialize the OpenGL model for the menu item
     */
    private void initializeGL()
    {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        final String vertexShaderCode =
                // This matrix member variable provides a hook to manipulate
                // the coordinates of the objects that use this vertex shader
                "uniform mat4 uMVPMatrix;" +
                        "attribute vec4 vPosition;" +
                        "void main() {" +
                        // the matrix must be included as a modifier of gl_Position
                        // Note that the uMVPMatrix factor *must be first* in order
                        // for the matrix multiplication product to be correct.
                        "  gl_Position = uMVPMatrix * vPosition;" +
                        "}";

        int vertexShader = GLGraphics.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        String fragmentShaderCode = "precision mediump float;" +
                "uniform vec4 vColor;" +
                "void main() {" +
                "  gl_FragColor = vColor;" +
                "}";
        int fragmentShader = GLGraphics.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }
}
