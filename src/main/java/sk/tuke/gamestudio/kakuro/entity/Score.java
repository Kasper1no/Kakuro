package sk.tuke.gamestudio.kakuro.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@NamedQuery(name = "Score.getTopScores",
        query = "SELECT s FROM Score s WHERE s.game = :game ORDER BY s.points DESC")
@NamedQuery(name = "Score.resetScores",
        query = "DELETE FROM Score")
public class Score implements Serializable {

    @Id
    @GeneratedValue
    private int ident;

    private String game;

    @ManyToOne
    @JoinColumn(name = "player_nickname", referencedColumnName = "nickname", nullable = true)
    private Player player;

    private int points;

    private Date playedOn;

    public Score() {}

    public Score(String game, String playerNickname, int points, Date playedOn) {
        this.game = game;
        this.player = new Player(playerNickname, null, null);
        this.points = points;
        this.playedOn = playedOn;
    }

    public Score(String game, Player player, int points, Date playedOn) {
        this.game = game;
        this.player = player;
        this.points = points;
        this.playedOn = playedOn;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public Date getPlayedOn() {
        return playedOn;
    }

    public void setPlayedOn(Date playedOn) {
        this.playedOn = playedOn;
    }

    @Override
    public String toString() {
        return "Score{" +
                "game='" + game + '\'' +
                ", player=" + player.getNickname() +
                ", points=" + points +
                ", playedOn=" + playedOn +
                '}';
    }
}
