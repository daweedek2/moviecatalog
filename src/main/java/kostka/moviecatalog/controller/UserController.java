package kostka.moviecatalog.controller;

import kostka.moviecatalog.exception.NoMoviesInDbException;
import kostka.moviecatalog.security.CustomUserDetails;
import kostka.moviecatalog.service.CacheService;
import kostka.moviecatalog.service.ExternalCommentService;
import kostka.moviecatalog.service.ExternalShopService;
import kostka.moviecatalog.service.RandomMovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static kostka.moviecatalog.controller.MovieDetailController.REDIRECT_MOVIE_DETAIL_VIEW;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;

@Controller
@RequestMapping(value = "/")
public class UserController {
    public static final String REDIRECT_TO_ALL_MOVIES = "redirect:/allMovies";
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    private static final String LATEST_COMMENTS_KEY = "latestComments";
    private static final String MY_MOVIES_KEY = "myMovies";
    private ExternalCommentService externalCommentService;
    private ExternalShopService externalShopService;
    private CacheService cacheService;
    private RandomMovieService randomMovieService;

    @Autowired
    public UserController(final ExternalCommentService externalCommentService,
                          final CacheService cacheService,
                          final ExternalShopService externalShopService,
                          final RandomMovieService randomMovieService) {
        this.externalCommentService = externalCommentService;
        this.cacheService = cacheService;
        this.externalShopService = externalShopService;
        this.randomMovieService = randomMovieService;
    }

    /**
     * Controller method for displaying index page with the lists of the movies.
     * @return name of the html file (user.html).
     */
    @GetMapping
    public String getUserHomePage(
            final @AuthenticationPrincipal CustomUserDetails user,
            final Model model) {
        LOGGER.info("rendering user home page");
        model.addAttribute(TOP_RATING_KEY, cacheService.getMoviesFromCacheWithKey(TOP_RATING_KEY));
        model.addAttribute(LATEST_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(LATEST_MOVIES_KEY));
        model.addAttribute(MY_MOVIES_KEY, externalShopService.getAlreadyBoughtMoviesForUser(user.getUserId()));
        model.addAttribute(LATEST_COMMENTS_KEY, externalCommentService.getLatest5Comments());
        return "user";
    }

    @GetMapping("allMovies")
    public String getAllMoviesPage(final Model model) {
        LOGGER.info("rendering all movies page");
        model.addAttribute(ALL_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        return "allMovies";
    }

    @GetMapping("randomMovie")
    public String getRandomMovieDetail(final Model model) {
        LOGGER.info("getting random movie detail page");
        try {
           Long randomMovieId = randomMovieService.getRandomMovieIdFromAllMovies();
           return REDIRECT_MOVIE_DETAIL_VIEW + randomMovieId;
        } catch (NoMoviesInDbException e) {
            LOGGER.error("Cannot generate Random movie", e);
            return REDIRECT_TO_ALL_MOVIES;
        }
    }
}
