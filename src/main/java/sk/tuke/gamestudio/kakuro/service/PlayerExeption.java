package sk.tuke.gamestudio.kakuro.service;

public class PlayerExeption extends RuntimeException {
    public PlayerExeption(String message) {
        super(message);
    }

    public PlayerExeption(String message, Throwable cause) {
        super(message, cause);
    }
}
