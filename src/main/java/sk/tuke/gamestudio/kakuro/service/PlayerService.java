package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Player;

public interface PlayerService {
    Player loginPlayer(String username, String rawPassword) throws PlayerExeption;
    Player registerPlayer(String nickname, String rawPassword, String avatar) throws PlayerExeption;
}
