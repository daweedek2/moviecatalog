package kostka.moviecatalog.entity.runtimeconfiguration;

public class VisibleMoviesOptionsRuntimeConfig {
    private Integer limit;

    public VisibleMoviesOptionsRuntimeConfig(final Integer limit) {
        this.limit = limit;
    }

    public VisibleMoviesOptionsRuntimeConfig() {
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }
}
