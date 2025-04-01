package com.example.chesssphere;

public class Piece {
    public static final int WHITE = 0;
    public static final int BLACK = 1;

    public static final int PAWN = 0;
    public static final int ROOK = 1;
    public static final int KNIGHT = 2;
    public static final int BISHOP = 3;
    public static final int QUEEN = 4;
    public static final int KING = 5;

    private int color;
    private int type;
    private int row, col;

    public Piece(int color, int type, int row, int col) {
        this.color = color;
        this.type = type;
        this.row = row;
        this.col = col;
    }

    public int getColor() { return color; }
    public int getType() { return type; }
    public int getRow() { return row; }
    public int getCol() { return col; }

    public boolean isWhite(){
        if (this.getColor() == 0){
            return true;
        }
        else return false;
    }

    public void setPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }
}
