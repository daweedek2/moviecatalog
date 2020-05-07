package kostka.moviecatalog.controller;

import kostka.moviecatalog.factory.LoginFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/successfulLogin")
    public String forwardBasedOnRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return LoginFactory.getRedirectUrlForRole(authentication.getAuthorities());
    }
}
