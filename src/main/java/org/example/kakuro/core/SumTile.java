package org.example.kakuro.core;

import java.util.ArrayList;
import java.util.List;

public class SumTile extends Tile {
    private final int rowSum;
    private final int colSum;

    public SumTile( int rowSum, int colSum) {
        this.rowSum = rowSum;
        this.colSum = colSum;
    }

    public int getRowSum() {
        return rowSum;
    }

    public int getColSum() {
        return colSum;
    }
}
