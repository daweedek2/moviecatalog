package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.OrderDto;
import kostka.moviecatalog.entity.Order;
import kostka.moviecatalog.security.CustomUserDetails;
import kostka.moviecatalog.service.ExternalShopService;
import kostka.moviecatalog.service.MovieDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/movies/detail")
public class MovieDetailController {
    private final MovieDetailService movieDetailService;
    private final ExternalShopService externalShopService;
    public static final String STATUS_ATTR = "status";
    private static final String MOVIE_DETAIL_VIEW = "detail";
    private static final String REDIRECT_MOVIE_DETAIL_VIEW = "redirect:/movies/detail/";

    @Autowired
    public MovieDetailController(
            final MovieDetailService movieDetailService,
            final ExternalShopService externalShopService) {
        this.movieDetailService = movieDetailService;
        this.externalShopService = externalShopService;
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
        addMovieDetailModelAttributes(movieId, user.getUserId(), model, "");
        return MOVIE_DETAIL_VIEW;
    }

    @PostMapping("/buy")
    public String buyMovie(
            final @RequestParam("movieId") Long movieId,
            final @AuthenticationPrincipal CustomUserDetails user,
            final Model model) {
        Long userId = user.getUserId();
        OrderDto dto = new OrderDto();
        dto.setMovieId(movieId);
        dto.setUserId(userId);
        Order order = externalShopService.buyMovieInShopService(dto);

        if (order == null) {
            addMovieDetailModelAttributes(movieId, userId, model, "Cannot buy movie.");
            return MOVIE_DETAIL_VIEW;
        }

        if (order.getId() == null) {
            addMovieDetailModelAttributes(movieId, userId, model, "Shop service is down. Movie is not bought");
            return MOVIE_DETAIL_VIEW;
        }

        addMovieDetailModelAttributes(movieId, userId, model, "Movie is successfully bought.");
        return REDIRECT_MOVIE_DETAIL_VIEW + movieId;
    }

    private void addMovieDetailModelAttributes(
            final Long movieId,
            final Long userId,
            final Model model,
            final String status) {
        model.addAttribute(STATUS_ATTR, status);
        model.addAttribute("movieDetail", movieDetailService.getMovieDetail(movieId, userId));
    }
}
