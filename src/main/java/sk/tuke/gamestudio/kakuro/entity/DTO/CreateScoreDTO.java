package sk.tuke.gamestudio.kakuro.entity.DTO;

public class CreateScoreDTO {
    private int score;

    public CreateScoreDTO() {}

    public CreateScoreDTO(int score) {
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
