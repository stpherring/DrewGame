package android.drewgame.game.game.screens;

import android.drewgame.game.game.MyGame;
import android.drewgame.game.game.objects.MenuItem;
import android.drewgame.game.graphics.Screen;
import android.drewgame.game.input.TouchEvent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.opengl.GLES20;

import java.util.List;

import Texample2.src.com.android.texample2.GLText;

/**
 * Created by Stephen on 7/13/2014.
 */
public class MainScreen extends Screen
{
    private GLText glText;
    private MyGame game;
    private MenuItem start;

    public MainScreen(MyGame game)
    {
        super(game);
        this.game = game;
        start = new MenuItem(0, 0);
    }

    @Override
    public void update(float deltaTime)
    {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        if(touchEvents.size() > 0)
        {
            game.setScreen(new GameScreen(game));
        }
    }

    @Override
    public void present(float[] mMVPMatrixRef)
    {
        drawMenuItems(mMVPMatrixRef);
        /*
        Canvas canvas = createCanvas();

        drawMenu(mMVPMatrixRef, canvas);
        */
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }

    public void drawMenuItems(float[] mMVPMatrixRef)
    {
        float[] mMVPMatrix = mMVPMatrixRef.clone();
        start.draw(mMVPMatrix);
    }
/*
    public Canvas createCanvas()
    {
        Bitmap bitmap  = Bitmap.createBitmap(256, 256, Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);

        Drawable background = game.getResources().getDrawable(android.R.color.white);
        background.setBounds(0, 0, 256, 256);
        background.draw(canvas);

        Paint textPaint = new Paint();
        textPaint.setTextSize(32);
        textPaint.setAntiAlias(true);
        textPaint.setARGB(0xff, 0x00, 0x00, 0x00);
        canvas.drawText("Hello", 16, 112, textPaint);

        return canvas;
    }

    public void drawMenu(float[] mMVPMatrixRef, Canvas canvas)
    {
        /*
        glText = new GLText(game.getAssets());

        glText.load("fonts/Roboto-Light.ttf", 14, 2, 2);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        glText.drawTexture(50, 50, mMVPMatrixRef);

        glText.begin(1.0f, 1.0f, 1.0f, 1.0f, mMVPMatrixRef);
        glText.draw("Hello!", 50, 200);
        glText.end();
        /*
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
    */
}
