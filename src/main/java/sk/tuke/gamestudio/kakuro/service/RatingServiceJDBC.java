package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Rating;

import java.sql.*;

public class RatingServiceJDBC implements RatingService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "admin";
    public static final String SELECT = "SELECT rating FROM rating WHERE game = ? AND player = ?";
    public static final String AVG = "SELECT AVG(rating) FROM rating WHERE game = ?";
    public static final String DELETE = "DELETE FROM rating";
    public static final String UPDATE = "UPDATE rating SET rating = ?, ratedon = ? WHERE game = ? AND player = ?";
    public static final String INSERT = "INSERT INTO rating (game, player, rating, ratedon) VALUES (?, ?, ?, ?)";


    @Override
    public void setRating(Rating rating) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
        ) {
            try (PreparedStatement selectStatement = connection.prepareStatement(SELECT)) {
                selectStatement.setString(1, rating.getGame());
                selectStatement.setString(2, rating.getPlayer());
                ResultSet rs = selectStatement.executeQuery();

                if (rs.next()) {
                    try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE)) {
                        updateStatement.setInt(1, rating.getRating());
                        updateStatement.setTimestamp(2, new Timestamp(rating.getRatedOn().getTime()));
                        updateStatement.setString(3, rating.getGame());
                        updateStatement.setString(4, rating.getPlayer());
                        updateStatement.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insertStatement = connection.prepareStatement(INSERT)) {
                        insertStatement.setString(1, rating.getGame());
                        insertStatement.setString(2, rating.getPlayer());
                        insertStatement.setInt(3, rating.getRating());
                        insertStatement.setTimestamp(4, new Timestamp(rating.getRatedOn().getTime()));
                        insertStatement.executeUpdate();
                    }
                }

            }
        } catch (SQLException e) {
            throw new RatingException("Problem setting rating", e);
        }
    }

    @Override
    public double getAverageRating(String game) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(AVG)
        ) {
            statement.setString(1, game);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double averageRating = rs.getDouble(1);
                if (rs.wasNull()) {
                    return 0;
                }
                return averageRating;
            }
            return 0;
        } catch (SQLException e) {
            throw new RatingException("Problem getting average rating", e);
        }
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, game);
            statement.setString(2, player);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RatingException("Problem getting rating", e);
        }
    }

    @Override
    public void reset() throws RatingException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new RatingException("Problem resetting rating", e);
        }
    }
}
