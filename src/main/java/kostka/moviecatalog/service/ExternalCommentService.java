package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.dto.CommentDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.MovieComments;
import kostka.moviecatalog.service.communication.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
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
    private static final String MOVIE_COMMENTS_KEY_PREFIX = "movieComments";
    private static final String USER_COMMENTS_KEY_PREFIX = "userComments";
    private static final String LATEST_COMMENTS_KEY = "latestComments";

    private CommunicationService communicationService;
    private CacheService cacheService;
    private JsonConvertService jsonConvertService;

    @Autowired
    public ExternalCommentService(
            final CommunicationService communicationService,
            final CacheService cacheService,
            final JsonConvertService jsonConvertService) {
        this.communicationService = communicationService;
        this.cacheService = cacheService;
        this.jsonConvertService = jsonConvertService;
    }

    @HystrixCommand(fallbackMethod = "getCommentsFromCommentServiceFallback")
    public List<Comment> getCommentsFromCommentService(final Long movieId) throws JsonProcessingException {
        LOGGER.info("Getting comments from Comment service.");
        MovieComments commentsResponse = communicationService.sendGetRequest(
                COMMENT_SERVICE_URL + movieId,
                MovieComments.class);

        List<Comment> movieComments = Objects.requireNonNull(commentsResponse).getComments();
        cacheService.cacheData(getKey(MOVIE_COMMENTS_KEY_PREFIX, movieId), movieComments);
        return movieComments;
    }

    public List<Comment> getCommentsFromCommentServiceFallback(final Long movieId) throws JsonProcessingException {
        LOGGER.warn("Comment service is down - return comments from cache.");
        String jsonData = cacheService.getCachedDataJsonWithKey(getKey(MOVIE_COMMENTS_KEY_PREFIX, movieId));
        if (jsonData == null) {
            Collections.emptyList();
        }
        return Arrays.asList(jsonConvertService.jsonToData(jsonData, Comment[].class));
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
    public List<Comment> getLatest5Comments() throws JsonProcessingException {
        LOGGER.info("Getting latest comments from Comment service.");
        MovieComments commentsResponse = communicationService.sendGetRequest(
                COMMENT_SERVICE_URL + GET_LATEST5, MovieComments.class);

        List<Comment> latestComments = Objects.requireNonNull(commentsResponse).getComments();
        cacheService.cacheData(LATEST_COMMENTS_KEY, latestComments);
        return latestComments;
    }

    public List<Comment> getLatest5CommentsFallback() throws JsonProcessingException {
        LOGGER.warn("CommentService is down, comments from cache are returned.");
        String jsonData = cacheService.getCachedDataJsonWithKey(LATEST_COMMENTS_KEY);
        if (jsonData == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(jsonConvertService.jsonToData(jsonData, Comment[].class));
    }

    @HystrixCommand(fallbackMethod = "getCommentsByUserCountFallback")
    public int getCommentsByUserCount(final Long userId) throws JsonProcessingException {
        LOGGER.info("Getting count of the comments of the user with id '{}'.", userId);
        int userCommentsCount = communicationService.sendGetRequest(
                COMMENT_SERVICE_URL + COUNT + userId,
                int.class);
        cacheService.cacheData(getKey(USER_COMMENTS_KEY_PREFIX, userId), userCommentsCount);
        return userCommentsCount;
    }

    public int getCommentsByUserCountFallback(final Long userId) throws JsonProcessingException {
        LOGGER.warn("CommentService is down, zero count is returned.");
        String jsonData = cacheService.getCachedDataJsonWithKey(getKey(USER_COMMENTS_KEY_PREFIX, userId));
        if (jsonData == null) {
            return 0;
        }
        return jsonConvertService.jsonToData(jsonData, int.class);
    }

    public static String getKey(final String prefix, final Long id) {
        return prefix + "-" + id;
    }
}
