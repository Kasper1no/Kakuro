package sk.tuke.gamestudio.kakuro.service;

import org.mindrot.jbcrypt.BCrypt;
import sk.tuke.gamestudio.kakuro.entity.Player;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

@Transactional
public class PlayerServiceJPA implements PlayerService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Player loginPlayer(String username, String rawPassword) throws PlayerExeption {
        Player player = entityManager.createQuery(
                        "SELECT p FROM Player p WHERE p.nickname = :nickname", Player.class)
                .setParameter("nickname", username)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (player == null) {
            throw new PlayerExeption("Player with nickname " + username + " not found");
        }

        if (!BCrypt.checkpw(rawPassword, player.getPassword())) {
            throw new PlayerExeption("Invalid password");
        }

        return player;
    }

    @Override
    public Player registerPlayer(String nickname, String rawPassword, String avatar) throws PlayerExeption {
        try {
            boolean exists = entityManager.createQuery(
                            "SELECT COUNT(p) FROM Player p WHERE p.nickname = :nickname", Long.class)
                    .setParameter("nickname", nickname)
                    .getSingleResult() > 0;

            if (exists) {
                throw new PlayerExeption("Player with nickname " + nickname + " already exists");
            }

            String hashedPassword = BCrypt.hashpw(rawPassword, BCrypt.gensalt());

            Player player = new Player();
            player.setNickname(nickname);
            player.setPassword(hashedPassword);
            player.setAvatar(avatar);

            entityManager.persist(player);
            return player;
        } catch (Exception e) {
            throw new PlayerExeption("Error registering player: " + e.getMessage(), e);
        }
    }
}
