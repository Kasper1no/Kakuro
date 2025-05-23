package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Rating;

public interface RatingService {
    void setRating(Rating rating) throws RatingException;
    int getRatingCount(String game) throws RatingException;
    double getAverageRating(String game) throws RatingException;
    int getRating(String game, String player) throws RatingException;
    void reset() throws RatingException;
}
