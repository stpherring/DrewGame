package android.drewgame.game.graphics;

import android.content.Context;
import android.drewgame.game.game.MyGame;
import android.opengl.GLSurfaceView;

/**
 * Created by Stephen on 7/5/2014.
 */
public class MyGLSurfaceView extends GLSurfaceView
{
    final Renderer renderer;

    public MyGLSurfaceView(MyGame game)
    {
        super(game);
        setEGLContextClientVersion(2);
        renderer = game;
        setRenderer(renderer);
    }
}
