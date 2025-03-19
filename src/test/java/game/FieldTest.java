package game;

import sk.tuke.gamestudio.kakuro.core.Field;
import sk.tuke.gamestudio.kakuro.core.ValueTile;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class FieldTest {

    private final Random randomGenerator = new Random();
    private final Field field;
    private final int rowCount;
    private final int columnCount;

    public FieldTest() {
        rowCount = randomGenerator.nextInt(3,5);
        columnCount = rowCount;
        field = new Field(rowCount, columnCount);
    }

    @Test
    public void fieldMustBeEmptyAfterInitialization() {
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if(field.getTile(row, column) instanceof ValueTile tile) {
                    assertEquals(0, (int) tile.getValue(), "Value should be empty after initialization");
                }
            }
        }
    }

    @Test
    public void fieldCanBeSolved() {
        assertFalse(field.isSolved(), "Field should not be solved initially");
        field.showSolution();
        assertTrue(field.isSolved(), "Field should be solved after calling showSolution()");
    }

    @Test
    public void resetShouldResetAllValueTiles() {
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if (field.getTile(row, column) instanceof ValueTile) {
                    field.setValue(row, column, randomGenerator.nextInt(1, 9));
                }
            }
        }

        field.reset();

        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                if(field.getTile(row, column) instanceof ValueTile tile) {
                    assertEquals(0, (int) tile.getValue(), "Value should be empty after reset");
                }
            }
        }
    }

    @Test
    public void setValueShouldReturnTrueWhenValid() {
        int testRow = rowCount - 1;
        int testColumn = columnCount - 1;
        int testValue = randomGenerator.nextInt(1, 9);

        boolean result = field.setValue(testRow, testColumn, testValue);

        assertTrue(result, "setValue() should return true when setting a valid value");
    }


    @Test
    public void setValueShouldSetValueTile() {
        int testRow = rowCount - 1;
        int testColumn = columnCount - 1;
        int testValue = randomGenerator.nextInt(1, 9);

        field.setValue(testRow, testColumn, testValue);

        if (field.getTile(testRow, testColumn) instanceof ValueTile tile) {
            assertEquals(testValue, tile.getValue(), "Tile value should be set correctly");
        }
    }

    @Test
    public void setValueShouldReturnFalseWhenInvalid() {
        int testRow = rowCount - 1;
        int testColumn = 0;
        int testValue = randomGenerator.nextInt(1, 9);

        boolean result = field.setValue(testRow, testColumn, testValue);
        assertFalse(result, "setValue() should return false when setting an invalid value");
    }

    @Test
    public void setValueShouldNotChangeValueTileWhenInvalid() {
        int testRow = 0;
        int testColumn = columnCount - 1;
        int testValue = randomGenerator.nextInt(1, 9);

        int originalValue = 0;
        if (field.getTile(testRow, testColumn) instanceof ValueTile tile) {
            originalValue = tile.getValue();
        }

        field.setValue(testRow, testColumn, testValue);

        if (field.getTile(testRow, testColumn) instanceof ValueTile tile) {
            assertEquals(originalValue, tile.getValue(), "Tile value should remain unchanged for invalid input");
        }
    }

    @Test
    public void setValueShouldReturnFalseWhenOutOfBounds() {
        int testValue = randomGenerator.nextInt(1, 9);

        assertFalse(field.setValue(-1, 0, testValue), "Should return false for negative row index");
        assertFalse(field.setValue(rowCount, 0, testValue), "Should return false for row index out of bounds");
        assertFalse(field.setValue(0, -1, testValue), "Should return false for negative column index");
        assertFalse(field.setValue(0, columnCount, testValue), "Should return false for column index out of bounds");
    }

    @Test
    public void setValueShouldReturnFalseForInvalidNumbers() {
        int testRow = rowCount - 1;
        int testColumn = columnCount - 1;

        assertFalse(field.setValue(testRow, testColumn, 0), "Should return false for zero");
        assertFalse(field.setValue(testRow, testColumn, -1), "Should return false for negative numbers");
        assertFalse(field.setValue(testRow, testColumn, 10), "Should return false for numbers greater than allowed");
    }


}
