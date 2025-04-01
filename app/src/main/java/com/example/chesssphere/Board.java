package com.example.chesssphere;

import android.util.Log;

public class Board {
    private Piece[][] board;

    public Board() {
        board = new Piece[8][8];
        setupBoard();
    }

    private void setupBoard() {
        // Setup pawns
        for (int i = 0; i < 8; i++) {
            board[1][i] = new Piece(Piece.BLACK, Piece.PAWN, 1, i);
            board[6][i] = new Piece(Piece.WHITE, Piece.PAWN, 6, i);
        }
        // Setup other pieces (Rook, Knight, Bishop, Queen, King)
        int[] order = {Piece.ROOK, Piece.KNIGHT, Piece.BISHOP, Piece.QUEEN, Piece.KING, Piece.BISHOP, Piece.KNIGHT, Piece.ROOK};
        for (int i = 0; i < 8; i++) {
            board[0][i] = new Piece(Piece.BLACK, order[i], 0, i);
            board[7][i] = new Piece(Piece.WHITE, order[i], 7, i);
        }
    }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }

    public void movePiece(int fromRow, int fromCol, int toRow, int toCol) {
        if (board[fromRow][fromCol] != null) {
            board[toRow][toCol] = board[fromRow][fromCol];
        }

        if(!(fromRow == toRow && fromCol == toCol)){
            board[fromRow][fromCol] = null;

        }

    }
}

