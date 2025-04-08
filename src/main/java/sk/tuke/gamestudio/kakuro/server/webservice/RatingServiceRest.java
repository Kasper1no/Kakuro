package sk.tuke.gamestudio.kakuro.server.webservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.kakuro.entity.Rating;
import sk.tuke.gamestudio.kakuro.service.RatingService;


@RestController
@RequestMapping("/api/rating")
public class RatingServiceRest {

    @Autowired
    private RatingService ratingService;

    @GetMapping("/game/{game}/average")
    public double getAvgRating(@PathVariable String game){
        return ratingService.getAverageRating(game);
    }

    @GetMapping("//game/{game}/player/{name}")
    public int getPersonalRating(@PathVariable String game, @PathVariable String name){
        return ratingService.getRating(game, name);
    }

    @PostMapping
    public void addRating(@RequestBody Rating rating){
        ratingService.setRating(rating);
    }
}
