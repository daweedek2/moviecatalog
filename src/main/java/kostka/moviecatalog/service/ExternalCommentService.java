package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.MovieComments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    public List<Comment> getCommentsFromCommentService(final Long movieId) {
        LOGGER.info("Getting comments from Comment service.");
        MovieComments commentsResponse = restTemplate.getForObject(
                COMMENT_URL_SERVICE_DISCOVERY + movieId,
                MovieComments.class);
        return Objects.requireNonNull(commentsResponse).getComments();
    }
}
