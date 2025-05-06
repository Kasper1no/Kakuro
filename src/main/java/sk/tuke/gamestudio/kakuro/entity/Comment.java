package sk.tuke.gamestudio.kakuro.entity;

import javax.persistence.*;
import java.util.Date;

@Entity
@NamedQuery(name = "Comment.getCommentsByGame",
        query = "SELECT c FROM Comment c WHERE c.game = :game ORDER BY c.commentedOn DESC")
@NamedQuery(name = "Comment.resetComments",
        query = "DELETE FROM Comment")
@NamedQuery(name = "Comment.getCommentByGameAndPlayer",
        query = "SELECT c FROM Comment c WHERE c.game = :game AND c.player.nickname = :nickname")
public class Comment {

    @Id
    @GeneratedValue
    private int ident;

    private String game;

    @ManyToOne
    @JoinColumn(name = "player_nickname", referencedColumnName = "nickname", nullable = true)
    private Player player;

    private String comment;

    private Date commentedOn;

    public Comment() {}

    public Comment(String game, Player player, String comment, Date commentedOn) {
        this.game = game;
        this.player = player;
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public Comment(String game, String nickname, String comment, Date commentedOn) {
        this.game = game;
        this.player = new Player(nickname, null, null);
        this.comment = comment;
        this.commentedOn = commentedOn;
    }

    public int getIdent() {
        return ident;
    }

    public String getGame() {
        return game;
    }

    public Player getPlayer() {
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCommentedOn(Date commentedOn) {
        this.commentedOn = commentedOn;
    }
}
