package sk.tuke.gamestudio.kakuro.core;

import java.util.*;

public class Field {
    private final int rowsCount;
    private final int columnsCount;
    private final Tile[][] field;
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String PURPLE = "\u001B[35m";
    public static final String RESET = "\u001B[0m";


    public Field(int rowsCount, int columnsCount) {
        this.rowsCount = rowsCount;
        this.columnsCount = columnsCount;

        this.field = new Tile[rowsCount][columnsCount];
        generateField();
    }

    private void generateField() {
        if (this.rowsCount < 5) {
            generateSmall();
        }
        else {
            generateHard();
        }
    }

    public boolean setValue(int row, int column, int value) {
        if((row < 0 || row >= rowsCount) || (column < 0 || column >= columnsCount) || (value <= 0 || value >= 10)) return false;
        if(!(field[row][column] instanceof ValueTile tile) || tile.getCorrectValue() <= 0) return false;
        tile.setValue(value);
        return true;
    }

    public boolean isSolved(){
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < columnsCount; j++) {
                if(field[i][j] instanceof SumTile tile){
                    if(!checkSumTile(tile, i, j))return false;
                }
            }
        }
        return true;
    }

    private boolean checkSumTile(SumTile sumTile, int x, int y) {
        List<Integer> values = new ArrayList<>();
        int sum = 0;
        boolean colSum = sumTile.getColSum() > 0;
        boolean rowSum = sumTile.getRowSum() > 0;
        boolean colResult = false;
        boolean rowResult = false;
        if(colSum){
            while(x + 1 < columnsCount && field[++x][y] instanceof ValueTile tile){
                if(!values.contains(tile.getValue())){
                    values.add(tile.getValue());
                }else{
                    return false;
                }
            }
            for (int value : values) {
                sum += value;
            }
            if(sum == sumTile.getColSum()){
                colResult = true;
            }
        }
        if(rowSum){
            values.clear();
            sum = 0;
            while (y + 1 < rowsCount && field[x][++y] instanceof ValueTile tile){
                if(!values.contains(tile.getValue())){
                    values.add(tile.getValue());
                }else{
                    return false;
                }
            }
            for (int value : values) {
                sum += value;
            }
            if(sum == sumTile.getRowSum()){
                rowResult = true;
            }
        }
        if(colSum && rowSum){
            return colResult && rowResult;
        }else if(colSum){
            return colResult;
        }else if(rowSum){
            return rowResult;
        }
        return false;
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

    private void generateTiles(SumTile tile, int x, int y, int rowsCount) {
        if (tile.getColSum() > 0) {
            fillColumnTile(tile, x, y, rowsCount);
        }
    }

    private void generateSmall() {
        ValueTile tile = new ValueTile(0);
        tile.setValue(0);
        field[0][0] = tile;

        List<Integer> previousSums = new ArrayList<>();

        for (int row = 0; row < rowsCount; row++) {
            for (int column = 0; column < columnsCount; column++) {
                if (field[row][column] instanceof ValueTile) continue;
                if (sumTileCanBePlaced(row, column)) {
                    int colSum = row == 0 ? getRandomNumberForTiles(rowsCount - 1,previousSums) : 0;
                    int rowSum = column == 0 ? calcRowSum(row, column) : 0;

                    previousSums.add(colSum);
                    SumTile sumTile = new SumTile(rowSum, colSum);
                    generateTiles(sumTile, row, column, columnsCount - 1);
                    field[row][column] = sumTile;
                }
            }
        }
    }

    public void generateHard() {
        List<Integer> previousSums = new ArrayList<>();
        int rowSum, colSum  = getRandomNumberForTiles(3, previousSums);
        SumTile tile = new SumTile(0, colSum);

        // Static Hard Level Generation
        generateTiles(tile, 0, 4, 3);
        field[0][4] = tile;
        colSum  = getRandomNumberForTiles(3, previousSums);
        tile = new SumTile(0, colSum);
        generateTiles(tile, 0, 3, 3);
        field[0][3] = tile;
        colSum  = getRandomNumberForTiles(3, previousSums);
        rowSum  = calcRowSum(1, 2);
        generateTiles(tile, 1, 2, 3);
        tile = new SumTile(rowSum, colSum);
        field[1][2] = tile;
        colSum  = getRandomNumberForTiles(3, previousSums);
        tile = new SumTile(0, colSum);
        generateTiles(tile, 1, 1, 3);
        field[1][1] = tile;
        rowSum  = calcRowSum(2, 0);
        tile = new SumTile(rowSum, 0);
        field[2][0] = tile;
        rowSum = calcRowSum(3, 0);
        tile = new SumTile(rowSum, 0);
        field[3][0] = tile;
        rowSum = calcRowSum(4, 0);
        tile = new SumTile(rowSum, 0);
        field[4][0] = tile;

        for (int row = 0; row < rowsCount; row++) {
            for (int column = 0; column < columnsCount; column++) {
                if(!(field[row][column] instanceof ValueTile) && !(field[row][column] instanceof SumTile)){
                    ValueTile newTile = new ValueTile(0);
                    newTile.setValue(0);
                    field[row][column] = newTile;
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
            list.get(startIndex).append(GREEN + "+--------+" + RESET);
            list.get(startIndex + 1).append(GREEN + "|" + RESET);
            list.get(startIndex + 2).append(GREEN + "|" + RESET);
            list.get(startIndex + 3).append(GREEN + "+--------+" + RESET);
        } else if (left == null) {
            list.get(startIndex + 1).append(GREEN + "|" + RESET);
            list.get(startIndex + 2).append(GREEN + "|" + RESET);
            list.get(startIndex + 3).append(GREEN + "+--------+" + RESET);
        } else if (upper == null) {
            list.get(startIndex).append(GREEN + "--------+" + RESET);
            list.get(startIndex + 3).append(GREEN + "--------+" + RESET);
        } else {
            list.get(startIndex + 3).append(GREEN + "--------+" + RESET);
        }

        if (tile instanceof ValueTile) {
            Integer correctValue = ((ValueTile) tile).getCorrectValue();
            list.get(startIndex + 1).append(GREEN);
            if (correctValue != null && correctValue > 0) {
                Integer value = ((ValueTile) tile).getValue();
                String valueString = value + "";
                list.get(startIndex + 1).append("    ").append(YELLOW).append(valueString).append(RESET).append(GREEN + "   |" + RESET);
                list.get(startIndex + 2).append(GREEN + "        |" + RESET);
            } else {
                list.get(startIndex + 1).append(GREEN + "########|" + RESET);
                list.get(startIndex + 2).append(GREEN + "########|" + RESET);
            }

        } else if (tile instanceof SumTile) {
            int valueCol = ((SumTile) tile).getColSum();
            int valueRow = ((SumTile) tile).getRowSum();
            String colSumStr = valueCol > 0 ? (valueCol + (valueCol < 10 ? " " : "")) : "  ";
            String rowSumStr = valueRow > 0 ? (valueRow + (valueRow < 10 ? " " : "")) : "  ";
            list.get(startIndex + 1).append(GREEN + "  \\\\ " + RESET).append(PURPLE).append(rowSumStr).append(RESET).append(GREEN + " |" + RESET);
            list.get(startIndex + 2).append(" ").append(PURPLE).append(colSumStr).append(RESET).append(GREEN + " \\\\  |" + RESET);
        }

    }

    private void setBorders(List<StringBuilder> list,int row, int col){
        for (int i = 0; i < row; i++) {
            int startIndex = (4 * (i) - (i - 1)) - 1;
            String sym = String.valueOf(i);
            if (i == 0) {
                list.get(startIndex).append(GREEN + "--------+" + RESET);
                list.get(startIndex + 1).append(GREEN + "########|" + RESET);
                list.get(startIndex + 2).append(GREEN + "########|" + RESET);
                list.get(startIndex + 3).append(GREEN + "--------+" + RESET);
            } else {
                list.get(startIndex + 1).append("    ").append(RED).append(sym).append(RESET).append(GREEN + "   |" + RESET);
                list.get(startIndex + 2).append(GREEN + "        |" + RESET);
                list.get(startIndex + 3).append(GREEN + "--------+" + RESET);
            }
        }
        int start = (4 * (row) - (row - 1)) - 1;
        for ( int i = 0; i < col + 1; i++){
            String sym = String.valueOf((char)(65 + i - 1));
            if( i == 0 ){
                list.get(start+1).append(GREEN + "|" + RESET);
                list.get(start+2).append(GREEN + "|" + RESET);
                list.get(start+3).append(GREEN + "+" + RESET);
            }

            if (i == 0 || i == col) {
                list.get(start + 1).append(GREEN + "########|" + RESET);
                list.get(start + 2).append(GREEN + "########|" + RESET);
                list.get(start + 3).append(GREEN + "--------+" + RESET);
            } else {
                list.get(start + 1).append("    ").append(RED).append(sym).append(RESET).append(GREEN + "   |" + RESET);
                list.get(start + 2).append(GREEN + "        |" + RESET);
                list.get(start + 3).append(GREEN + "--------+" + RESET);
            }
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
