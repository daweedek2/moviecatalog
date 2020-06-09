package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.dto.UserFormDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.CacheService;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.UserService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public static final String ADMIN_VIEW = "admin";
    public static final String REDIRECT_ADMIN_VIEW = "redirect:/admin";
    public static final String ERROR = "error";
    public static final String INVALID_DTO = "Required fields are empty.";
    public static final String SUCCESS = "success";
    public static final String ALL_USERS_ATTR = "allUsers";
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationController.class);
    private DbMovieService dbMovieService;
    private RabbitMqSender rabbitMqSender;
    private RedisService redisService;
    private UserService userService;
    private CacheService cacheService;

    @Autowired
    public AdministrationController(final DbMovieService dbMovieService,
                                    final RabbitMqSender rabbitMqSender,
                                    final RedisService redisService,
                                    final UserService userService,
                                    final CacheService cacheService) {
        this.dbMovieService = dbMovieService;
        this.rabbitMqSender = rabbitMqSender;
        this.redisService = redisService;
        this.userService = userService;
        this.cacheService = cacheService;
    }

    @GetMapping()
    public String viewAdmin(final Model model) {
        addModelAttributes(model);
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
            addModelAttributes(model);
            model.addAttribute(ERROR, INVALID_DTO);
            return ADMIN_VIEW;
        }

        Movie movie = dbMovieService.createMovie(dto);
        rabbitMqSender.sendToCreateElasticQueue(movie.getId().toString());
        rabbitMqSender.sendUpdateRequestToQueue();
        rabbitMqSender.sendRefreshAdminRequestToQueue();
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

        dbMovieService.deleteMovie(movieId);
        rabbitMqSender.sendToDeleteElasticQueue(movieId.toString());
        rabbitMqSender.sendUpdateRequestToQueue();
        rabbitMqSender.sendRefreshAdminRequestToQueue();
        return REDIRECT_ADMIN_VIEW;
    }

    @PostMapping("user/create")
    public String createUser(final @Valid UserFormDto dto,
                             final BindingResult bindingResult,
                             final RedirectAttributes redirectAttributes,
                             final Model model) {
        LOGGER.info("create user request");

        if (bindingResult.hasErrors()) {
            addModelAttributes(model);
            model.addAttribute(ERROR, INVALID_DTO);
            return ADMIN_VIEW;
        }

        userService.createUser(dto);
        rabbitMqSender.sendRefreshAdminRequestToQueue();
        redirectAttributes.addFlashAttribute(SUCCESS, "User is successfully created.");
        return REDIRECT_ADMIN_VIEW;
    }

    /**
     * Method for deleting existing user from db..
     * @param userId id of the deleted user.
     */
    @GetMapping("/users/delete/{userId}")
    public String deleteUser(final @PathVariable Long userId) {
        LOGGER.info("delete user with id '{}' request", userId);

        userService.deleteUser(userId);
        rabbitMqSender.sendRefreshAdminRequestToQueue();
        return REDIRECT_ADMIN_VIEW;
    }

    @GetMapping("/users/ban/{userId}")
    public String banUser(final @PathVariable Long userId) {
        LOGGER.info("ban user with id '{}' request", userId);

        userService.banUser(userId);
        rabbitMqSender.sendRefreshAdminRequestToQueue();
        return REDIRECT_ADMIN_VIEW;
    }

    @GetMapping("/users/unban/{userId}")
    public String unBanUser(final @PathVariable Long userId) {
        LOGGER.info("ban user with id '{}' request", userId);

        userService.unBanUser(userId);
        rabbitMqSender.sendRefreshAdminRequestToQueue();
        return REDIRECT_ADMIN_VIEW;
    }

    private void addModelAttributes(final Model model) {
        model.addAttribute("movieDto", new MovieFormDto());
        model.addAttribute("userDto", new UserFormDto());
        model.addAttribute(ALL_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        model.addAttribute(ALL_USERS_ATTR, userService.getAllUsers());
    }

    /**
     * In case the exception is thrown during execution controllers method, log this error and print it
     * and display in the UI.
     * @param e holds the caught exception.
     * @param redirectAttributes attributes which holds the message for displaying to UI.
     * @return redirect to admin view.
     */
    @ExceptionHandler(value = Exception.class)
    public String administratorExceptionHandler(final Exception e, RedirectAttributes redirectAttributes) {
        LOGGER.error(e.getMessage(), e);
        redirectAttributes.addFlashAttribute(ERROR, e.getMessage());
        return REDIRECT_ADMIN_VIEW;
    }
}
