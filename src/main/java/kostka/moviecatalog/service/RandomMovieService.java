package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.exception.NoMoviesInDbException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;

@Service
public class RandomMovieService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RandomMovieService.class);
    private CacheService cacheService;
    private Random random = new Random();

    @Autowired
    public RandomMovieService(final CacheService cacheService) {
        this.cacheService = cacheService;
    }

    public Long getRandomMovieIdFromAllMovies() {
        List<MovieListDto> allMovies = cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY);
        if (allMovies.isEmpty()) {
            throw new NoMoviesInDbException();
        }

        int randomMovieIndex = random.nextInt(allMovies.size());
        Long randomMovieId = allMovies.get(randomMovieIndex).getId();
        LOGGER.info("generated random movieId '{}'", randomMovieId);
        return randomMovieId;
    }
}
