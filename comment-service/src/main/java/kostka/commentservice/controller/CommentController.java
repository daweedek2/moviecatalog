package kostka.commentservice.controller;

import kostka.commentservice.dto.CommentDto;
import kostka.commentservice.model.Comment;
import kostka.commentservice.model.MovieComments;
import kostka.commentservice.service.CommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    private CommentService commentService;
    private static final Logger LOGGER = LoggerFactory.getLogger(CommentController.class);

    @Autowired
    public CommentController(final CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/create")
    public Comment createComment(@RequestBody final CommentDto dto) {
        return commentService.createComment(dto);
    }

    @GetMapping("/{movieId}")
    public MovieComments getCommentsForMovie(@PathVariable final Long movieId) {
        LOGGER.info("getting comments for movie with id {}", movieId);
        MovieComments movieComments = new MovieComments();
        movieComments.setComments(commentService.getAllCommentsForMovie(movieId));
        return movieComments;
    }

    @GetMapping("/all")
    public List<Comment> getAllComments() {
        return commentService.getAllComments();
    }
}
