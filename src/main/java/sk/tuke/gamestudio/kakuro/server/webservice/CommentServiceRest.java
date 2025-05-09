package sk.tuke.gamestudio.kakuro.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.kakuro.entity.Comment;
import sk.tuke.gamestudio.kakuro.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
public class CommentServiceRest {

    @Autowired
    private CommentService commentService;

    @GetMapping("/all/{game}")
    public List<Comment> getAllComments(@PathVariable String game) {
        return commentService.getComments(game);
    }

    @GetMapping("/game/{game}/player/{nickname}")
    public Comment getComment(@PathVariable String game ,@PathVariable String nickname) {
        return commentService.getComment(game, nickname);
    }

    @PostMapping
    public void addComment(@RequestBody Comment comment){
        commentService.addComment(comment);
    }


}
