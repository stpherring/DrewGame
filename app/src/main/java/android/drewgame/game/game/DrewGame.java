package android.drewgame.game.game;

import android.drewgame.game.game.screens.MainScreen;
import android.drewgame.game.graphics.Screen;

/**
 * Created by Stephen on 7/3/2014.
 */
public class DrewGame extends MyGame {

    public static final String TAG = "DrewGame";

    @Override
    public Screen getStartScreen() {
        return new MainScreen(this);
    }

}