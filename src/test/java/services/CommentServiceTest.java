package services;

import sk.tuke.gamestudio.kakuro.entity.Comment;
import sk.tuke.gamestudio.kakuro.service.CommentServiceJDBC;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommentServiceTest {
    private final CommentServiceJDBC commentService;

    public CommentServiceTest() {
        commentService = new CommentServiceJDBC();
    }

    @Test
    public void resetTest(){
        commentService.reset();

        assertEquals(0, commentService.getComments("Kakuro").size());
    }

    @Test
    public void setCommentTest(){
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("Kakuro","Player","Cool game!",date));

        assertEquals(1, commentService.getComments("Kakuro").size());

        List<Comment> comments = commentService.getComments("Kakuro");

        assertEquals("Kakuro",comments.get(0).getGame());
        assertEquals("Player", comments.get(0).getPlayer());
        assertEquals("Cool game!", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());
    }

    @Test
    public void getCommentsTest(){
        commentService.reset();

        Date date = new Date();
        commentService.addComment(new Comment("Kakuro","Player1","My comment",date));
        commentService.addComment(new Comment("Kakuro","John","funny game",date));
        commentService.addComment(new Comment("Chess","Player1","My comment",date));

        List<Comment> comments = commentService.getComments("Kakuro");

        assertEquals(2, comments.size());

        assertEquals("Kakuro",comments.get(0).getGame());
        assertEquals("Player1", comments.get(0).getPlayer());
        assertEquals("My comment", comments.get(0).getComment());
        assertEquals(date, comments.get(0).getCommentedOn());

        assertEquals("Kakuro",comments.get(1).getGame());
        assertEquals("John", comments.get(1).getPlayer());
        assertEquals("funny game", comments.get(1).getComment());
        assertEquals(date, comments.get(1).getCommentedOn());
    }

}
