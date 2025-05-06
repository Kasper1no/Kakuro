package sk.tuke.gamestudio.kakuro.entity.DTO;

import sk.tuke.gamestudio.kakuro.entity.Player;

public class ScoreDTO {
    private final Player player;
    private final int points;
    private final String playedOnFormatted;

    public ScoreDTO(Player player, int points, String playedOnFormatted) {
        this.player = player;
        this.points = points;
        this.playedOnFormatted = playedOnFormatted;
    }

    public Player getPlayer() {
        return player;
    }

    public int getPoints() {
        return points;
    }

    public String getPlayedOnFormatted() {
        return playedOnFormatted;
    }
}
