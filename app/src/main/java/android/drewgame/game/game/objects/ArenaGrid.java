package android.drewgame.game.game.objects;

import android.drewgame.game.game.enums.GameState;
import android.drewgame.game.game.enums.PieceDirection;
import android.drewgame.game.game.screens.GameScreen;
import android.drewgame.game.input.TouchEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen on 7/4/2014.
 */
public class ArenaGrid
{
    private static final String TAG = "ArenaGrid";

    public static float LANE_HEIGHT;
    public static float LANE_WIDTH;
    Lane[] lanes;

    /**
     * Creates a grid of lanes.  Represents the arena where the user plays.
     * @param rows The number of rows in the grid.  More leads to higher difficulty.
     * @param left The left bound of the screen.
     * @param right The right bound of the screen.
     * @param top The top bound of the screen.
     * @param bottom The bottom bound of the screen.
     */
    public ArenaGrid(int rows, float left, float right, float top, float bottom)
    {
        // We have 2 columns for each row: even numbered elements in array are left, odd are right
        lanes = new Lane[rows*2];

        LANE_HEIGHT = (top-bottom)/rows;
        LANE_WIDTH = (right-left)/2;


        for(int i = 0; i < lanes.length; i++)
        {
            PieceDirection direction = PieceDirection.LEFT;
            if(i%2 > 0)
            {
                direction = PieceDirection.RIGHT;
            }

            float x = (i%2) * LANE_WIDTH + left;
            float y = (i/2) * LANE_HEIGHT + bottom;
            lanes[i] = new Lane(x, y, LANE_WIDTH, LANE_HEIGHT, direction);
        }
    }

    public List<Piece> getPieces()
    {
        List<Piece> pieces = new ArrayList<Piece>();
        for(Lane lane: lanes)
        {
            if(lane.getPiece() != null)
            {
                pieces.add(lane.getPiece());
            }
        }
        return pieces;
    }

    public List<Lane> getRefreshableLanes()
    {
        List<Lane> emptyLanes = new ArrayList<Lane>();
        for(int i = 0; i < lanes.length; i++)
        {
            if(lanes[i].canRespawn())
            {
                emptyLanes.add(lanes[i]);
            }
        }
        return emptyLanes;
    }


    /**
     * Given a touch event, process it to determine if any action should be taken
     * @param event The touch event to process
     * @return The state of the game after the touch (score, game over, or nothing)
     */
    public GameState processTouch(TouchEvent event)
    {
        for(int i = 0; i < lanes.length; i++)
        {
            float[] coords = pixelToCoords(event.x, event.y);
            float x = coords[0];
            float y = coords[1];

            //Log.d(TAG, "X: " + x + " Y: " + y);
            if(lanes[i].inBounds(x, y))
            {
                //Log.d(TAG, "In bounds for lane " + i);
                return lanes[i].processTouch();
            }
        }

        return GameState.OK;
    }

    /**
     * Helper function to convert touch events (registered in pixels) to coordinates within the world
     * @param x X position of touched pixel
     * @param y Y position of touched pixel
     * @return An array containing converted coordinates [x, y]
     */
    private float[] pixelToCoords(int x, int y)
    {
        float ret_y = -((y - (GameScreen.SCREEN_HEIGHT / 2.0f)) / (GameScreen.SCREEN_HEIGHT / 2.0f));
        float ret_x = -((x - (GameScreen.SCREEN_WIDTH / 2.0f)) / (GameScreen.SCREEN_WIDTH / 2.0f));
        float ratio = (float) GameScreen.SCREEN_WIDTH / GameScreen.SCREEN_HEIGHT;
        ret_x *= ratio;

        float[] ret = new float[2];
        ret[0] = ret_x;
        ret[1] = ret_y;

        return ret;
    }


    /**
     * Update all lanes, returning amount of points scored in frame, or signal a game over
     * @param deltaTime Time elapsed between current frame and last frame
     * @return -1 for game over, otherwise returns score for frame
     */
    public int updateLanes(float deltaTime)
    {
        int score = 0;
        for(Lane lane : lanes)
        {
            Piece piece = lane.getPiece();
            if(piece != null)
            {
                GameState state = piece.updatePosition(deltaTime);
                if(state == GameState.GAME_OVER)
                {
                    return -1;
                }
                else if(state == GameState.SCORE)
                {
                    score++;
                    lane.setPiece(null);
                }
            }
            else
            {
                lane.incrementEmptyTime(deltaTime);
            }
        }
        return score;
    }
}
