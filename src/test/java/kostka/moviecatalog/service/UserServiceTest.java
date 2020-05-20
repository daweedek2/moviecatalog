package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.UserFormDto;
import kostka.moviecatalog.entity.Role;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.exception.FutureBirthDateException;
import kostka.moviecatalog.exception.UserNameNotUniqueException;
import kostka.moviecatalog.repository.RoleRepository;
import kostka.moviecatalog.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static kostka.moviecatalog.service.UserService.ADULTS_LIMIT;
import static kostka.moviecatalog.service.UserService.isUserAdultCheck;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    public static final String TEST_USERNAME = "test";
    public static final String TEST_PASSWORD = "pwd";
    public static final String BIRTH_DATE_STRING = "1990-02-01";
    public static final String FUTURE_BIRTH_DATE_STRING = "2050-02-01";
    public static final LocalDate TEST_BIRTH_DATE = LocalDate.of(1990, 2, 1);

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    public void createUserSuccessTest() {
        User user = new User();
        user.setUsername(TEST_USERNAME);
        user.setPassword(TEST_PASSWORD);
        user.setBirthDate(TEST_BIRTH_DATE);

        UserFormDto dto = new UserFormDto();
        dto.setBirthDate(BIRTH_DATE_STRING);
        dto.setUserName(TEST_USERNAME);
        dto.setPassword(TEST_PASSWORD);
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);
        when(roleRepository.findByName(any())).thenReturn(new Role());
        when(passwordEncoder.encode(any())).thenReturn(anyString());

        User result = userService.createUser(dto);

        assertThat(result).isEqualTo(user);
        assertThat(result.getBirthDate()).isEqualTo(TEST_BIRTH_DATE);
        assertThat(result.getPassword()).isEqualTo(TEST_PASSWORD);
        assertThat(result.getUsername()).isEqualTo(TEST_USERNAME);
    }

    @Test
    public void createUserAlreadyUsedUserNameTest() {
        User user = new User();
        User user2 = new User();
        UserFormDto dto = new UserFormDto();
        dto.setBirthDate(BIRTH_DATE_STRING);
        dto.setUserName(TEST_USERNAME);
        dto.setPassword(TEST_PASSWORD);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user2));

        assertThatThrownBy(() -> userService.createUser(dto)).isInstanceOf(UserNameNotUniqueException.class);
    }

    @Test
    public void createUserFutureBirthDateTest() {
        User user = new User();
        User user2 = new User();
        UserFormDto dto = new UserFormDto();
        dto.setBirthDate(FUTURE_BIRTH_DATE_STRING);
        dto.setUserName(TEST_USERNAME);
        dto.setPassword(TEST_PASSWORD);
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(dto)).isInstanceOf(FutureBirthDateException.class);
    }

    @Test
    public void userBirthDateValidationTest() {
        User adultUser = new User();
        adultUser.setBirthDate(LocalDate.now().minusYears(ADULTS_LIMIT).minusDays(1));

        User youngUser = new User();
        youngUser.setBirthDate(LocalDate.now().minusYears(ADULTS_LIMIT).plusDays(1));

        assertThat(isUserAdultCheck(adultUser)).isTrue();
        assertThat(isUserAdultCheck(youngUser)).isFalse();
    }
}
