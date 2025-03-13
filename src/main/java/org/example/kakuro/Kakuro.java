package org.example.kakuro;


import org.example.kakuro.consoleui.ConsoleUI;
import org.example.kakuro.core.Field;

public class Kakuro {
    public static void main(String[] args) {
        ConsoleUI ui = new ConsoleUI();

        do {
            Field field = switch (ui.setDifficulty()) {
                case 1 -> new Field(3, 3);
                case 2 -> new Field(4, 4);
                case 3 -> new Field(5, 5);
                default -> new Field(3, 3);
            };

            ui.play(field);
        }while (ui.restartGame());
    }
}