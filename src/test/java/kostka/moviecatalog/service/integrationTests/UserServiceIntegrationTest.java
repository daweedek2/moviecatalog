package kostka.moviecatalog.service.integrationTests;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.UserDto;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.RoleRepository;
import kostka.moviecatalog.repository.UserRepository;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static kostka.moviecatalog.configuration.WebSecurityConfiguration.USER_ROLE;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class UserServiceIntegrationTest {
    private static final String TEST_USER_NAME = "username";
    private static final String TEST_LAST_NAME = "lastName";
    private static final String TEST_FIRST_NAME = "firstName";
    private static final String TEST_PASSWORD = "secrets";
    private static final String TEST_BIRTH_DATE = "1990-02-01";
    private static final LocalDate TEST_BIRTH_DATE_DB = LocalDate.of(1990,2,1);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserService userService;

    @MockBean
    EsMovieService esMovieService;
    @MockBean
    MovieElasticSearchRepository movieElasticSearchRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void createUserSuccessfullyIntegrationTest() {
        UserDto dto = new UserDto();
        dto.setFirstName(TEST_FIRST_NAME);
        dto.setLastName(TEST_LAST_NAME);
        dto.setPassword(TEST_PASSWORD);
        dto.setUserName(TEST_USER_NAME);
        dto.setBirthDate(TEST_BIRTH_DATE);

        User createdUser = userService.createUser(dto);

        assertThat(createdUser.getUsername()).isEqualTo(TEST_USER_NAME);
        assertThat(createdUser.getFirstName()).isEqualTo(TEST_FIRST_NAME);
        assertThat(createdUser.getLastName()).isEqualTo(TEST_LAST_NAME);
        assertThat(createdUser.getBirthDate()).isEqualTo(TEST_BIRTH_DATE_DB);
        assertThat(passwordEncoder.matches(TEST_PASSWORD, createdUser.getPassword())).isTrue();
        assertThat(createdUser.getRoles()).contains(roleRepository.findByName(USER_ROLE));
    }
}
