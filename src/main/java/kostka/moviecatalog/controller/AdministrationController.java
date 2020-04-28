package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.CommentDto;
import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.Rating;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.ExternalCommentService;
import kostka.moviecatalog.service.ExternalRatingService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
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

    @Autowired
    public AdministrationController(final DbMovieService dbMovieService,
                                    final RabbitMqSender rabbitMqSender,
                                    final ExternalCommentService externalCommentService,
                                    final ExternalRatingService externalRatingService) {
        this.dbMovieService = dbMovieService;
        this.rabbitMqSender = rabbitMqSender;
        this.externalCommentService = externalCommentService;
        this.externalRatingService = externalRatingService;
    }

    @GetMapping()
    public String viewAdmin(final Model model) {
        addModelAttributes(model, "");
        return ADMIN_VIEW;
    }

    @PostMapping("/movie/create")
    public String createMovie(final @Valid @ModelAttribute MovieDto dto,
                              final BindingResult bindingResult,
                              final RedirectAttributes redirectAttributes,
                              final Model model) {
        LOGGER.info("create movie request");

        if (bindingResult.hasErrors()) {
            addModelAttributes(model, INVALID_DTO);
            return ADMIN_VIEW;
        }

        try {
            Movie movie = dbMovieService.createMovie(dto);
            rabbitMqSender.sendToCreateElasticQueue(movie.getId().toString());
            rabbitMqSender.sendUpdateRequestToQueue();
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
        rabbitMqSender.sendUpdateRequestToQueue();
        rabbitMqSender.sendRefreshMovieDetailRequestQueue();

        redirectAttributes.addFlashAttribute(SUCCESS, "Comment is successfully created.");
        return REDIRECT_ADMIN_VIEW;
    }

    @PostMapping("/rating/create")
    public String createRating(final @Valid @ModelAttribute RatingDto dto,
                               final BindingResult bindingResult,
                               final RedirectAttributes redirectAttributes,
                               final Model model) {
        LOGGER.info("create movie request");
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
        rabbitMqSender.sendUpdateRequestToQueue();
        rabbitMqSender.sendRefreshMovieDetailRequestQueue();

        redirectAttributes.addFlashAttribute(SUCCESS, "Rating is successfully created.");
        return REDIRECT_ADMIN_VIEW;
    }

    private void addModelAttributes(final Model model, final String message) {
        model.addAttribute("movieDto", new MovieDto());
        model.addAttribute("commentDto", new CommentDto());
        model.addAttribute("ratingDto", new RatingDto());
        model.addAttribute(ALL_MOVIES_KEY, dbMovieService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        model.addAttribute(ERROR, message);
    }
}
