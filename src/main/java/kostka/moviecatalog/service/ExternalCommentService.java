package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
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

@Service
public class ExternalCommentService {
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalCommentService.class);
    private static final String COMMENT_URL_SERVICE_DISCOVERY = "http://comment-service/comments/";

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
        LOGGER.info("Comment service is down - return default list of comments.");
        Comment defaultComment = new Comment();
        defaultComment.setAuthorId(999L);
        defaultComment.setCommentId(999L);
        defaultComment.setMovieId(movieId);
        defaultComment.setCommentText("Comment Service is down.");
        return Collections.singletonList(defaultComment);
    }
}
