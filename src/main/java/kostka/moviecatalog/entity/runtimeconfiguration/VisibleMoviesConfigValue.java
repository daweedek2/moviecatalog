package kostka.moviecatalog.entity.runtimeconfiguration;

public class VisibleMoviesConfigValue {
    private Integer limit;

    public VisibleMoviesConfigValue(final Integer limit) {
        this.limit = limit;
    }

    public VisibleMoviesConfigValue() {
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(final Integer limit) {
        this.limit = limit;
    }
}
