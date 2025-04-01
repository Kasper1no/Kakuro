package sk.tuke.gamestudio.kakuro.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Date;

@Entity
@NamedQuery(name = "Rating.getAverageRating",
        query = "SELECT AVG(r.rating) FROM Rating r WHERE r.game = :game")
@NamedQuery(name = "Rating.getRatingForPlayer",
        query = "SELECT r.rating FROM Rating r WHERE r.game = :game AND r.player = :player")
@NamedQuery( name = "Rating.resetRating",
        query = "DELETE FROM Rating ")
public class Rating {

    @Id
    @GeneratedValue
    private int ident;

    private String game;

    private String player;

    private int rating;

    private Date ratedOn;

    public Rating() {}

    public Rating(String game, String player, int rating, Date ratedOn) {
        this.game = game;
        this.player = player;
        this.ratedOn = ratedOn;
        setRating(rating);
    }

    public String getGame() {
        return game;
    }

    public String getPlayer() {
        return player;
    }

    public int getRating() {
        return rating;
    }

    public Date getRatedOn() {
        return ratedOn;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5.");
        }
        this.rating = rating;
    }

    public void setRatedOn(Date ratedOn) {
        this.ratedOn = ratedOn;
    }
}
