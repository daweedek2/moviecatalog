package kostka.moviecatalog.controller;

import kostka.moviecatalog.security.CustomUserDetails;
import kostka.moviecatalog.service.UserDetailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
            final Model model) {
        LOGGER.info("view user detail page request");
        model.addAttribute(USER_DETAIL_ATTR, userDetailService.getUserDetailDto(userId));
        return USER_DETAIL;
    }
}
