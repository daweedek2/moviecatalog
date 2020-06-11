package kostka.moviecatalog.enumeration;

public enum RuntimeConfigurationEnum {
    VISIBLE_MOVIES("visible_movies"),
    OTHER("other");

    private final String name;

    RuntimeConfigurationEnum(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
