package android.drewgame.game.game.enums;

/**
 * Created by Stephen on 7/4/2014.
 */

/**
 * An enum determining the type of a piece.  If the piece is of type TOUCH, the user must
 * touch the lane with the piece before it leaves the screen.  If the piece is of type
 * NO_TOUCH, the user must let the piece leave the screen without touching the lane.
 */
public enum PieceType
{
    TOUCH,
    NO_TOUCH
}