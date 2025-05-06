package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Comment;
import sk.tuke.gamestudio.kakuro.entity.Player;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentServiceJDBC implements CommentService {
    public static final String URL = "jdbc:postgresql://localhost/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "admin";
    public static final String SELECT = "SELECT game, player_nickname, comment, commentedon FROM comment WHERE game = ?";
    public static final String SELECT_BY_PLAYER = "SELECT game, player_nickname, comment, commentedon FROM comment WHERE game = ? AND player_nickname = ?";
    public static final String DELETE = "DELETE FROM comment";
    public static final String INSERT = "INSERT INTO comment (game, player_nickname, comment, commentedon) VALUES (?, ?, ?, ?)";

    @Override
    public void addComment(Comment comment) throws CommentException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(INSERT)
        ) {
            statement.setString(1, comment.getGame());
            statement.setString(2, comment.getPlayer().getNickname());
            statement.setString(3, comment.getComment());
            statement.setTimestamp(4, new Timestamp(comment.getCommentedOn().getTime()));
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new CommentException("Problem inserting a comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(SELECT)
        ) {
            statement.setString(1, game);
            try (ResultSet rs = statement.executeQuery()) {
                List<Comment> comments = new ArrayList<>();
                while (rs.next()) {
                    comments.add(new Comment(rs.getString(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4)));
                }
                return comments;
            }

        } catch (SQLException e) {
            throw new CommentException("Problem getting comments", e);
        }
    }

    @Override
    public void reset() throws CommentException {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(DELETE);
        } catch (SQLException e) {
            throw new CommentException("Problem resetting comments", e);
        }
    }

    @Override
    public Comment getComment(String game, String nickname) throws CommentException {
        try (
                Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement statement = connection.prepareStatement(SELECT_BY_PLAYER)
        ) {
            statement.setString(1, game);
            statement.setString(2, nickname);

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    String gameName = rs.getString("game");
                    String playerNickname = rs.getString("player_nickname");
                    String commentText = rs.getString("comment");
                    Date commentedOn = rs.getDate("commentedon");

                    return new Comment(gameName, playerNickname, commentText, commentedOn);
                }
            }

        } catch (SQLException e) {
            throw new CommentException("Problem getting comment for user", e);
        }

        return null;
    }


}
