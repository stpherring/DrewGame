package android.drewgame.game.game.values;

/**
 * Created by Stephen on 7/6/2014.
 */
public class DifficultyValues
{
    public static int NUM_ROWS = 4;
    public static float VELOCITY = 0.3f;
    public static float ACCELERATION = 1.01f;
    public static float PROBABILITY_NO_TOUCH = 0.2f;
    public static float BASE_RESPAWN_TIME = 0.5f;

    // This acts as time added on top of BASE_RESPAWN_TIME, so the maximum time a piece
    // could go before respawning is BASE_RESPAWN_TIME + EXTRA_RESPAWN_TIME
    public static float EXTRA_RESPAWN_TIME = 1.0f;
}
