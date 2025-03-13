package org.example.kakuro.consoleui;

import org.example.kakuro.core.Field;

import java.util.Scanner;

public class ConsoleUI {
    private final Scanner scanner = new Scanner(System.in);

    public void play(Field field) {
        scanner.nextLine();
        while (!field.isSolved()) {
            printDelimiter();
            field.printField();
            System.out.println("Enter your move:");
            System.out.println("➡ Format: A1 5 or 1A 5");
            System.out.println("➡ Type 'END' to quit or 'RESET' to restart.");
            System.out.print("\n📝 Your move: ");

            String input = scanner.nextLine().toUpperCase().trim();
            if (input.equals("END")) {
                loseScreen(field);
                return;
            }

            if (input.equals("RESET")) {
                System.out.println("🔄 Restarting the game...");
                field.reset();
                continue;
            }
            if (!handleInput(input)) {
                System.out.println("❌ Invalid format! Use A1 5 or 1A 5.");
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
        field.printField();
        System.out.println("🎉 Congratulations! You solved the Kakuro puzzle! 🎉");
    }

    public void printWelcomeMessage() {
        System.out.println("==============================================");
        System.out.println("       🎉 Welcome to the Kakuro Game! 🎉      ");
        System.out.println("==============================================");
        System.out.println("     🔢 A challenging logic puzzle awaits!    ");
        System.out.println("     📜 Rules: Fill the grid with numbers,    ");
        System.out.println("            following the given hints!        ");
        System.out.println("==============================================");
        System.out.println("          🚀 Good luck and have fun!          ");
        System.out.println("==============================================\n");
    }

    public int setDifficulty() {
        System.out.println("Please choose the difficulty level ");
        System.out.println("1. Easy");
        System.out.println("2. Normal");
        System.out.println("3. Hard");
        while(true) {
            System.out.print("Your choice: ");
            try {
                int level = scanner.nextInt();
                if(level < 1 || level > 3) {
                    System.out.println("Please choose one of the following levels(1 - 3): ");
                }
                else{
                    return level;
                }
            } catch (Exception ignored) {
                scanner.nextLine();
                System.out.println("Please choose one of the following levels(1 - 3): ");
            }
        }
    }

    public boolean restartGame() {
        System.out.print("Do you want to play again? (yes/no): ");
        boolean result = scanner.nextLine().trim().equalsIgnoreCase("yes");
        printDelimiter();
        return result;
    }

    private boolean handleInput(String input) {
        return (input.matches("[A-Z][0-9] [1-9]") || input.matches("[0-9][A-Z] [1-9]")
                && input.length() == 4);
    }

    private void printDelimiter() {
        System.out.println("--------------------------------------------------");
    }


    private void loseScreen(Field field) {
        System.out.println("\n🌟 Don't be discouraged! Just think a little longer next time. You got this! 💪");
        System.out.println("🔍 Here is the correct solution:\n");

        field.showSolution();
        field.printField();

    }
}

