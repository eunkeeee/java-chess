package chess.piece;

import static chess.path.Movement.DOWN;
import static chess.path.Movement.DOWN_LEFT;
import static chess.path.Movement.DOWN_RIGHT;
import static chess.path.Movement.LEFT;
import static chess.path.Movement.RIGHT;
import static chess.path.Movement.UP;
import static chess.path.Movement.UP_LEFT;
import static chess.path.Movement.UP_RIGHT;

import chess.path.Movement;
import chess.path.Path;
import chess.position.Position;
import java.util.List;

public class King extends Piece {

    private static final List<Movement> CAN_MOVE_DESTINATION = List.of(
            UP, DOWN, RIGHT, LEFT,
            UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT);

    public King(final Color color) {
        super(color);
    }

    @Override
    public Path searchPathTo(final Position from, final Position to, final Piece destination) {
        if (destination != null) {
            validateSameColor(destination);
        }

        Movement movement = to.convertMovement(from);
        validateMovement(movement, CAN_MOVE_DESTINATION);
        validateAvailableDestination(from, to, movement);

        return new Path();
    }

    private void validateAvailableDestination(final Position from, final Position to, final Movement movement) {
        if (!from.moveBy(movement).equals(to)) {
            throw new IllegalStateException("갈 수 없는 도착지입니다.");
        }
    }
}
