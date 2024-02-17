/*
 * Cell.java
 *
 * Created on October 30, 2004, 5:16 PM
 */

package com.ngss.jcalendar;

/**
 * @author ngeor
 */
public class Cell {
    private static final int DAYS_PER_WEEK = 7;
    private int row;
    private int col;

    /**
     * Creates a new instance of Cell.
     */
    public Cell() {}

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    /**
     * Increment by one column.
     */
    public void inc() {
        col++;
        if (col >= DAYS_PER_WEEK) {
            col = 0;
            row++;
        }
    }
}
