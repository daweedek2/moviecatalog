package kostka.moviecatalog.service;

import kostka.moviecatalog.exception.InvalidDtoException;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {
    private static final Integer MAX_VISIBLE_MOVIES = 10;
    private static final Integer MIN_VISIBLE_MOVIES = 1;

    public void validateVisibleMoviesConfigValue(final Integer newValue) {
        if (newValue < MIN_VISIBLE_MOVIES || newValue > MAX_VISIBLE_MOVIES) {
            throw new InvalidDtoException();
        }
    }
}
