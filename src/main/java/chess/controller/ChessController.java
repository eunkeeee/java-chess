package chess.controller;

import static chess.controller.ChessGameCommand.COMMAND_INDEX;
import static chess.controller.ChessGameCommand.DEFAULT_COMMAND_SIZE;
import static chess.controller.ChessGameCommand.EMPTY;
import static chess.controller.ChessGameCommand.END;
import static chess.controller.ChessGameCommand.FROM_INDEX;
import static chess.controller.ChessGameCommand.MOVE;
import static chess.controller.ChessGameCommand.MOVE_COMMAND_SIZE;
import static chess.controller.ChessGameCommand.START;
import static chess.controller.ChessGameCommand.STATUS;
import static chess.controller.ChessGameCommand.TO_INDEX;

import chess.ChessGame;
import chess.domain.board.BoardFactory;
import chess.domain.position.Position;
import chess.exception.ChessDBException;
import chess.service.ChessService;
import chess.view.InputView;
import chess.view.OutputView;
import java.util.List;
import java.util.Map;

public final class ChessController {
    private final Map<ChessGameCommand, ChessGameAction> commandMapper;
    private final ChessService chessService;

    public ChessController(ChessService chessService) {
        this.commandMapper = Map.of(
                START, this::start,
                MOVE, this::movePiece,
                STATUS, this::showStatus,
                END, this::end
        );
        this.chessService = chessService;
    }

    public void run() {
        String gameId = readGameIdCommand();
        if (gameId == null) {
            gameId = createGame();
        }

        ChessGame chessGame = chessService.readChessGame(gameId);
        if (chessGame == null) {
            createGame();
        }

        OutputView.printStartMessage();
        ChessGameCommand command = EMPTY;

        while (command.isPlayable()) {
            command = play();
            if (chessGame.isEnd()) {
                command = END;
                OutputView.printWinner(chessGame.winner());
            }
        }
    }

    private String readGameIdCommand() {
        String gameIdCommand;

        while (true) {
            final List<String> gameIds = chessService.readGameIds();
            gameIdCommand = InputView.readGameId(gameIds);

            if (gameIdCommand.equalsIgnoreCase("new")) {
                gameIdCommand = null;
                break;
            }
            if (gameIds.contains(gameIdCommand)) {
                break;
            }
        }
        return gameIdCommand;
    }

    private String createGame() {
        ChessGame chessGame = new ChessGame(new BoardFactory().createInitialBoard());
        final String gameId = chessService.createChessStatus(chessGame);
        chessService.createChessGame(chessGame);
        return gameId;
    }

    private ChessGameCommand play() {
        try {
            List<String> commands = InputView.readCommand();
            ChessGameCommand command = ChessGameCommand.from(commands.get(COMMAND_INDEX));
            commandMapper.get(command).execute(commands);
            return command;
        } catch (IllegalArgumentException | IllegalStateException exception) {
            OutputView.printExceptionMessage(exception.getMessage());
            return EMPTY;
        } catch (ChessDBException exception) {
            OutputView.printExceptionMessage(exception.getMessage());
            return END;
        } catch (Exception exception) {
            OutputView.printExceptionMessage("예기치 못한 예외가 발생했습니다.");
            return EMPTY;
        }
    }

    private void start(final List<String> commands) {
        ChessGame chessGame = chessService.readChessGame();
        validateCommandsSize(commands, DEFAULT_COMMAND_SIZE);
        OutputView.printBoard(chessGame.board());
    }

    private void movePiece(final List<String> commands) {
        ChessGame chessGame = chessService.readChessGame();
        validateCommandsSize(commands, MOVE_COMMAND_SIZE);
        Position from = searchPosition(commands.get(FROM_INDEX));
        Position to = searchPosition(commands.get(TO_INDEX));

        chessGame.move(from, to);
        OutputView.printBoard(chessGame.board());

        chessService.update(chessGame, from, to);
    }

    private void showStatus(final List<String> commands) {
        ChessGame chessGame = chessService.readChessGame();
        validateCommandsSize(commands, DEFAULT_COMMAND_SIZE);

        OutputView.printScore(chessGame.calculateScore());
    }

    private void end(final List<String> commands) {
        ChessGame chessGame = chessService.readChessGame();
        validateCommandsSize(commands, DEFAULT_COMMAND_SIZE);

        if (!chessGame.isEnd()) {
            OutputView.printScore(chessGame.calculateScore());
            return;
        }

        OutputView.printWinner(chessGame.winner());
        chessService.deleteAll();
    }

    private static void validateCommandsSize(final List<String> commands, final int moveCommandSize) {
        if (commands.size() != moveCommandSize) {
            throw new IllegalArgumentException("명령을 형식에 맞게 입력해 주세요!");
        }
    }

    private Position searchPosition(final String command) {
        final List<String> positionCommands = List.of(command.split(""));
        validatePositionCommandsSize(positionCommands);
        return Position.of(positionCommands.get(0), positionCommands.get(1));
    }

    private static void validatePositionCommandsSize(final List<String> commands) {
        if (commands.size() != 2) {
            throw new IllegalArgumentException("a1 ~ h8까지 좌표를 입력해 주세요");
        }
    }
}
