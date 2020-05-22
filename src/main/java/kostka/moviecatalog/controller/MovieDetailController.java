package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.CommentDto;
import kostka.moviecatalog.dto.OrderDto;
import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Order;
import kostka.moviecatalog.entity.Rating;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.security.CustomUserDetails;
import kostka.moviecatalog.service.ExternalRatingService;
import kostka.moviecatalog.service.ExternalCommentService;
import kostka.moviecatalog.service.ExternalShopService;
import kostka.moviecatalog.service.MovieDetailService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

import static kostka.moviecatalog.controller.AdministrationController.INVALID_DTO;
import static kostka.moviecatalog.service.UserService.isUserAdultCheck;

@Controller
@RequestMapping("/movies/detail")
public class MovieDetailController {
    public static final String MOVIE_DETAIL_VIEW = "detail";
    public static final String REDIRECT_MOVIE_DETAIL_VIEW = "redirect:/movies/detail/";
    public static final String MOVIE_DETAIL_ATTR = "movieDetail";
    public static final String IS_USER_ADULT_ATTR = "isUserAdult";
    public static final String STATUS_ATTR = "status";
    public static final String COMMENT_DTO_ATTR = "commentDto";
    public static final String RATING_DTO_ATTR = "ratingDto";

    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDetailController.class);

    private final MovieDetailService movieDetailService;
    private final ExternalShopService externalShopService;
    private final ExternalCommentService externalCommentService;
    private final RabbitMqSender rabbitMqSender;
    private final ExternalRatingService externalRatingService;

    @Autowired
    public MovieDetailController(
            final MovieDetailService movieDetailService,
            final ExternalShopService externalShopService,
            final ExternalCommentService externalCommentService,
            final ExternalRatingService externalRatingService,
            final RabbitMqSender rabbitMqSender) {
        this.movieDetailService = movieDetailService;
        this.externalShopService = externalShopService;
        this.externalCommentService = externalCommentService;
        this.rabbitMqSender = rabbitMqSender;
        this.externalRatingService = externalRatingService;
    }

    /**
     * Method for displaying movie detail page.
     * @param movieId id of the movie.
     * @param model MVC model.
     * @return name of the html file (detail.html).
     */
    @GetMapping("/{movieId}")
    public String getMovieDetail(
            final @PathVariable Long movieId,
            final @AuthenticationPrincipal CustomUserDetails user,
            final Model model) {
        addMovieDetailModelAttributes(movieId, user.getUser(), model, "");
        return MOVIE_DETAIL_VIEW;
    }

    @PostMapping("/buy")
    public String buyMovie(
            final @RequestParam("movieId") Long movieId,
            final @AuthenticationPrincipal CustomUserDetails user,
            final Model model) {
        User currentUser = user.getUser();
        OrderDto dto = new OrderDto();
        dto.setMovieId(movieId);
        dto.setUserId(user.getUserId());
        Order order = externalShopService.buyMovieInShopService(dto);

        if (order == null) {
            addMovieDetailModelAttributes(movieId, currentUser, model, "Cannot buy movie.");
            return MOVIE_DETAIL_VIEW;
        }

        if (order.getId() == null) {
            addMovieDetailModelAttributes(movieId, currentUser, model,
                    "Shop service is down. Movie is not bought");
            return MOVIE_DETAIL_VIEW;
        }

        addMovieDetailModelAttributes(movieId, currentUser, model, "Movie is successfully bought.");
        return REDIRECT_MOVIE_DETAIL_VIEW + movieId;
    }

    @PostMapping("/rating/create")
    public String createRating(final @Valid @ModelAttribute RatingDto dto,
                               final BindingResult bindingResult,
                               final @AuthenticationPrincipal CustomUserDetails user,
                               final RedirectAttributes redirectAttributes,
                               final Model model) {
        LOGGER.info("create rating request");
        User currentUser = user.getUser();
        dto.setAuthorId(user.getUserId());

        if (bindingResult.hasErrors()) {
            addMovieDetailModelAttributes(dto.getId(), currentUser, model, INVALID_DTO);
            return MOVIE_DETAIL_VIEW;
        }

        Rating createdRating = externalRatingService.createRatingInRatingService(dto);
        if (createdRating.getRatingId() == null) {
            LOGGER.info("Rating service is down.");
            addMovieDetailModelAttributes(dto.getId(), currentUser, model,
                    "Rating is not created. RatingService is down.");
            return MOVIE_DETAIL_VIEW;
        }

        rabbitMqSender.sendToSetAverageRatingForSingleMovie(dto.getId().toString());
        rabbitMqSender.sendRefreshMovieDetailRequestToQueue();
        redirectAttributes.addFlashAttribute(STATUS_ATTR, "Rating is successfully created.");
        return REDIRECT_MOVIE_DETAIL_VIEW + dto.getId();
    }

    @PostMapping("/comment/create")
    public String createComment(final @Valid @ModelAttribute CommentDto dto,
                                final BindingResult bindingResult,
                                final @AuthenticationPrincipal CustomUserDetails user,
                                final RedirectAttributes redirectAttributes,
                                final Model model) {
        LOGGER.info("create comment request");
        User currentUser = user.getUser();
        dto.setAuthorId(user.getUserId());

        if (bindingResult.hasErrors()) {
            addMovieDetailModelAttributes(dto.getMovieId(), currentUser, model, INVALID_DTO);
            return MOVIE_DETAIL_VIEW;
        }

        Comment createdComment = externalCommentService.createCommentInCommentService(dto);
        if (createdComment.getCommentId() == null) {
            LOGGER.info("Comment service is down.");
            addMovieDetailModelAttributes(dto.getMovieId(), currentUser, model,
                    "Comment is not created. CommentService is down.");
            return MOVIE_DETAIL_VIEW;
        }

        rabbitMqSender.sendRefreshMovieDetailRequestToQueue();
        rabbitMqSender.sendUpdateRequestToQueue();
        redirectAttributes.addFlashAttribute(STATUS_ATTR, "Comment is successfully created.");
        return REDIRECT_MOVIE_DETAIL_VIEW + dto.getMovieId();
    }

    private void addMovieDetailModelAttributes(
            final Long movieId,
            final User user,
            final Model model,
            final String status) {
        model.addAttribute(STATUS_ATTR, status);
        model.addAttribute(MOVIE_DETAIL_ATTR, movieDetailService.getMovieDetail(movieId, user.getUserId()));
        model.addAttribute(IS_USER_ADULT_ATTR, isUserAdultCheck(user));
        model.addAttribute(COMMENT_DTO_ATTR, new CommentDto());
        model.addAttribute(RATING_DTO_ATTR, new RatingDto());
    }
}
