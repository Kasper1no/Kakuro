package org.example.kakuro.core;

public class ValueTile extends Tile {
    private Integer curValue;
    private final Integer correctValue;

    public ValueTile(Integer correctValue) {
        curValue = null;
        this.correctValue = correctValue;
    }

    public Integer getCorrectValue() {
        return correctValue;
    }

    public void setValue(Integer value) {
        this.curValue = value;
    }

    public Integer getValue() {
        return curValue;
    }

}
