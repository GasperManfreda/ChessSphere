package com.example.chesssphere;

import android.util.Log;

public class ChessGame {
    private Board board;

    public ChessGame() {
        board = new Board();
    }

    public Board getBoard() {
        return board;
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        board.movePiece(fromRow, fromCol, toRow, toCol);
    }
    public boolean isLegalMove(Piece piece, int toRow ,int toCol){

        if(piece != null) {

            int fromRow = piece.getRow();
            int fromCol = piece.getCol();
            if (piece.getType() == Piece.PAWN) {
                return pawnMove(fromRow, fromCol, toRow, toCol, piece);
            } else if (piece.getType() == Piece.ROOK){
                return rookMove(fromRow, fromCol, toRow, toCol, piece);
            } else if (piece.getType() == Piece.KNIGHT) {
                return knightMove(fromRow, fromCol, toRow, toCol, piece);
            } else if (piece.getType() == Piece.BISHOP) {
                return bishopMove(fromRow, fromCol, toRow, toCol, piece);
            } else return false;

        }
        else return false;
    }
    private boolean pawnMove(int fromRow, int fromCol, int toRow, int toCol,  Piece piece){

        int direction;
        if (piece.getColor() == 0)
            direction = 1;
        else
            direction = -1;
        //ne mores digonalno
        if (toCol != fromCol && board.getPiece(toRow,toCol)==null) return false;
        // en kvadratek naprej
        if (toRow == fromRow - direction && board.getPiece(toRow, toCol) == null){
            piece.setPosition(toRow, toCol);
            return true;
        }
        //prvi premik za 2
        int startRow = piece.isWhite() ? 6 : 1;;
        if (fromRow == startRow && toRow == fromRow - 2 * direction && board.getPiece(toRow, toCol) == null && board.getPiece(fromRow - direction, fromCol) == null) {
            piece.setPosition(toRow, toCol);
            return true;
        }
        //pojej diagonalno
        int colDiff = Math.abs(toCol - fromCol);
        if(colDiff == 1){
            Piece target = board.getPiece(toRow,toCol);
            if(target !=null && target.isWhite() != piece.isWhite()){
                piece.setPosition(toRow, toCol);
                return true;
            }
        }
        return false;
    }

    private boolean rookMove(int fromRow, int fromCol, int toRow, int toCol, Piece piece){
        boolean isHorizontal = (toRow == fromRow && toCol != fromCol);
        boolean isVertical = (toCol == fromCol && toRow != fromRow);
        if (!isHorizontal && !isVertical) return false;

        int rowStep = isVertical ? Integer.compare(toRow, fromRow) : 0;
        int colStep = isHorizontal ? Integer.compare(toCol, fromCol) : 0;
        int steps = isHorizontal ? Math.abs(toCol - fromCol) : Math.abs(toRow - fromRow);

        for (int i = 1; i < steps; i++) {
            int checkRow = fromRow + i * rowStep;
            int checkCol = fromCol + i * colStep;
            if (board.getPiece(checkRow, checkCol) != null) {
                return false;
            }
        }

        Piece target = board.getPiece(toRow, toCol);
        if (target == null) {
            piece.setPosition(toRow, toCol);
            return true;
        } else if (target.isWhite() != piece.isWhite()) {
            piece.setPosition(toRow, toCol);
            return true;
        } else {
            return false;
        }


    }

    private boolean knightMove(int fromRow, int fromCol, int toRow, int toCol, Piece piece){
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);

        boolean isLShape = (rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2);
        if (!isLShape) return false;

        Piece target = board.getPiece(toRow, toCol);
        if (target == null) {
            piece.setPosition(toRow, toCol);
            return true;
        } else if (target.isWhite() != piece.isWhite()) {
            piece.setPosition(toRow, toCol);
            return true;
        } else {
            return false;
        }
    }

    private boolean bishopMove(int fromRow, int fromCol, int toRow, int toCol, Piece piece){
        int rowDiff = Math.abs(toRow - fromRow);
        int colDiff = Math.abs(toCol - fromCol);
        if (rowDiff != colDiff || rowDiff == 0 ) return false;

        int rowStep = Integer.compare(toRow, fromRow);
        int colStep = Integer.compare(toCol, fromCol);
        int steps = rowDiff;
        for (int i = 1; i < steps; i++) {
            int checkRow = fromRow + i * rowStep;
            int checkCol = fromCol + i * colStep;
            if (board.getPiece(checkRow, checkCol) != null) {
                return false;
            }
        }
        Piece target = board.getPiece(toRow, toCol);
        if (target == null) {
            piece.setPosition(toRow, toCol);
            return true;
        } else if (target.isWhite() != piece.isWhite()) {
            piece.setPosition(toRow, toCol);
            return true;
        } else {
            return false;
        }
    }


}
