package kostka.moviecatalog.factory;

import java.util.Collection;

public final class LoginFactory {

    private LoginFactory() {
        throw new IllegalStateException();
    }

    public static String getRedirectUrlForRole(final Collection role) {
        switch (role.toString()) {
            case "[ROLE_ADMIN]":
                return "redirect:/admin";
            case "[ROLE_USER]":
                return "redirect:/";
            default:
                return "redirect:/logout";
        }
    }
}
