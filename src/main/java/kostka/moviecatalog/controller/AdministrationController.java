package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.dto.UserFormDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesConfigValue;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.service.CacheService;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.UserService;
import kostka.moviecatalog.service.ValidationService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import kostka.moviecatalog.service.redis.RedisService;
import kostka.moviecatalog.service.runtimeconfig.RuntimeConfigurationService;
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

import static kostka.moviecatalog.enums.RuntimeConfigurationEnum.VISIBLE_MOVIES;
import static kostka.moviecatalog.factory.RuntimeConfigurationFactory.getValueForUpdateRuntimeConfig;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;

@Controller
@RequestMapping(value = "/admin")
public class AdministrationController {
    public static final String ADMIN_VIEW = "admin";
    public static final String REDIRECT_ADMIN_VIEW = "redirect:/admin";
    public static final String ERROR = "status";
    public static final String INVALID_DTO = "Required fields are empty.";
    public static final String SUCCESS = "success";
    public static final String ALL_USERS_ATTR = "allUsers";
    private static final Logger LOGGER = LoggerFactory.getLogger(AdministrationController.class);
    private DbMovieService dbMovieService;
    private RabbitMqSender rabbitMqSender;
    private RedisService redisService;
    private UserService userService;
    private CacheService cacheService;
    private RuntimeConfigurationService runtimeConfigService;
    private ValidationService validationService;

    @Autowired
    public AdministrationController(final DbMovieService dbMovieService,
                                    final RabbitMqSender rabbitMqSender,
                                    final RedisService redisService,
                                    final UserService userService,
                                    final CacheService cacheService,
                                    final RuntimeConfigurationService runtimeConfigService,
                                    final ValidationService validationService) {
        this.dbMovieService = dbMovieService;
        this.rabbitMqSender = rabbitMqSender;
        this.redisService = redisService;
        this.userService = userService;
        this.cacheService = cacheService;
        this.runtimeConfigService = runtimeConfigService;
        this.validationService = validationService;
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
        redirectAttributes.addFlashAttribute(SUCCESS, "Comment is successfully created.");
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

    @PostMapping("user/create")
    public String createUser(final @Valid UserFormDto dto,
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
            rabbitMqSender.sendRefreshAdminRequestToQueue();
            redirectAttributes.addFlashAttribute(SUCCESS, "User is successfully created.");
            return REDIRECT_ADMIN_VIEW;
        } catch (Exception e) {
            LOGGER.error("Creation of user failed.", e);
            return ADMIN_VIEW;
        }
    }

    @PostMapping("config/update")
    public String updateRuntimeConfig(final @Valid RuntimeConfigDto dto,
                                      final BindingResult bindingResult,
                                      final RedirectAttributes redirectAttributes,
                                      final Model model) {
        LOGGER.info("update runtime config request");

        if (bindingResult.hasErrors()) {
            addModelAttributes(model, INVALID_DTO);
            return ADMIN_VIEW;
        }

        try {
            validationService.validateVisibleMoviesConfigValue(dto.getLimit());
            // TODO: in dto send Enum instead of the string
            runtimeConfigService.updateRuntimeConfiguration(dto.getConfigName(), getValueForUpdateRuntimeConfig(dto));
            redirectAttributes.addFlashAttribute(SUCCESS, "Runtime Config is updated.");
            rabbitMqSender.sendUpdateRequestToQueue();
            return REDIRECT_ADMIN_VIEW;
        } catch (Exception e) {
            LOGGER.error("Runtime Config update failed.", e);
            addModelAttributes(model, "Runtime Config is not updated.");
            return ADMIN_VIEW;
        }
    }

    private void addModelAttributes(final Model model, final String message) {
        model.addAttribute("movieDto", new MovieFormDto());
        model.addAttribute("userDto", new UserFormDto());
        model.addAttribute(ALL_MOVIES_KEY, cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY));
        model.addAttribute(ALL_USERS_ATTR, userService.getAllUsers());
        model.addAttribute(ERROR, message);
        try {
            model.addAttribute(
                    "visibleMoviesConfigDto",
                    new RuntimeConfigDto(
                            runtimeConfigService
                                    .getRuntimeConfigurationValue(VISIBLE_MOVIES, VisibleMoviesConfigValue.class)
                                    .getLimit(),
                            VISIBLE_MOVIES.getName()));

        } catch (Exception e) {
            LOGGER.error("Cannot set runtime config dto", e);
        }
    }
}
