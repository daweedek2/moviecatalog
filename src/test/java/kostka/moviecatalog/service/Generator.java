package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import org.springframework.stereotype.Service;

@Service
public class Generator {

    private static final String TEST_NAME = "TestMovieName";

    public Movie createMovieWithName(final String name) {
        Movie movie = new Movie();
        movie.setName(name);
        return movie;
    }

    public MovieFormDto createValidMovieFormDto(final String name) {
        MovieFormDto dto = new MovieFormDto();
        dto.setName(name);
        return dto;
    }

    public RatingDto createValidRatingDto(final Long id, final int rating) {
        RatingDto dto = new RatingDto();
        dto.setId(id);
        dto.setRatingValue(rating);
        return dto;
    }

    public EsMovie createEsMovieWithName(final String name) {
        EsMovie esMovie = new EsMovie();
        esMovie.setName(name);
        return esMovie;
    }
}
