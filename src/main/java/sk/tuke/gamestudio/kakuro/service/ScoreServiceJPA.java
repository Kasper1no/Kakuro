package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Score;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class ScoreServiceJPA implements ScoreService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addScore(Score score) throws ScoreException {
        try {
            Score existingScore = entityManager.createQuery(
                    "SELECT s FROM Score s WHERE s.game = :game AND s.player = :player", Score.class)
                    .setParameter("game", score.getGame())
                    .setParameter("player", score.getPlayer())
                    .getSingleResult();

            existingScore.setPoints(existingScore.getPoints() + score.getPoints());
            existingScore.setPlayedOn(score.getPlayedOn());
            entityManager.merge(existingScore);
        } catch (NoResultException e) {
            entityManager.persist(score);
        }
    }

    @Override
    public List<Score> getTopScores(String game) throws ScoreException {
        return entityManager.createNamedQuery("Score.getTopScores", Score.class)
                .setParameter("game", game)
                .setMaxResults(10)
                .getResultList();
    }

    @Override
    public void reset() {
        entityManager.createNamedQuery("Score.resetScores").executeUpdate();
        // alebo:
        // entityManager.createNativeQuery("delete from score").executeUpdate();
    }
}
