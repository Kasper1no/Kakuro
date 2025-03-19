package services;

import sk.tuke.gamestudio.kakuro.entity.Score;
import sk.tuke.gamestudio.kakuro.service.ScoreServiceJDBC;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ScoreServiceTest {
    private final ScoreServiceJDBC scoreService;

    public ScoreServiceTest() {
        scoreService = new ScoreServiceJDBC();
    }

    @Test
    public void testReset(){
        scoreService.reset();

        assertEquals(0, scoreService.getTopScores("Kakuro").size());
    }

    @Test
    public void addScore(){
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("Kakuro","ProMan",10, date));

        List<Score> scores = scoreService.getTopScores("Kakuro");
        assertEquals(1, scores.size());

        assertEquals("Kakuro", scores.get(0).getGame());
        assertEquals("ProMan", scores.get(0).getPlayer());
        assertEquals(10, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());
    }

    @Test
    public void getTopScores(){
        scoreService.reset();

        Date date = new Date();
        scoreService.addScore(new Score("Kakuro", "John",20,date));
        scoreService.addScore(new Score("Kakuro", "James",0,date));
        scoreService.addScore(new Score("Chess", "Michael",35,date));
        scoreService.addScore(new Score("Kakuro", "Lucy",15,date));

        List<Score> scores = scoreService.getTopScores("Kakuro");

        assertEquals(3, scores.size());

        assertEquals("Kakuro", scores.get(0).getGame());
        assertEquals("John", scores.get(0).getPlayer());
        assertEquals(20, scores.get(0).getPoints());
        assertEquals(date, scores.get(0).getPlayedOn());

        assertEquals("Kakuro", scores.get(1).getGame());
        assertEquals("Lucy", scores.get(1).getPlayer());
        assertEquals(15, scores.get(1).getPoints());
        assertEquals(date, scores.get(1).getPlayedOn());

        assertEquals("Kakuro", scores.get(2).getGame());
        assertEquals("James", scores.get(2).getPlayer());
        assertEquals(0, scores.get(2).getPoints());
        assertEquals(date, scores.get(2).getPlayedOn());
    }
}
