package kostka.commentservice.service;

import kostka.commentservice.dto.CommentDto;
import kostka.commentservice.exception.CommentNotFoundException;
import kostka.commentservice.exception.InvalidDtoException;
import kostka.commentservice.model.Comment;
import kostka.commentservice.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private CommentRepository commentRepository;

    @Autowired
    public CommentService(final CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    public Comment createComment(final CommentDto dto) {
        if (dto.getMovieId() == null) {
            throw new InvalidDtoException();
        }
        Comment comment = populateCommentFromDto(dto);
        return commentRepository.save(comment);
    }

    private Comment populateCommentFromDto(final CommentDto dto) {
        Comment comment = new Comment();
        comment.setMovieId(dto.getMovieId());
        comment.setAuthorId(dto.getAuthorId());
        comment.setCommentText(dto.getCommentText());
        return comment;
    }

    public Comment getComment(final Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElseThrow(CommentNotFoundException::new);
    }

    public List<Comment> getAllCommentsForMovie(final Long id) {
        return commentRepository.findAllByMovieId(id);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
