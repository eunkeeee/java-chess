package chess;

import chess.controller.ChessController;
import chess.service.ChessService;
import chess.dao.ChessStatusDao;
import chess.dao.DBChessStatusDao;
import chess.dao.DBPieceDao;

public final class ChessApplication {
    public static void main(String[] args) {
        final DBPieceDao chessGameDao = new DBPieceDao();
        final ChessStatusDao chessStatusDao = new DBChessStatusDao();
        final ChessService chessService = new ChessService(chessGameDao, chessStatusDao);
        final ChessController chessController = new ChessController(chessService);
        chessController.run();
    }
}
