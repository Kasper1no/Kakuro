package sk.tuke.gamestudio.kakuro.service;

import sk.tuke.gamestudio.kakuro.entity.Comment;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Transactional
public class CommentServiceJPA implements CommentService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void addComment(Comment comment) throws CommentException {
        Comment existingComment = entityManager.createQuery(
                        "SELECT c FROM Comment c WHERE c.game = :game AND c.player.nickname = :nickname", Comment.class)
                .setParameter("game", comment.getGame())
                .setParameter("nickname", comment.getPlayer().getNickname())
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (existingComment != null) {
            existingComment.setComment(comment.getComment());
            existingComment.setCommentedOn(comment.getCommentedOn());
            entityManager.merge(existingComment);
        } else {
            entityManager.persist(comment);
        }
    }


    @Override
    public List<Comment> getComments(String game) throws CommentException {
        return entityManager.createNamedQuery("Comment.getCommentsByGame", Comment.class)
                .setParameter("game", game)
                .getResultList();
    }

    @Override
    public void reset() throws CommentException {
        entityManager.createNamedQuery("Comment.resetComments").executeUpdate();
    }

    @Override
    public Comment getComment(String game, String nickname) throws CommentException {
        return entityManager.createNamedQuery("Comment.getCommentByGameAndPlayer", Comment.class)
                .setParameter("game", game)
                .setParameter("nickname", nickname)
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

}
