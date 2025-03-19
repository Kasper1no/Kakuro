package services;

import sk.tuke.gamestudio.kakuro.entity.Rating;
import sk.tuke.gamestudio.kakuro.service.RatingServiceJDBC;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class RatingServiceTest {
    private final RatingServiceJDBC ratingService;

    public RatingServiceTest() {
        ratingService = new RatingServiceJDBC();
    }

    @Test
    public void resetTest(){
        ratingService.reset();

        assertEquals(0, ratingService.getAverageRating("Kakuro"));
    }

    @Test
    public void setRatingTest(){
        ratingService.reset();

        Date date = new Date();
        ratingService.setRating(new Rating("Kakuro","Player",3,date));

        assertEquals(3, ratingService.getRating("Kakuro", "Player"));
    }

    @Test
    public void getRatingTest(){
        ratingService.reset();

        Date date = new Date();
        ratingService.setRating(new Rating("Kakuro","Player",5, date));

        assertEquals(5, ratingService.getRating("Kakuro", "Player"));
        assertEquals(5, ratingService.getAverageRating("Kakuro"));
    }
}
