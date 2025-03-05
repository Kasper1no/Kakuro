package org.example.kakuro.core;

import java.util.ArrayList;
import java.util.List;

public class SumTile extends Tile {
    private int rowSum;
    private int colSum;
    private final List<ValueTile> rowTiles;
    private final List<ValueTile> colTiles;
    private List<Integer> correctRow;
    private List<Integer> correctCol;

    public SumTile( int rowSum, int colSum) {
        this.rowSum = rowSum;
        this.colSum = colSum;
        this.rowTiles = new ArrayList<>();
        this.colTiles = new ArrayList<>();
    }

    public int getRowSum() {
        return rowSum;
    }

    public int getColSum() {
        return colSum;
    }

    public void addRowTile(ValueTile tile) {
        rowTiles.add(tile);
    }

    public void addColTile(ValueTile tile) {
        colTiles.add(tile);
    }

    public List<ValueTile> getRowTiles() {
        return rowTiles;
    }

    public List<ValueTile> getColTiles() {
        return colTiles;
    }

    public List<Integer> getCorrectRow() {
        return correctRow;
    }

    public List<Integer> getCorrectCol() {
        return correctCol;
    }

    public void setCorrectRow(List<Integer> correctRow) {
        this.correctRow = correctRow;
    }

    public void setCorrectCol(List<Integer> correctCol) {
        this.correctCol = correctCol;
    }


}
