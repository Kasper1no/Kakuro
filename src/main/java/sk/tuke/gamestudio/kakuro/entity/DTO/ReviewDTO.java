package sk.tuke.gamestudio.kakuro.entity.DTO;


import sk.tuke.gamestudio.kakuro.entity.Player;

public class ReviewDTO {
    private final Player player;
    private final String comment;
    private final int rating;
    private final String date;

    public ReviewDTO(Player player, String comment, int rating, String date) {
        this.player = player;
        this.comment = comment;
        this.rating = rating;
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public Player getPlayer() {
        return player;
    }

    public String getComment() {
        return comment;
    }

    public String getDate() {
        return date;
    }


}

