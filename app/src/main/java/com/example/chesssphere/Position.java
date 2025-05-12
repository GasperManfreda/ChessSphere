package com.example.chesssphere;

public class Position {



/**
 * A simple class to represent a position (row and column) on the chessboard.
 * Instances of this class are immutable.
 */


    // Final fields ensure the position cannot be changed after creation
    public final int row;
    public final int col;

    /**
     * Constructs a new Position object.
     *
     * @param row The row index (typically 0-7).
     * @param col The column index (typically 0-7).
     */
    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }


    public int getCol() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        // Standard equals implementation:
        // 1. Check for self comparison
        if (this == o) return true;
        // 2. Check for null and different class
        if (o == null || getClass() != o.getClass()) return false;
        // 3. Cast and compare fields
        Position position = (Position) o;
        return row == position.row && col == position.col;
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}

