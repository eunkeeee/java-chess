package chess.piece;

import static chess.position.InitialPositionFixtures.WHITE_KNIGHT_LEFT_POSITION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import chess.path.Path;
import chess.position.Position;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class KnightTest {
    @DisplayName("정상 위치로 이동 시 경로를 반환할 수 있다.")
    @Test
    void test_searchPathTo() {

        Piece piece = new Knight(Color.WHITE);

        Path path = piece.searchPathTo(WHITE_KNIGHT_LEFT_POSITION, new Position(3, 3), null);

        assertThat(path)
                .extracting("positions", InstanceOfAssertFactories.list(Position.class))
                .containsExactly();
    }

    @DisplayName("비정상 경로를 받으면 예외 처리한다.")
    @Test
    void test_searchPathTo2() {

        Piece piece = new Knight(Color.WHITE);

        assertThatThrownBy(() ->
                piece.searchPathTo(WHITE_KNIGHT_LEFT_POSITION,
                        new Position(4, 5),
                        null))
                .isInstanceOf(IllegalStateException.class);
    }
}
