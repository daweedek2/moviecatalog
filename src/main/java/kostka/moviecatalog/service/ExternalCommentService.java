package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.dto.CommentDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.MovieComments;
import kostka.moviecatalog.service.communication.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service which communicates with external microservice (CommentService) via rest template.
 */
@Service
public class ExternalCommentService {
    public static final long DEFAULT_ID = 999L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalCommentService.class);
    private static final String COMMENT_SERVICE_URL = "http://comment-service/comments/";
    private static final String COUNT = "count/";
    private static final String GET_LATEST5 = "latest5";
    private static final String CREATE = "create";

    private CommunicationService communicationService;

    @Autowired
    public ExternalCommentService(
            final CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @HystrixCommand(fallbackMethod = "getCommentsFromCommentServiceFallback")
    public List<Comment> getCommentsFromCommentService(final Long movieId) {
        LOGGER.info("Getting comments from Comment service.");
        MovieComments commentsResponse = communicationService.sendGetRequest(
                COMMENT_SERVICE_URL + movieId,
                MovieComments.class);
        return Objects.requireNonNull(commentsResponse).getComments();
    }

    public List<Comment> getCommentsFromCommentServiceFallback(final Long movieId) {
        LOGGER.warn("Comment service is down - return default list of comments.");
        Comment defaultComment = getDefaultComment(movieId);
        return Collections.singletonList(defaultComment);
    }

    @HystrixCommand(fallbackMethod = "createCommentInCommentServiceFallback")
    public Comment createCommentInCommentService(final CommentDto dto) {
        LOGGER.info("Create new comment request for CommentService.");
        return communicationService.sendPostRequest(COMMENT_SERVICE_URL + CREATE, dto, Comment.class);
    }

    public Comment createCommentInCommentServiceFallback(final CommentDto dto) {
        LOGGER.warn("CommentService is down, default comment is returned.");
        return new Comment();
    }

    @HystrixCommand(fallbackMethod = "getLatest5CommentsFallback")
    public List<Comment> getLatest5Comments() {
        LOGGER.info("Getting latest comments from Comment service.");
        MovieComments commentsResponse = communicationService.sendGetRequest(
                COMMENT_SERVICE_URL + GET_LATEST5, MovieComments.class);

        return Objects.requireNonNull(commentsResponse).getComments();
    }

    public List<Comment> getLatest5CommentsFallback() {
        LOGGER.warn("CommentService is down, default comment is returned.");
        return Collections.singletonList(getDefaultComment(DEFAULT_ID));
    }

    @HystrixCommand(fallbackMethod = "getCommentsByUserCountFallback")
    public int getCommentsByUserCount(final Long userId) {
        LOGGER.info("Getting count of the comments of the user with id '{}'.", userId);
        return communicationService.sendGetRequest(
                COMMENT_SERVICE_URL + COUNT + userId,
                int.class);
    }

    public int getCommentsByUserCountFallback(final Long userId) {
        LOGGER.warn("CommentService is down, default count is returned.");
        return (int) DEFAULT_ID;
    }

    private Comment getDefaultComment(final Long movieId) {
        Comment defaultComment = new Comment();
        defaultComment.setAuthorId(DEFAULT_ID);
        defaultComment.setCommentId(DEFAULT_ID);
        defaultComment.setMovieId(movieId);
        defaultComment.setCommentText("Comment Service is down.");
        return defaultComment;
    }
}
