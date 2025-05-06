package sk.tuke.gamestudio.kakuro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.kakuro.entity.Comment;

import java.util.List;

public class CommentServiceRestClient implements CommentService {
    private final String url = "http://localhost:8080/api/comment";

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addComment(Comment comment) throws CommentException {
        restTemplate.postForObject(url, comment, Comment.class);
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        return List.of(restTemplate.getForObject(url + "/all/" + game, Comment[].class));
    }

    @Override
    public Comment getComment(String game, String nickname) throws CommentException {
        return restTemplate.getForObject(url + "/game/" + game + "/player/" + nickname, Comment.class);
    }

    @Override
    public void reset() throws CommentException {
        throw new UnsupportedOperationException("Not supported via web service");
    }
}
