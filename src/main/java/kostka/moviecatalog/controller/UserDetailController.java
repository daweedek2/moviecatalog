package kostka.moviecatalog.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.security.CustomUserDetails;
import kostka.moviecatalog.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static kostka.moviecatalog.controller.MovieDetailController.REDIRECT_ALL_MOVIES;
import static kostka.moviecatalog.controller.MovieDetailController.STATUS_ATTR;

@Controller
@RequestMapping("/users/detail")
public class UserDetailController {
    private static final String USER_DETAIL = "userDetail";
    private static final String USER_DETAIL_ATTR = "userDetailDto";

    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailController.class);

    private final UserDetailService userDetailService;

    @Autowired
    public UserDetailController(final UserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    @GetMapping("/{userId}")
    public String getUserDetail(
            final @PathVariable Long userId,
            final @AuthenticationPrincipal CustomUserDetails user,
            final Model model) throws JsonProcessingException {
        LOGGER.info("view user detail page request");
        model.addAttribute(USER_DETAIL_ATTR, userDetailService.getUserDetailDto(userId));
        return USER_DETAIL;
    }

    @ExceptionHandler(value = Exception.class)
    public String userDetailExceptionHandler(final Exception e, final RedirectAttributes redirectAttributes) {
        LOGGER.error(e.getMessage(), e);
        redirectAttributes.addFlashAttribute(STATUS_ATTR, e.getMessage());
        return REDIRECT_ALL_MOVIES;
    }
}
