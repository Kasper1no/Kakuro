package sk.tuke.gamestudio.kakuro.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import sk.tuke.gamestudio.kakuro.entity.DTO.ReviewDTO;
import sk.tuke.gamestudio.kakuro.entity.DTO.ScoreDTO;
import sk.tuke.gamestudio.kakuro.service.CommentService;
import sk.tuke.gamestudio.kakuro.service.RatingService;
import sk.tuke.gamestudio.kakuro.service.ScoreService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Controller
public class PageController {

    @Autowired
    private String gameName;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private RatingService ratingService;

    @GetMapping("/")
    public String homePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @GetMapping("/game")
    public String gamePage() {
        return "level";
    }

    @GetMapping("/rules")
    public String aboutPage() {
        return "rules";
    }

    @GetMapping("/leaderboard")
    public String leaderboardPage(Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);

        List<ScoreDTO> scoreViews = scoreService.getTopScores(gameName).stream()
                .map(score -> {
                    LocalDate playedOnLocal = score.getPlayedOn()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();

                    return new ScoreDTO(
                            score.getPlayer(),
                            score.getPoints(),
                            playedOnLocal.format(formatter)
                    );
                })
                .toList();

        model.addAttribute("scores", scoreViews);
        return "leaderboard";
    }

    @GetMapping("/review")
    public String reviewPage(Model model) {
        List<ReviewDTO> reviews = commentService.getComments(gameName).stream()
                .map(comment -> {
                    LocalDateTime commentedOn = comment.getCommentedOn()
                            .toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime();

                    return new ReviewDTO(
                            comment.getPlayer(),
                            comment.getComment(),
                            ratingService.getRating(gameName, comment.getPlayer().getNickname()),
                            formatRelativeDate(commentedOn)
                    );
                }).toList();

        double averageRating = ratingService.getAverageRating(gameName);
        Map<Integer, String> stars = getRatingStars(averageRating);
        averageRating = Math.round(averageRating * 10.0) / 10.0;

        int count = ratingService.getRatingCount(gameName);

        model.addAttribute("reviews", reviews);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("stars", stars);
        model.addAttribute("count",count);
        return "review";
    }

    public static String formatRelativeDate(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        long days = duration.toDays();

        if (days >= 30) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            return dateTime.format(formatter);
        }

        if (days > 0) {
            return days + (days == 1 ? " day ago" : " days ago");
        }

        long hours = duration.toHours();
        if (hours > 0) {
            return hours + (hours == 1 ? " hour ago" : " hours ago");
        }

        long minutes = duration.toMinutes();
        if (minutes > 0) {
            return minutes + (minutes == 1 ? " minute ago" : " minutes ago");
        }

        return "Just now";
    }

    public static Map<Integer, String> getRatingStars(double averageRating) {
        Map<Integer, String> stars = new LinkedHashMap<>();
        int filledStars = (int) Math.floor(averageRating);
        boolean hasHalfStar = true;

        for (int i = 1; i <= 5; i++) {
            if (i <= filledStars) {
                stars.put(i, "filled");
            } else if (i == filledStars + 1 && hasHalfStar) {
                stars.put(i, "half-filled");
            } else {
                stars.put(i, "empty");
            }
        }
        return stars;
    }



}
