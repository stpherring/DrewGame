package android.drewgame.game.game.screens;

import android.drewgame.game.game.Game;
import android.drewgame.game.game.MyGame;
import android.drewgame.game.game.enums.GameState;
import android.drewgame.game.game.enums.PieceDirection;
import android.drewgame.game.game.enums.PieceType;
import android.drewgame.game.game.objects.ArenaGrid;
import android.drewgame.game.game.objects.Lane;
import android.drewgame.game.game.objects.Piece;
import android.drewgame.game.game.values.DifficultyValues;
import android.drewgame.game.graphics.GLGraphics;
import android.drewgame.game.graphics.Screen;
import android.drewgame.game.input.TouchEvent;
import android.drewgame.game.utils.FPSCounter;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Stephen on 7/13/2014.
 */
public class GameScreen extends Screen
{
    private static final String TAG = "GameScreen";
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static int SCORE;

    private float round_acceleration;

    FPSCounter counter;
    List<Piece> pieceList;
    ArenaGrid grid;
    GLGraphics glGraphics;

    public GameScreen(Game game)
    {
        super(game);
        glGraphics = ((MyGame)game).getGLGraphics();

        SCREEN_WIDTH = glGraphics.getWidth();
        SCREEN_HEIGHT = glGraphics.getHeight();

        float ratio = (1.0f * SCREEN_WIDTH/SCREEN_HEIGHT);

        grid = new ArenaGrid(DifficultyValues.NUM_ROWS, -ratio, ratio, 1.0f, -1.0f);
        counter = new FPSCounter();

        SCORE = 0;

        round_acceleration = DifficultyValues.ACCELERATION;

    }

    @Override
    public void update(float deltaTime)
    {
        updateUser(deltaTime);
        updateLanes(deltaTime);
        updateSpawners(deltaTime);
    }

    @Override
    public void present(float[] mMVPMatrix)
    {
        counter.logFrame();
        drawUserInteraction();
        drawPieces(mMVPMatrix);
        drawSpawners();
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {
            /*
            gl = glGraphics.getGL10();

            gl.glViewport(0,0, SCREEN_WIDTH, SCREEN_HEIGHT);
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            gl.glOrthof(0, glGraphics.getWidth(), 0, glGraphics.getHeight(), 1, -1);
        */
    }

    @Override
    public void dispose()
    {

    }

    /**
     * Look through user touch events for any signs that action should be taken
     * (game over, clear objects, etc.)
     *
     * @param deltaTime The time that has passed since the last frame
     */
    private void updateUser(float deltaTime)
    {
        List<TouchEvent> touchEvents = game.getInput().getTouchEvents();
        for(int i = 0; i < touchEvents.size(); i++)
        {
            TouchEvent event = touchEvents.get(i);
            GameState state = grid.processTouch(event);
            if(state == GameState.SCORE)
            {
                increaseScore(1);
            }
            else if(state == GameState.GAME_OVER)
            {
                gameOver();
            }
        }
    }

    /**
     * Make stuff move down the lanes
     *
     * @param deltaTime The time that has passed since the last frame
     */
    private void updateLanes(float deltaTime)
    {
        int result = grid.updateLanes(deltaTime);
        if(result < 0)
        {
            gameOver();
        }
        else
        {
            increaseScore(result);
        }
    }

    /**
     * Spawn stuff in the lanes
     *
     * @param deltaTime The time that has passed since the last frame
     */
    private void updateSpawners(float deltaTime)
    {
        Log.d(TAG, "deltaTime: " + deltaTime);
            /*
                If lane is empty, try and spawn new piece
             */
        List<Lane> emptyLanes = grid.getRefreshableLanes();
        for (Lane lane : emptyLanes) {

            float x = 0; //lane.position.x + lane.bounds.width;
            float y = lane.position.y + lane.bounds.height / 2;
            float velocity = DifficultyValues.VELOCITY * round_acceleration;
            round_acceleration *= DifficultyValues.ACCELERATION;
            if (lane.getDirection() == PieceDirection.LEFT) {
                velocity *= -1;
            }

            PieceType type = PieceType.TOUCH;
            if (Math.random() <= DifficultyValues.PROBABILITY_NO_TOUCH) {
                type = PieceType.NO_TOUCH;
            }

            Piece spawn = new Piece(x, y, velocity, type);
            lane.setPiece(spawn);

        }
    }

    /**
     * If a user has interacted with any lane, show it
     */
    private void drawUserInteraction()
    {
        // Can be empty... for now
    }

    /**
     * Draw the pieces in each lane
     */
    private void drawPieces(float[] mMVPMatrix)
    {
        pieceList = grid.getPieces();
        //Log.d(TAG, "Num pieces: " + pieceList.size());
        for (Piece piece : pieceList) {
            piece.draw(mMVPMatrix);
        }

    }

    /**
     * If a piece has spawned, we want to animate it
     */
    private void drawSpawners()
    {
        // Can be empty... for now
    }

    public void increaseScore(int scoreIncrement)
    {
        SCORE += scoreIncrement;
        Log.d(TAG, "Score: " + SCORE);
    }

    public void gameOver()
    {
        Log.d(TAG, "Score: " + SCORE);
        Log.d(TAG, "Game Over!");
        game.setScreen(new MainScreen((MyGame) game));
    }
}
