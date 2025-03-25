package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Score;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ScoreServiceJDBC implements ScoreService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "admin";
    public static final String SELECT = "SELECT game, player, points, playedOn FROM score WHERE game = ? ORDER BY points DESC LIMIT 10";
    public static final String SELECT_POINTS = "SELECT points FROM score WHERE game = ? AND player = ?";
    public static final String DELETE = "DELETE FROM score";
    public static final String INSERT = "INSERT INTO score (game, player, points, playedOn) VALUES (?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE score SET points = ?, playedOn = ? WHERE game = ? AND player = ?";

    @Override
    public void addScore(Score score) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        ) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SELECT_POINTS)) {
                selectStatement.setString(1, score.getGame());
                selectStatement.setString(2, score.getPlayer());

                ResultSet rs = selectStatement.executeQuery();
                if (rs.next()) {
                    int points = rs.getInt(1);
                    try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                        updateStatement.setInt(1, score.getPoints() + points);
                        updateStatement.setTimestamp(2, new Timestamp(score.getPlayedOn().getTime()));
                        updateStatement.setString(3, score.getGame());
                        updateStatement.setString(4, score.getPlayer());
                        updateStatement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                        insertStatement.setString(1, score.getGame());
                        insertStatement.setString(2, score.getPlayer());
                        insertStatement.setInt(3, score.getPoints());
                        insertStatement.setTimestamp(4, new Timestamp(score.getPlayedOn().getTime()));
                        insertStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem inserting score", e);
        }
    }

    @Override
    public List<Score> getTopScores(String game) {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT);
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Score> scores = new ArrayList<>();
                while (rs.next()) {
                    scores.add(new Score(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getTimestamp(4)));
                }
                return scores;
            }
        } catch (SQLException e) {
            throw new ScoreException("Problem selecting score", e);
        }
    }

    @Override
    public void reset() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new ScoreException("Problem deleting score", e);
        }
    }
}
