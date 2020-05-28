package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.UserFormDto;
import kostka.moviecatalog.dto.UserListDto;
import kostka.moviecatalog.entity.Role;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.exception.FutureBirthDateException;
import kostka.moviecatalog.exception.UserNameNotUniqueException;
import kostka.moviecatalog.exception.UserNotFoundException;
import kostka.moviecatalog.repository.RoleRepository;
import kostka.moviecatalog.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static kostka.moviecatalog.configuration.WebSecurityConfiguration.USER_ROLE;

@Service
public class UserService {
    public static final int ADULTS_LIMIT = 18;
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(final UserRepository userRepository,
                       final RoleRepository roleRepository,
                       final PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(final UserFormDto dto) {
        validateDto(dto);
        LOGGER.info("creating user with username '{}'.", dto.getUserName());
        User user = populateUserFromUserDto(dto);
        user.setPassword(getSecuredPassword(dto.getPassword()));
        user.setRoles(getUserRole(roleRepository.findByName(USER_ROLE)));
        return saveUser(user);
    }

    private Set<Role> getUserRole(final Role role) {
        return Collections.singleton(role);
    }

    private void validateDto(final UserFormDto dto) {
        LOGGER.info("Validating user Dto.");
        if (isUsernameAlreadyUsed(dto.getUserName())) {
            throw new UserNameNotUniqueException();
        }
        if (isBirthDateInFuture(LocalDate.parse(dto.getBirthDate()))) {
            throw new FutureBirthDateException();
        }
    }

    public User saveUser(final User user) {
        LOGGER.info("Saving user with username '{}'.", user.getUsername());
        return userRepository.save(user);
    }

    public List<UserListDto> getAllUsers() {
        LOGGER.info("Getting all users from database");
        return userRepository.findAll()
                .stream()
                .map(this::populateUserListDto)
                .collect(Collectors.toList());
    }

    private boolean isBirthDateInFuture(final LocalDate birthDate) {
        return birthDate.isAfter(LocalDate.now());
    }

    private boolean isUsernameAlreadyUsed(final String userName) {
        return userRepository.findByUsername(userName).isPresent();
    }

    private User populateUserFromUserDto(final UserFormDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setBirthDate(LocalDate.parse(dto.getBirthDate()));
        user.setUsername(dto.getUserName());
        return user;
    }

    private UserListDto populateUserListDto(final User user) {
        UserListDto dto = new UserListDto();
        dto.setUserId(user.getUserId());
        dto.setUserName(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setBirthDate(user.getBirthDate());
        return dto;
    }

    private String getSecuredPassword(final String password) {
        LOGGER.info("Securing password...");
        return passwordEncoder.encode(password);
    }

    public static boolean isUserAdultCheck(final User user) {
        LocalDate birthDate = user.getBirthDate();

        return birthDate.isBefore(LocalDate.now().minusYears(ADULTS_LIMIT));
    }

    public User getUser(final Long id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    public void deleteUser(final Long userId) {
        LOGGER.info("Deleting user with id '{}'.", userId);
        userRepository.delete(this.getUser(userId));
    }
}
