package sk.tuke.gamestudio.kakuro.consoleui;

import sk.tuke.gamestudio.kakuro.core.Field;
import sk.tuke.gamestudio.kakuro.entity.Comment;
import sk.tuke.gamestudio.kakuro.entity.Rating;
import sk.tuke.gamestudio.kakuro.entity.Score;
import sk.tuke.gamestudio.kakuro.service.*;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);
    private final ScoreServiceJDBC scoreService = new ScoreServiceJDBC();
    private final CommentServiceJDBC commentService = new CommentServiceJDBC();
    private final RatingServiceJDBC ratingService = new RatingServiceJDBC();

    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String PURPLE = "\u001B[35m";
    public static final String CYAN = "\u001B[36m";
    public static final String RESET = "\u001B[0m";

    private String player;
    private Field field;
    private int multiplier;

    public void startGame() {
        printWelcomeMessage();
        do {
            field = switch (setDifficulty()) {
                case 1 -> {
                    multiplier = 1;
                    yield new Field(3, 3);
                }
                case 2 -> {
                    multiplier = 2;
                    yield new Field(4, 4);
                }
                case 3 -> {
                    multiplier = 3;
                    yield new Field(5, 5);
                }
                default -> {
                    multiplier = 1;
                    yield new Field(3, 3);
                }
            };

            play();
        } while (restartGame());
    }

    private void play() {
        scanner.nextLine();
        while (!field.isSolved()) {
            printDelimiter();

            System.out.println("🟣" + PURPLE + "Purple: Sum in row/column" + RESET);
            System.out.println("🟡" + YELLOW + "Yellow: Value in the cell" + RESET);
            System.out.println("🔴" + RED + "Red: Cell number in the grid" + RESET);

            field.printField();
            System.out.println("Enter your move:");
            System.out.println("➡ Format: " + RED + "A1" + YELLOW + " 5" + RESET + " or " + RED + "1A" + YELLOW + " 5" + RESET);
            System.out.println("➡ Type '" + GREEN + "END" + RESET + "' to quit or '" + GREEN + "RESET" + RESET + "' to restart.");
            System.out.print("\n📝 Your move: ");

            String input = scanner.nextLine().toUpperCase().trim();
            if (input.equals("END")) {
                endScreen(false);
                return;
            }

            if (input.equals("RESET")) {
                System.out.println("🔄 Restarting the game...");
                field.reset();
                continue;
            }
            if (!handleInput(input)) {
                System.out.println("❌ Invalid format! Use " + RED + "A1" + YELLOW + " 5" + RESET + " or " + RED + "1A" + YELLOW + " 5" + RESET);
                continue;
            }

            int row, column, value;
            if (Character.isLetter(input.charAt(0))) {
                column = input.charAt(0) - 'A';
                row = Character.getNumericValue(input.charAt(1)) - 1;
            } else {
                row = Character.getNumericValue(input.charAt(0)) - 1;
                column = input.charAt(1) - 'A';
            }
            column++;
            row++;
            value = Character.getNumericValue(input.charAt(3));

            if (!field.setValue(row, column, value)) {
                System.out.println("❌ Invalid tile! Choose another one.");
            }
        }
        endScreen(true);
    }

    private void printWelcomeMessage() {
        System.out.println("==============================================");
        System.out.println("       🎉 Welcome to the Kakuro Game! 🎉      ");
        System.out.println("==============================================");
        System.out.println("     🔢 A challenging logic puzzle awaits!    ");
        System.out.println("     📜 Rules: Fill the grid with numbers,    ");
        System.out.println("            following the given hints!        ");
        System.out.println("==============================================");
        System.out.println("          🚀 Good luck and have fun!          ");
        System.out.println("==============================================");

        getNickname();
        System.out.println("Hello, " + player + "! Let's play Kakuro!\n");

        viewLeaderboard("Kakuro");

        System.out.println("==============================================\n");
    }

    private void endScreen(boolean win) {
        int score = 0;

        if (win) {
            score = 10 * multiplier;
            field.printField();
            System.out.println("🎉 Congratulations! You solved the Kakuro puzzle! 🎉");
        } else {
            loseScreen();
            System.out.println("💔 You lost the game. Better luck next time! 💔");
        }

        System.out.println("\nYour score: " + YELLOW + score + " points" + RESET);

        addScore(score);

        showMenu("Kakuro");
    }

    private int setDifficulty() {
        System.out.println("Please choose the difficulty level ");
        System.out.println("1. Easy");
        System.out.println("2. Normal");
        System.out.println("3. Hard");
        while (true) {
            System.out.print("Your choice: ");
            try {
                int level = scanner.nextInt();
                if (level < 1 || level > 3) {
                    System.out.println("Please choose one of the following levels(1 - 3): ");
                } else {
                    return level;
                }
            } catch (Exception ignored) {
                scanner.nextLine();
                System.out.println("Please choose one of the following levels(1 - 3): ");
            }
        }
    }

    private boolean restartGame() {
        System.out.print("Do you want to play again? (yes/no): ");
        boolean result = scanner.nextLine().trim().equalsIgnoreCase("yes");
        printDelimiter();
        return result;
    }

    private boolean handleInput(String input) {
        return (input.matches("[A-Z][0-9] [1-9]") || input.matches("[0-9][A-Z] [1-9]")
                && input.length() == 4);
    }

    private void getNickname() {
        System.out.print(GREEN + "Please enter your name: " + RESET);
        player = scanner.nextLine();
    }

    private void addComment(String game) {
        System.out.print("Enter your comment: ");
        String commentText = scanner.nextLine();
        Comment comment = new Comment(game, player, commentText, new Date());
        try {
            commentService.addComment(comment);
            System.out.println("Your comment has been added!");
        } catch (CommentException e) {
            System.out.println("Error adding comment: " + e.getMessage());
        }
    }

    private void addRating(String game) {
        int ratingValue;
        do {
            System.out.print("Enter your rating (1-5): ");
            ratingValue = scanner.nextInt();
            scanner.nextLine();

            if (ratingValue < 1 || ratingValue > 5) {
                System.out.println("Invalid rating. Please enter a value between 1 and 5.");
            }

        } while (ratingValue < 1 || ratingValue > 5);

        Rating rating = new Rating(game, player, ratingValue, new Date());
        try {
            ratingService.setRating(rating);
            System.out.println("Your rating has been added!");
        } catch (RatingException e) {
            System.out.println("Error adding rating: " + e.getMessage());
        }
    }

    private void addScore(int score) {
        try {
            Score scoreEntry = new Score("Kakuro", player, score, new Date());
            scoreService.addScore(scoreEntry);

            viewLeaderboard("Kakuro");

        } catch (ScoreException e) {
            System.out.println("Error saving score: " + e.getMessage());
        }
    }

    private void viewComments(String game) {
        try {
            List<Comment> comments = commentService.getComments(game);
            if (comments.isEmpty()) {
                System.out.println("No comments yet for this game.");
            } else {
                System.out.println(GREEN + "Comments for " + game + ":" + RESET);
                for (Comment comment : comments) {
                    System.out.println(PURPLE + comment.getPlayer() + RESET + " said: " + YELLOW + "'" + comment.getComment() + "'" + RESET);
                    System.out.println("Commented on: " + comment.getCommentedOn());
                    System.out.println("----------------------------------");
                }
            }
        } catch (CommentException e) {
            System.out.println("Error retrieving comments: " + e.getMessage());
        }
    }

    private void viewAverageRating(String game) {
        try {
            double averageRating = ratingService.getAverageRating(game);
            System.out.println(GREEN + "Average rating for " + game + ": " + RESET + CYAN + averageRating + RESET);
        } catch (RatingException e) {
            System.out.println(RED + "Error retrieving average rating: " + e.getMessage() + RESET);
        }
    }

    private void viewLeaderboard(String game) {
        System.out.println(YELLOW + "\n🏆 Leaderboard 🏆:" + RESET);
        try {
            List<Score> topScores = scoreService.getTopScores(game);
            if (topScores.isEmpty()) {
                System.out.println("No scores available yet.");
            } else {
                System.out.println("\nTop Scores:");
                for (int i = 0; i < topScores.size(); i++) {
                    Score score = topScores.get(i);
                    System.out.println(CYAN + (i + 1) + ". " + RESET + PURPLE + score.getPlayer() + RESET + " - " + YELLOW + score.getPoints() + " points" + RESET);
                }
            }
        } catch (ScoreException e) {
            System.out.println("Error retrieving top scores: " + e.getMessage());
        }
    }

    private void resetData() {
        System.out.print("Enter password to reset data: ");
        String password = scanner.nextLine();
        if ("admin".equals(password)) {
            try {
                commentService.reset();
                ratingService.reset();
                scoreService.reset();
                System.out.println("Data has been reset!");
            } catch (CommentException | RatingException e) {
                System.out.println(RED + "Error resetting data: " + e.getMessage() + RESET);
            }
        } else {
            System.out.println(RED + "Incorrect password. Data not reset." + RESET);
        }
    }

    private void viewMyRating(String game) {
        try {
            int userRating = ratingService.getRating(game, player);
            if (userRating != 0) {
                System.out.println("Your rating for the game " + game + " is: " + YELLOW + userRating + RESET);
            } else {
                System.out.println("You haven't rated this game yet.");
            }
        } catch (RatingException e) {
            System.out.println(RED + "Error retrieving your rating: " + e.getMessage() + RESET);
        }
    }

    private void showMenu(String game) {
        while (true) {
            System.out.println("\nChoose an option:");
            System.out.println("1. Add Comment");
            System.out.println("2. Add Rating");
            System.out.println("3. View Comments");
            System.out.println("4. View Average Rating");
            System.out.println("5. View My Rating");
            System.out.println("6. View Leaderboard");
            System.out.println("7. Reset Data (Admin)");
            System.out.println("8. Exit Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            System.out.println();

            switch (choice) {
                case 1:
                    addComment(game);
                    break;
                case 2:
                    addRating(game);
                    break;
                case 3:
                    viewComments(game);
                    break;
                case 4:
                    viewAverageRating(game);
                    break;
                case 5:
                    viewMyRating(game);
                    break;
                case 6:
                    viewLeaderboard(game);
                    break;
                case 7:
                    resetData();
                    break;
                case 8:
                    System.out.println("Exiting the menu...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private void printDelimiter() {
        System.out.println("--------------------------------------------------");
    }

    private void loseScreen() {
        System.out.println("\n🌟 Don't be discouraged! Just think a little longer next time. You got this! 💪");
        System.out.println("🔍 Here is the correct solution:\n");

        field.showSolution();
        field.printField();
    }
}

