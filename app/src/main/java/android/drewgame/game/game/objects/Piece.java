package android.drewgame.game.game.objects;

import android.drewgame.game.game.DrewGame;
import android.drewgame.game.game.MyGame;
import android.drewgame.game.game.enums.GameState;
import android.drewgame.game.game.enums.PieceType;
import android.drewgame.game.graphics.GLGraphics;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Stephen on 7/4/2014.
 * An object that represents a piece that the user interacts with in the game
 */
public class Piece extends GameObject
{
    private static final String TAG = "Piece";

    private int mProgram;

    private FloatBuffer vertexBuffer;

    static final int COORDS_PER_VERTEX = 3;
    // 4 bytes per float
    static final int vertexStride = 4 * COORDS_PER_VERTEX;

    static float triangleCoords[] = {   // in counterclockwise order:
            -0.03f, 0.03f, 0.0f, // top left
            -0.03f, -0.03f, 0.0f, // bottom left
            0.03f, -0.03f, 0.0f,  // bottom right
            0.03f, 0.03f, 0.0f // top right

    };

    static final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;

    float color[];

    float velocity;
    PieceType type;

    /**
     *
     * @param x horizontal location
     * @param y vertical location
     * @param velocity The velocity of the piece.  Since pieces only move horizontally, a single float is passed in
     * @param type The type of the piece.  If it is of type TOUCH, it should be touched before reaching the end of its
     *             lane.  If it is of type NO_TOUCH, it must reach the end of its lane without being touched.
     */
    public Piece(float x, float y, float velocity, PieceType type)
    {
        super(x, y, 0.5f, 0.5f);
        this.velocity = velocity;
        this.type = type;

        if(type == PieceType.NO_TOUCH)
        {
            color = new float[]{1.0f, 0.5f, 0.0f, 1.0f};
        }
        else
        {
            color = new float[]{0.0f, 0.0f, 1.0f, 1.0f};
        }

        initializeGL();
    }

    /**
     * Update the position of the piece since the previous time it was drawn
     * @param deltaTime The time between frames being drawn
     */
    public GameState updatePosition(float deltaTime)
    {
        this.position.x += velocity * deltaTime;

        // If out of bounds
        if(this.position.x < -ArenaGrid.LANE_WIDTH || this.position.x > ArenaGrid.LANE_WIDTH)
        {
            if(type == PieceType.NO_TOUCH)
            {
                return GameState.SCORE;
            }
            else
            {
                return GameState.GAME_OVER;
            }
        }
        return GameState.OK;
    }

    public float getX()
    {
        return this.position.x;
    }

    public float getY()
    {
        return this.position.y;
    }

    public PieceType getType()
    {
        return type;
    }

    /**
     * Initialize the OpenGL model for the piece
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

    public void draw(float[] mMVPMatrixRef)
    {
        // Manipulating the passed in array causes other objects to have incorrect matrix
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
}
