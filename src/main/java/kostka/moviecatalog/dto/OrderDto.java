package kostka.moviecatalog.dto;

import javax.validation.constraints.NotNull;

public final class OrderDto {
    @NotNull(message = "movieId cannot be empty")
    private Long movieId;
    @NotNull(message = "userId cannot be empty")
    private Long userId;

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }
}
