package sk.tuke.gamestudio.kakuro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.kakuro.entity.Rating;
import sk.tuke.gamestudio.kakuro.entity.Score;

public class RatingServiceRestClient implements RatingService{
    private final String url = "http://localhost:8080/api/rating";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void setRating(Rating rating) throws RatingException {
        restTemplate.postForEntity(url, rating, Rating.class);
    }

    @Override
    public int getRatingCount(String game) throws RatingException {
        return restTemplate.getForObject(url+"/game/"+game+"/count", Integer.class);
    }

    @Override
    public double getAverageRating(String game) throws RatingException {
        return restTemplate.getForObject(url+"/game/"+game+"/average", Double.class);
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        return restTemplate.getForObject(url+"/game/"+game+"/player/"+player, Integer.class);
    }

    @Override
    public void reset() throws RatingException {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}
