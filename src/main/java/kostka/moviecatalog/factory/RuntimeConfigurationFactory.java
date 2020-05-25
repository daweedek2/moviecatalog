package kostka.moviecatalog.factory;

import kostka.moviecatalog.dto.RuntimeConfigDto;
import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesConfigValue;

public class RuntimeConfigurationFactory {

    public static Object getValueForUpdateRuntimeConfig(final RuntimeConfigDto dto) {
        switch (dto.getConfigName()) {
            case "visible_movies":
                return new VisibleMoviesConfigValue(dto.getLimit());
            default:
                return new Object();
        }
    }
}
