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

    /***
     * Method which creates new comment and saves it to db via comment repository.
     * @param dto parameter with data of the new comment.
     * @return newly created comment.
     */
    public Comment createComment(final CommentDto dto) {
        if (dto.getMovieId() == null) {
            throw new InvalidDtoException();
        }
        Comment comment = populateCommentFromDto(dto);
        return commentRepository.save(comment);
    }

    /***
     * Method which takes dto and sets properties of the comment entity.
     * @param dto parameter with data of the new comment.
     * @return comment with data from dto.
     */
    private Comment populateCommentFromDto(final CommentDto dto) {
        Comment comment = new Comment();
        comment.setMovieId(dto.getMovieId());
        comment.setAuthorId(dto.getAuthorId());
        comment.setCommentText(dto.getCommentText());
        return comment;
    }

    /***
     * Method which gets comment based on the id.
     * @param id of the desired comment.
     * @return comment with the id from the db.
     */
    public Comment getComment(final Long id) {
        Optional<Comment> optionalComment = commentRepository.findById(id);
        return optionalComment.orElseThrow(CommentNotFoundException::new);
    }

    /***
     * Method which gets all comments for the specific movie based on movie id.
     * @param id movie id of the movie for which we get the comments.
     * @return list of comments from the db.
     */
    public List<Comment> getAllCommentsForMovie(final Long id) {
        return commentRepository.findAllByMovieId(id);
    }

    /***
     * Method which gets all comments stored in db.
     * @return list of all comments in db.
     */
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }
}
