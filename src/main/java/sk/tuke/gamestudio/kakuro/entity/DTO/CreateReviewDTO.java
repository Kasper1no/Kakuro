package sk.tuke.gamestudio.kakuro.entity.DTO;

public class CreateReviewDTO {
    private String comment;
    private int rating;

    public CreateReviewDTO() {}

    public CreateReviewDTO(String comment, int rating) {
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }
    public int getRating() {
        return rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
}
