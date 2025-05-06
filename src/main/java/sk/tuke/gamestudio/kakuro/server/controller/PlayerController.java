package sk.tuke.gamestudio.kakuro.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.kakuro.entity.Comment;
import sk.tuke.gamestudio.kakuro.entity.DTO.CreateReviewDTO;
import sk.tuke.gamestudio.kakuro.entity.DTO.CreateScoreDTO;
import sk.tuke.gamestudio.kakuro.entity.DTO.PlayerRegistrationDTO;
import sk.tuke.gamestudio.kakuro.entity.Player;
import sk.tuke.gamestudio.kakuro.entity.Rating;
import sk.tuke.gamestudio.kakuro.entity.Score;
import sk.tuke.gamestudio.kakuro.service.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/player")
@Scope(WebApplicationContext.SCOPE_SESSION)
public class PlayerController {

    @Autowired
    private String gameName;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @Autowired
    private ScoreService scoreService;

    private Player player;

    @PostMapping("/register")
    @ResponseBody
    public ResponseEntity<String> registerPlayer(@RequestBody PlayerRegistrationDTO playerRegistrationDTO) {
        try {
            player = playerService.registerPlayer(playerRegistrationDTO.getNickname(), playerRegistrationDTO.getPassword(), playerRegistrationDTO.getAvatar());

            return ResponseEntity.ok("Registration successful for: " + playerRegistrationDTO.getNickname());
        } catch (PlayerExeption e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    @ResponseBody
    public ResponseEntity<String> loginPlayer(@RequestBody PlayerRegistrationDTO playerRegistrationDTO) {
        try {
            player = playerService.loginPlayer(
                    playerRegistrationDTO.getNickname(),
                    playerRegistrationDTO.getPassword()
            );

            return ResponseEntity.ok("Login successful for: " + player.getNickname());

        } catch (PlayerExeption e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: " + e.getMessage());
        }
    }

    @PostMapping("/logout")
    @ResponseBody
    public ResponseEntity<String> logoutPlayer() {
        player = null;
        return ResponseEntity.ok("Logout successful.");
    }

    @GetMapping("/current")
    @ResponseBody
    public ResponseEntity<?> getCurrentPlayer() {
        if (player == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No player logged in.");
        }
        return ResponseEntity.ok(player);
    }

    @PostMapping("/score")
    @ResponseBody
    public ResponseEntity<?> saveScore(@RequestBody CreateScoreDTO scoreDTO){
        try {
            if (player == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No player logged in.");
            if (scoreDTO.getScore() < 0) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid score.");
            Score score = new Score(gameName, player, scoreDTO.getScore(), new Date());
            scoreService.addScore(score);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving score: " + e.getMessage());
        }
        return ResponseEntity.ok("Score saved successfully.");
    }

    @GetMapping("/review")
    @ResponseBody
    public ResponseEntity<?> getReview(){
        Comment comment = commentService.getComment(gameName, player.getNickname());
        int rating = ratingService.getRating(gameName, player.getNickname());

        Map<String, Object> response = new HashMap<>();
        response.put("comment", comment != null ? comment.getComment() : null);
        response.put("rating", rating > 0 ? rating : null);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/review")
    @ResponseBody
    public ResponseEntity<?> addReview(@RequestBody CreateReviewDTO reviewDTO){
        try {
            if(player == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No player logged in");
            if(reviewDTO.getComment() != null && reviewDTO.getComment().length() > 255) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Comment too long.");
            if(reviewDTO.getRating() < 0 || reviewDTO.getRating() > 5) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid rating.");

            Comment comment = new Comment(gameName, player, reviewDTO.getComment(), new Date());
            commentService.addComment(comment);

            if(reviewDTO.getRating() != 0) {
                Rating rating = new Rating(gameName, player, reviewDTO.getRating(), new Date());
                ratingService.setRating(rating);
            }
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error saving review: " + e.getMessage());
        }

        return ResponseEntity.ok("Review saved successfully.");
    }

}
