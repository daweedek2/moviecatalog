package kostka.moviecatalog.enums;

public enum RuntimeConfigurationEnum {
    VISIBLE_MOVIES("visible_movies");

    private final String name;

    RuntimeConfigurationEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
