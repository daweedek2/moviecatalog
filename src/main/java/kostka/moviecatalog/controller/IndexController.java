package kostka.moviecatalog.controller;

import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.ExternalCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;

@Controller
@RequestMapping(value = "/")
public class IndexController {
    static final Logger LOGGER = LoggerFactory.getLogger(IndexController.class);
    private static final String LATEST_COMMENTS_KEY = "latestComments";
    private DbMovieService dbMovieService;
    private ExternalCommentService externalCommentService;

    @Autowired
    public IndexController(final DbMovieService dbMovieService,
                           final ExternalCommentService externalCommentService) {
        this.dbMovieService = dbMovieService;
        this.externalCommentService = externalCommentService;
    }

    /**
     * Controller method for displaying index page with the lists of the movies.
     * @return name of the html file (index.html).
     */
    @GetMapping
    public String index(final Model model) {
        LOGGER.info("rendering index page");
        model.addAttribute(TOP_RATING_KEY, dbMovieService.getMoviesFromCacheWithKey(TOP_RATING_KEY));
        model.addAttribute(LATEST_MOVIES_KEY, dbMovieService.getMoviesFromCacheWithKey(LATEST_MOVIES_KEY));
        model.addAttribute(ALL_MOVIES_KEY, dbMovieService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        model.addAttribute(LATEST_COMMENTS_KEY, externalCommentService.getLatest5Comments());
        return "index";
    }
}
