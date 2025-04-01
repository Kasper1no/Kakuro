package sk.tuke.gamestudio.kakuro.entity;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import java.util.Date;

@Entity
@NamedQuery(name = "Comment.getCommentsByGame",
        query = "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC")
@NamedQuery( name = "Comment.resetComments",
        query = "DELETE FROM Comment ")
public class Comment {
    @Id
    @GeneratedValue
    private int ident;

    private String game;

    private String player;

    private String comment;

    private Date commentedOn;

    public Comment() {}

    public Comment(String game, String player, String comment, Date commentedOn) {
        this.game = game;
        this.player = player;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public String getGame() {
        return game;
    }

    public String getPlayer() {
        return player;
    }

    public String getComment() {
        return comment;
    }

    public Date getCommentedOn() {
        return commentedOn;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentedOn(Date commentedOn) {
        this.commentedOn = commentedOn;
    }
}
