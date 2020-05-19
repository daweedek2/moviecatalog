package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.CommentDto;
import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.dto.UserDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.Rating;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.service.CacheService;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.ExternalCommentService;
import kostka.moviecatalog.service.ExternalRatingService;
import kostka.moviecatalog.service.UserService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;

@Controller
@RequestMapping(value = "/admin")
public class AdministrationController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationController.class);
    public static final String ADMIN_VIEW = "admin";
    public static final String REDIRECT_ADMIN_VIEW = "redirect:/admin";
    public static final String ERROR = "status";
    public static final String INVALID_DTO = "Required fields are empty.";
    public static final String SUCCESS = "success";
    private DbMovieService dbMovieService;
    private RabbitMqSender rabbitMqSender;
    private ExternalCommentService externalCommentService;
    private ExternalRatingService externalRatingService;
    private RedisService redisService;
    private UserService userService;
    private CacheService cacheService;

    @Autowired
    public AdministrationController(final DbMovieService dbMovieService,
                                    final RabbitMqSender rabbitMqSender,
                                    final ExternalCommentService externalCommentService,
                                    final ExternalRatingService externalRatingService,
                                    final RedisService redisService,
                                    final UserService userService,
                                    final CacheService cacheService) {
        this.dbMovieService = dbMovieService;
        this.rabbitMqSender = rabbitMqSender;
        this.externalCommentService = externalCommentService;
        this.externalRatingService = externalRatingService;
        this.redisService = redisService;
        this.userService = userService;
        this.cacheService = cacheService;
    }

    @GetMapping()
    public String viewAdmin(final Model model) {
        addModelAttributes(model, "");
        return ADMIN_VIEW;
    }

    /**
     * Controller method for creating new movie in db and in elasticsearch.
     * @param dto which holds the new movie data.
     * @return admin page.
     */
    @PostMapping("/movie/create")
    public String createMovie(final @Valid @ModelAttribute MovieFormDto dto,
                              final BindingResult bindingResult,
                              final RedirectAttributes redirectAttributes,
                              final Model model) {
        LOGGER.info("create movie request");
        redisService.incrementGeneralCounterWithLockCheck();

        if (bindingResult.hasErrors()) {
            addModelAttributes(model, INVALID_DTO);
            return ADMIN_VIEW;
        }

        try {
            Movie movie = dbMovieService.createMovie(dto);
            rabbitMqSender.sendToCreateElasticQueue(movie.getId().toString());
            rabbitMqSender.sendUpdateRequestToQueue();
            rabbitMqSender.sendRefreshAdminRequestToQueue();
        } catch (InvalidDtoException e) {
            LOGGER.error("Creation of movie failed", e);
            addModelAttributes(model, "Movie cannot be created.");
            return ADMIN_VIEW;
        }

        redirectAttributes.addFlashAttribute(SUCCESS, "Movie is successfully created.");
        return REDIRECT_ADMIN_VIEW;
    }

    /**
     * Method for deleting existing movie from db and from elasticsearch.
     * @param movieId id of the deleted movie.
     */
    @GetMapping("/movies/delete/{movieId}")
    public String deleteMovie(final @PathVariable Long movieId) {
        LOGGER.info("delete movie with id '{}' request", movieId);

        try {
            dbMovieService.deleteMovie(movieId);
            rabbitMqSender.sendToDeleteElasticQueue(movieId.toString());
            rabbitMqSender.sendUpdateRequestToQueue();
            rabbitMqSender.sendRefreshAdminRequestToQueue();
        } catch (Exception e) {
            LOGGER.error("Movie with id '{}' cannot be deleted.", movieId, e);
        }
        return REDIRECT_ADMIN_VIEW;
    }

    @PostMapping("/comment/create")
    public String createComment(final @Valid @ModelAttribute CommentDto dto,
                                final BindingResult bindingResult,
                                final RedirectAttributes redirectAttributes,
                                final Model model) {
        LOGGER.info("create comment request");

        if (bindingResult.hasErrors()) {
            addModelAttributes(model, INVALID_DTO);
            return ADMIN_VIEW;
        }

        Comment createdComment = externalCommentService.createCommentInCommentService(dto);
        if (createdComment.getCommentId() == null) {
            LOGGER.info("Comment service is down.");
            addModelAttributes(model, "Comment is not created. CommentService is down.");
            return ADMIN_VIEW;
        }
        rabbitMqSender.sendRefreshMovieDetailRequestToQueue();
        rabbitMqSender.sendUpdateRequestToQueue();

        redirectAttributes.addFlashAttribute(SUCCESS, "Comment is successfully created.");
        return REDIRECT_ADMIN_VIEW;
    }

    @PostMapping("/rating/create")
    public String createRating(final @Valid @ModelAttribute RatingDto dto,
                               final BindingResult bindingResult,
                               final RedirectAttributes redirectAttributes,
                               final Model model) {
        LOGGER.info("create rating request");

        if (bindingResult.hasErrors()) {
            addModelAttributes(model, INVALID_DTO);
            return ADMIN_VIEW;
        }

        Rating createdRating = externalRatingService.createRatingInRatingService(dto);
        if (createdRating.getRatingId() == null) {
            LOGGER.info("Rating service is down.");
            addModelAttributes(model, "Rating is not created. RatingService is down.");
            return ADMIN_VIEW;
        }
        rabbitMqSender.sendToSetAverageRatingForSingleMovie(dto.getId().toString());

        redirectAttributes.addFlashAttribute(SUCCESS, "Rating is successfully created.");
        return REDIRECT_ADMIN_VIEW;
    }

    @PostMapping("user/create")
    public String createUser(final @Valid UserDto dto,
                             final BindingResult bindingResult,
                             final RedirectAttributes redirectAttributes,
                             final Model model) {
        LOGGER.info("create user request");

        if (bindingResult.hasErrors()) {
            addModelAttributes(model, INVALID_DTO);
            return ADMIN_VIEW;
        }

        try {
            userService.createUser(dto);
            redirectAttributes.addFlashAttribute(SUCCESS, "User is successfully created.");
            rabbitMqSender.sendRefreshAdminRequestToQueue();
            return REDIRECT_ADMIN_VIEW;
        } catch (Exception e) {
            LOGGER.error("Creation of user failed.", e);
            return ADMIN_VIEW;
        }
    }

    private void addModelAttributes(final Model model, final String message) {
        model.addAttribute("movieDto", new MovieFormDto());
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("ratingDto", new RatingDto());
        model.addAttribute("userDto", new UserDto());
        model.addAttribute(ALL_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        model.addAttribute(ERROR, message);
    }
}
