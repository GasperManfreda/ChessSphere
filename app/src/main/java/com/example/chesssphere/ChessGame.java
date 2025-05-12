package com.example.chesssphere;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.PieceType;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.Move;
import com.github.bhlangonijr.chesslib.move.MoveGenerator;
import com.github.bhlangonijr.chesslib.move.MoveGeneratorException;
import com.github.bhlangonijr.chesslib.move.MoveList;





public class ChessGame {
    private Board chesslibBoard;







    public int getCurrentPlayer() {
        Side sideToMove = chesslibBoard.getSideToMove();

        return (sideToMove == Side.WHITE) ? 0 : 1;
    }

    public ChessGame() {
        this.chesslibBoard = new Board();

    }
    public static Square coordinateToSquare(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) return Square.NONE;
        try {
            com.github.bhlangonijr.chesslib.File file = com.github.bhlangonijr.chesslib.File.values()[col];
            com.github.bhlangonijr.chesslib.Rank rank = com.github.bhlangonijr.chesslib.Rank.values()[7 - row]; // Invert row
            return Square.encode(rank, file);
        } catch (Exception e) { return Square.NONE; }
    }


    public static Position squareToPosition(Square square) {
        if (square == null || square == Square.NONE) return null;
        int col = square.getFile().ordinal();
        int row = 7 - square.getRank().ordinal(); // Invert rank
        return new Position(row, col);
    }




    private boolean isValidSquare(int row, int col) {
        return row >= 0 && row < 8 && col >= 0 && col < 8;
    }

    public List<Position> getLegalMoves(int startRow, int startCol) {
        List<Position> legalUiMoves = new ArrayList<>();
        Square startSquare = coordinateToSquare(startRow, startCol);
        if (startSquare == Square.NONE) return legalUiMoves;

        com.github.bhlangonijr.chesslib.Piece piece = chesslibBoard.getPiece(startSquare);
        Side currentSide = chesslibBoard.getSideToMove();

        if (piece == com.github.bhlangonijr.chesslib.Piece.NONE || piece.getPieceSide() != currentSide) {
            return legalUiMoves;
        }

        try {
            List<Move> allLegalMoves = MoveGenerator.generateLegalMoves(chesslibBoard);
            for (Move move : allLegalMoves) {
                if (move.getFrom() == startSquare) {
                    Position targetPosition = squareToPosition(move.getTo());
                    if (targetPosition != null) {
                        legalUiMoves.add(targetPosition);
                    }
                }
            }
        } catch (MoveGeneratorException e) {
            Log.e("ChessGame", "Error generating moves", e);
        }
        return legalUiMoves;
    }

    public boolean movePiece(int fromRow, int fromCol, int toRow, int toCol) {

        Square fromSquare = coordinateToSquare(fromRow, fromCol);
        Square toSquare = coordinateToSquare(toRow, toCol);
        if (fromSquare == Square.NONE || toSquare == Square.NONE) return false;

        // Default promotion to Queen (needs UI for real choice)
        com.github.bhlangonijr.chesslib.Piece promotionPiece = com.github.bhlangonijr.chesslib.Piece.NONE;
        com.github.bhlangonijr.chesslib.Piece movingPiece = chesslibBoard.getPiece(fromSquare);
        if (movingPiece != com.github.bhlangonijr.chesslib.Piece.NONE && movingPiece.getPieceType() == PieceType.PAWN) {

            Side side = movingPiece.getPieceSide();
            int targetRankIndex = toSquare.getRank().ordinal();
            if ((side == Side.WHITE && targetRankIndex == 7) || (side == Side.BLACK && targetRankIndex == 0)) {
                promotionPiece = (side == Side.WHITE) ? com.github.bhlangonijr.chesslib.Piece.WHITE_QUEEN : com.github.bhlangonijr.chesslib.Piece.BLACK_QUEEN;
            }
        }

        Move move = new Move(fromSquare, toSquare, promotionPiece);
        boolean success = chesslibBoard.doMove(move);

        // Log results
        if (success) {
            Log.d("ChessGame", "Move executed: " + move);
            if (chesslibBoard.isMated()) Log.i("ChessGame", "CHECKMATE!");
            else if (chesslibBoard.isStaleMate())Log.i("ChessGame", "STALEMATE!");
            else if (chesslibBoard.isKingAttacked()) Log.i("ChessGame", "Check!");
        } else {
            Log.w("ChessGame", "Illegal move attempted: " + move);
        }
        return success;
    }

    public com.github.bhlangonijr.chesslib.Piece getChesslibPieceAt(int row, int col) {
        Square square = coordinateToSquare(row, col);
        return (square == Square.NONE) ? com.github.bhlangonijr.chesslib.Piece.NONE : chesslibBoard.getPiece(square);
    }



}
