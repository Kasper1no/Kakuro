package sk.tuke.gamestudio.kakuro.entity.DTO;

import sk.tuke.gamestudio.kakuro.core.Field;

public class UpdateResponseDTO {
    private Field field;
    private int score;

    public UpdateResponseDTO(Field field, int score) {
        this.field = field;
        this.score = score;
    }

    public Field getField() {
        return field;
    }

    public int getScore() {
        return score;
    }
}
