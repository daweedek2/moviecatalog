package kostka.moviecatalog.controller;

import kostka.moviecatalog.service.CacheService;
import kostka.moviecatalog.service.ExternalCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Collections;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;

@Controller
@RequestMapping(value = "/")
public class UserController {
    static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String LATEST_COMMENTS_KEY = "latestComments";
    private static final String MY_MOVIES_KEY = "myMovies";
    private ExternalCommentService externalCommentService;
    private CacheService cacheService;

    @Autowired
    public UserController(final ExternalCommentService externalCommentService,
                          final CacheService cacheService) {
        this.externalCommentService = externalCommentService;
        this.cacheService = cacheService;
    }

    /**
     * Controller method for displaying index page with the lists of the movies.
     * @return name of the html file (user.html).
     */
    @GetMapping
    public String getUserHomePage(final Model model) {
        LOGGER.info("rendering user home page");
        model.addAttribute(TOP_RATING_KEY, cacheService.getMoviesFromCacheWithKey(TOP_RATING_KEY));
        model.addAttribute(LATEST_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(LATEST_MOVIES_KEY));
        //TODO: adapt my movies when my movies feature is implemented
        model.addAttribute(MY_MOVIES_KEY, Collections.emptyList());
        model.addAttribute(LATEST_COMMENTS_KEY, externalCommentService.getLatest5Comments());
        return "user";
    }

    @GetMapping("allMovies")
    public String getAllMoviesPage(final Model model) {
        LOGGER.info("rendering all movies page");
        model.addAttribute(ALL_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        return "allMovies";
    }
}
