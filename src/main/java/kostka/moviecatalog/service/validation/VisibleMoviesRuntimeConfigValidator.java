package kostka.moviecatalog.service.validation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.entity.runtimeconfiguration.RuntimeConfiguration;
import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesOptionsRuntimeConfig;
import kostka.moviecatalog.exception.InvalidRuntimeConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static kostka.moviecatalog.enumeration.RuntimeConfigurationEnum.VISIBLE_MOVIES;

public final class VisibleMoviesRuntimeConfigValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(VisibleMoviesRuntimeConfigValidator.class);
    private static final String INVALID_VISIBLE_MOVIES_MESSAGE = "Allowed limit values are only: 0 < limit <= 10.";

    private VisibleMoviesRuntimeConfigValidator() {
        throw new IllegalStateException();
    }

    public static void validateVisibleMoviesOptions(final RuntimeConfiguration runtimeConfiguration) throws JsonProcessingException {
        if (runtimeConfiguration.getConfigType() != VISIBLE_MOVIES) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        VisibleMoviesOptionsRuntimeConfig visibleMoviesOptions = objectMapper
                .readValue(runtimeConfiguration.getOptions(), VisibleMoviesOptionsRuntimeConfig.class);
        int limit = visibleMoviesOptions.getLimit();

        if ((limit < 1) || (limit > 10)) {
            LOGGER.error("Runtime config options are invalid: {}", INVALID_VISIBLE_MOVIES_MESSAGE);
            throw new InvalidRuntimeConfigurationException(INVALID_VISIBLE_MOVIES_MESSAGE);
        }
    }
}
