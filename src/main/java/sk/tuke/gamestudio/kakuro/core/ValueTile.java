package sk.tuke.gamestudio.kakuro.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ValueTile extends Tile {

    @JsonProperty("curValue")
    private Integer curValue;

    @JsonProperty("correctValue")
    private final Integer correctValue;

    public ValueTile(Integer correctValue) {
        curValue = null;
        this.correctValue = correctValue;
    }

    @JsonProperty("correctValue")
    public Integer getCorrectValue() {
        return correctValue;
    }

    public void setValue(Integer value) {
        this.curValue = value;
    }

    @JsonProperty("curValue")
    public Integer getValue() {
        return curValue != null ? curValue : 0;
    }

}
