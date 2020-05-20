package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.dto.CommentDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.MovieComments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    private static final String COMMENT_URL_SERVICE_DISCOVERY = "http://comment-service/comments/";
    private static final String LATEST_COMMENTS_URL_SERVICE_DISCOVERY = "http://comment-service/comments/latest5";
    private static final String CREATE_COMMENT_URL_SERVICE_DISCOVERY = "http://comment-service/comments/create";
    private RestTemplate restTemplate;

    @Autowired
    public ExternalCommentService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getCommentsFromCommentServiceFallback")
    public List<Comment> getCommentsFromCommentService(final Long movieId) {
        LOGGER.info("Getting comments from Comment service.");
        MovieComments commentsResponse = restTemplate.getForObject(
                COMMENT_URL_SERVICE_DISCOVERY + movieId,
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
        return restTemplate.postForObject(CREATE_COMMENT_URL_SERVICE_DISCOVERY, dto, Comment.class);
    }

    public Comment createCommentInCommentServiceFallback(final CommentDto dto) {
        LOGGER.warn("CommentService is down, default comment is returned.");
        return new Comment();
    }

    @HystrixCommand(fallbackMethod = "getLatest5CommentsFallback")
    public List<Comment> getLatest5Comments() {
        LOGGER.info("Getting latest comments from Comment service.");
        MovieComments commentsResponse = restTemplate.getForObject(
                LATEST_COMMENTS_URL_SERVICE_DISCOVERY, MovieComments.class);

        return Objects.requireNonNull(commentsResponse).getComments();
    }

    public List<Comment> getLatest5CommentsFallback() {
        LOGGER.warn("CommentService is down, default comment is returned.");
        return Collections.singletonList(getDefaultComment(DEFAULT_ID));
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
