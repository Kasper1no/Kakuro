package org.example.kakuro.core;

import java.util.*;

public class Field {
    private final int rowsCount;
    private final int columnsCount;
    private final Tile[][] field;

    public Field(int rowsCount, int columnsCount) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;

        this.field = new Tile[rowsCount][columnsCount];
        generateField();
    }

    private void generateField() {
        if (this.rowsCount <= 5) {
            generateSmall();
        }
//        else {
//            generateBig();
//        }
    }

    public boolean setValue(int row, int column, int value) {
        if((row < 0 || row >= rowsCount) || (column < 0 || column >= columnsCount) || (value <= 0 || value >= 10)) return false;
        if(!(field[row][column] instanceof ValueTile tile)) return false;
        tile.setValue(value);
        return true;
    }

    public boolean isSolved(){
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                if(field[i][j] instanceof ValueTile tile){
                    if(tile.getCorrectValue() <= 0) continue;
                    if(!Objects.equals(tile.getCorrectValue(), tile.getValue())) return false;
                }
            }
        }
        return true;
    }

    public Tile getTile(int x, int y){
        if(x < 0 || x >= rowsCount && y < 0 || y >= columnsCount) return null;
        return field[x][y];
    }

    private boolean sumTileCanBePlaced(int x, int y) {
        SumTile rightTile = null;
        SumTile upperTile = null;
        boolean rightIsSum, upperIsSum;

        if (y + 1 < field[0].length) {
            rightIsSum = field[x][y + 1] instanceof SumTile;
            if (rightIsSum) {
                rightTile = (SumTile) field[x][y + 1];
            }
        }

        if (x - 1 >= 0) {
            upperIsSum = field[x - 1][y] instanceof SumTile;
            if (upperIsSum) {
                upperTile = (SumTile) field[x - 1][y];
            }
        }

        boolean canPlaceBasedOnUpperTile = (upperTile == null || upperTile.getColSum() == 0);
        boolean canPlaceBasedOnRightTile = (rightTile == null || rightTile.getRowSum() == 0);

        return canPlaceBasedOnUpperTile && canPlaceBasedOnRightTile;
    }

    private List<List<Integer>> getPossibleSums(int num, int count) {
        List<List<Integer>> sums = new ArrayList<>();
        findCombinations(sums, new ArrayList<>(), 0, count, num);
        return sums;
    }

    private void findCombinations(List<List<Integer>> combinations, List<Integer> combination, int sum, int count, int targetSum) {
        if (count == 0) {
            if (sum == targetSum) {
                combinations.add(combination);
            } else if (!combination.isEmpty()) {
                combination.remove(combination.size() - 1);
            }
            return;
        }
        for (int i = 1; i <= 9; i++) {
            if (sum + i <= targetSum && !combination.contains(i)) {
                combination.add(i);

                List<Integer> newCombination = new ArrayList<>(combination);
                newCombination.sort(Integer::compare);

                combination.remove(combination.size() - 1);

                if (combinations.contains(newCombination)) continue;
                findCombinations(combinations, newCombination, sum + i, count - 1, targetSum);
            }
        }
    }

    private int getRandomNumberForTiles(int numTile, List<Integer> sums) {
        int max = 0;
        int min = 0;
        List<Integer> maxNums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        List<Integer> minNums = new ArrayList<>(List.copyOf(maxNums));
        while (numTile-- > 0) {
            max += maxNums.remove(maxNums.size() - 1);
            min += minNums.remove(0);
        }
        int random;
        do{
            random = new Random(System.currentTimeMillis()).nextInt(min, max);
        }while (sums.contains(random));
        return random;
    }

    private int calcRowSum(int x, int y) {
        int sum = 0;

        while (true) {
            if(y + 1 >= rowsCount) break;
            Tile tile = field[x][++y];
            if (tile instanceof ValueTile && ((ValueTile) tile).getValue() != null) {
                sum += ((ValueTile) tile).getCorrectValue();
            } else break;
        }
        return sum;
    }

    private boolean checkTileMatches(List<Integer> combination, int x, int y) {
        for (int num : combination) {
            ++x;
            while (y - 1 >= 0) {
                y--;
                if (field[x][y] instanceof ValueTile && num == ((ValueTile) (field[x][y])).getCorrectValue()) {
                    return false;
                }
            }
            while (y + 1 < columnsCount) {
                y++;
                if (field[x][y] instanceof ValueTile && num == ((ValueTile) (field[x][y])).getCorrectValue()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void fillRowTile(SumTile tile, int x, int y, int count) {
        ValueTile newTile;
        for (int i = 0; i < count; i++) {
            if (y + 1 < columnsCount && field[x][++y] instanceof ValueTile) {
                newTile = (ValueTile) field[x][y];
            }
        }
    }

    private void fillColumnTile(SumTile tile, int x, int y, int count) {
        boolean flag = true;
        List<List<Integer>> combinations = getPossibleSums(tile.getColSum(), count);
        List<Integer> randomCombination = null, copyCombination = null;

        while (true) {
            if (flag) {
                randomCombination = combinations.get(new Random().nextInt(0, combinations.size()));
                copyCombination = new ArrayList<>(randomCombination);
                flag = false;
            }
            Collections.rotate(copyCombination, 1);
            if (checkTileMatches(copyCombination, x, y)) {
                break;
            } else if (copyCombination == randomCombination) {
                flag = true;
            }
        }

        for (Integer num : copyCombination) {
            ValueTile newTile = new ValueTile(num);
            newTile.setValue(0);
            field[++x][y] = newTile;
        }
    }

    private void generateTiles(SumTile tile, int x, int y, int rowsCount, int colsCount) {
        if (tile.getRowSum() > 0) {
            fillRowTile(tile, x, y, rowsCount);
        }
        if (tile.getColSum() > 0) {
            fillColumnTile(tile, x, y, colsCount);
        }
    }

    private void generateSmall() {
        field[0][0] = new ValueTile(0);
        ValueTile tile = (ValueTile) field[0][0];
        tile.setValue(0);

        List<Integer> previousSums = new ArrayList<>();

        for (int row = 0; row < rowsCount; row++) {
            for (int column = 0; column < columnsCount; column++) {
                if (field[row][column] instanceof ValueTile) continue;
                if (sumTileCanBePlaced(row, column)) {
                    int colSum = row == 0 ? getRandomNumberForTiles(rowsCount - 1,previousSums) : 0;
                    int rowSum = column == 0 ? calcRowSum(row, column) : 0;

                    previousSums.add(colSum);
                    SumTile sumTile = new SumTile(rowSum, colSum);
                    generateTiles(sumTile, row, column, rowsCount - 1, columnsCount - 1);
                    field[row][column] = sumTile;
                }
            }
        }
    }

    private void prepareListString(List<StringBuilder> list) {
        int count = (4 * (rowsCount+1) - (rowsCount - 1));
        for (int i = 0; i < count; i++) {
            list.add(new StringBuilder());
        }
    }

    private void tileToString(List<StringBuilder> list, Tile tile, int x, int y) {
        Tile upper = null, left = null;
        int startIndex = (4 * (x) - (x - 1)) - 1;
        if (x - 1 >= 0) upper = field[x - 1][y];
        if (y - 1 >= 0) left = field[x][y - 1];
        if (upper == null && left == null) {
            list.get(startIndex).append("+--------+");
            list.get(startIndex + 1).append("|");
            list.get(startIndex + 2).append("|");
            list.get(startIndex + 3).append("+--------+");
        } else if (left == null) {
            list.get(startIndex + 1).append("|");
            list.get(startIndex + 2).append("|");
            list.get(startIndex + 3).append("+--------+");
        } else if (upper == null) {
            list.get(startIndex).append("--------+");
            list.get(startIndex + 3).append("--------+");
        } else {
            list.get(startIndex + 3).append("--------+");
        }

        if (tile instanceof ValueTile) {
            Integer value = ((ValueTile) tile).getValue();
            String valueString = value != null && value > 0 ? value + "" : " ";
            list.get(startIndex + 1).append("    ").append(valueString).append("   |");
            list.get(startIndex + 2).append("        |");
        } else if (tile instanceof SumTile) {
            int valueCol = ((SumTile) tile).getColSum();
            int valueRow = ((SumTile) tile).getRowSum();
            String colSumStr = valueCol > 0 ? (valueCol + (valueCol < 10 ? " " : "")) : "  ";
            String rowSumStr = valueRow > 0 ? (valueRow + (valueRow < 10 ? " " : "")) : "  ";
            list.get(startIndex + 1).append("  \\\\ ").append(rowSumStr).append(" |");
            list.get(startIndex + 2).append(" ").append(colSumStr).append(" \\\\  |");
        }

    }

    private void setBorders(List<StringBuilder> list,int row, int col){
        for (int i = 0; i < row; i++) {
            int startIndex = (4 * (i) - (i - 1)) - 1;
            String sym = String.valueOf(i);
            if( i == 0 ){
                list.get(startIndex).append("--------+");
                sym = " ";
            }
            list.get(startIndex + 1).append("    ").append(sym).append("   |");
            list.get(startIndex + 2).append("        |");
            list.get(startIndex + 3).append("--------+");
        }
        int start = (4 * (row) - (row - 1)) - 1;
        for ( int i = 0; i < col + 1; i++){
            String sym = String.valueOf((char)(65 + i - 1));
            if( i == 0 ){
                list.get(start+1).append("|");
                list.get(start+2).append("|");
                list.get(start+3).append("+");
            }
            if(i == 0 || i == col) sym = " ";
            list.get(start + 1).append("    ").append(sym).append("   |");
            list.get(start + 2).append("        |");
            list.get(start + 3).append("--------+");
        }
    }

    public void printField() {
        List<StringBuilder> stringField = new ArrayList<>();
        prepareListString(stringField);

        Tile tile;
        for (int row = 0; row < rowsCount; row++) {
            for (int col = 0; col < columnsCount; col++) {
                tile = field[row][col];
                tileToString(stringField, tile, row, col);
            }
        }
        setBorders(stringField, rowsCount, columnsCount);
        for (StringBuilder sb : stringField) {
            System.out.println(sb.toString());
        }
    }

    public void showSolution(){
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                if(field[i][j] instanceof ValueTile tile) {
                    tile.setValue(tile.getCorrectValue());
                }
            }
        }
    }

    public void reset() {
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                if(field[i][j] instanceof ValueTile tile) {
                    tile.setValue(0);
                }
            }
        }
    }


}
