package kostka.moviecatalog.factory;

import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesOptionsRuntimeConfig;
import kostka.moviecatalog.enumeration.RuntimeConfigurationEnum;

public class RuntimeConfigurationFactory {

    private static final Class visibleMoviesOptionsClass = VisibleMoviesOptionsRuntimeConfig.class;

    public static <T> Class<T> getOptionsClass(final RuntimeConfigurationEnum runtimeConfiguration) {
        switch (runtimeConfiguration) {
            case VISIBLE_MOVIES: return visibleMoviesOptionsClass;
            default: return null;
        }
    }
}
