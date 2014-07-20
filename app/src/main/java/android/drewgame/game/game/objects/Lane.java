package android.drewgame.game.game.objects;

import android.drewgame.game.game.enums.GameState;
import android.drewgame.game.game.enums.PieceDirection;
import android.drewgame.game.game.enums.PieceType;
import android.drewgame.game.game.values.DifficultyValues;
import android.util.Log;

/**
 * Created by Stephen on 7/4/2014.
 */
public class Lane extends GameObject
{
    private static final String TAG = "Lane";
    // The piece running down the lane
    private Piece piece;
    private PieceDirection direction;
    private float emptyTime;
    private float respawnTime;

    public Lane(float x, float y, float width, float height, PieceDirection direction)
    {
        super(x, y, width, height);
        piece = null;
        this.direction = direction;
        emptyTime = 0;
        respawnTime = 0;

        //Log.d(TAG, "llX: " + x + " llY: " + y + " width: " + width + " height: " + height);
    }

    public void setPiece(Piece runner)
    {
        this.piece = runner;
        emptyTime = 0;
        respawnTime = DifficultyValues.BASE_RESPAWN_TIME + (float)Math.random() * DifficultyValues.EXTRA_RESPAWN_TIME;
    }

    public Piece getPiece()
    {
        return piece;
    }

    public PieceDirection getDirection()
    {
        return direction;
    }

    /**
     * Given a coordinate, determine whether or not it is in the bounds of the lane
     * @param x  The horizontal component of the coordinate
     * @param y  The vertical component of the coordinate
     * @return True if (x,y) is in the bounding rectangle, false otherwise
     */
    public boolean inBounds(float x, float y)
    {
        float llX = this.bounds.lowerLeft.x;
        float llY = this.bounds.lowerLeft.y;
        float laneWidth = this.bounds.width;
        float laneHeight = this.bounds.height;

        return x >= llX && x <= llX + laneWidth && y >= llY && y <= llY + laneHeight;
    }


    /**
     * Process a user touching the lane
     * @return OK if nothing happened, SCORE if user scored a point, GAME_OVER if user lost
     */
    public GameState processTouch()
    {
        // Potentially mark the lane as touched here for animations
        if(piece != null)
        {
            PieceType type = piece.getType();
            if(type == PieceType.TOUCH)
            {
                piece = null;
                return GameState.SCORE;
            }
            else
            {
                return GameState.GAME_OVER;
            }
        }
        return GameState.OK;
    }

    public boolean canRespawn()
    {
        return emptyTime > respawnTime;
    }

    public void incrementEmptyTime(float deltaTime)
    {
        if(piece == null) {
            emptyTime += deltaTime;
        }
    }
}
