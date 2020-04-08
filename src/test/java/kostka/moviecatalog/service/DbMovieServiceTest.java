package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.redis.RedisService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class DbMovieServiceTest {
    public static final String TEST_NAME = "testName";
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DbMovieService dbMovieService;
    @MockBean
    private RedisService redisService;
    @MockBean
    EsMovieService esMovieService;
    @MockBean
    MovieElasticSearchRepository movieElasticSearchRepository;

    @Test
    public void createMovieTest() {
        MovieDto dto = new MovieDto();
        dto.setName(TEST_NAME);
        dbMovieService.createMovie(dto);

        Assert.assertEquals(1, movieRepository.findAll().size());
        Assert.assertEquals(1, movieRepository.count());
    }

}
