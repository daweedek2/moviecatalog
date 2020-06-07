package kostka.moviecatalog.service;

import kostka.moviecatalog.builders.MovieBuilder;
import kostka.moviecatalog.dto.OrderDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.Order;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.exception.UserNotAllowedToBuyMovieException;
import kostka.moviecatalog.service.communication.CommunicationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static kostka.moviecatalog.service.ExternalShopService.CHECK_ORDER;
import static kostka.moviecatalog.service.ExternalShopService.CREATE;
import static kostka.moviecatalog.service.ExternalShopService.SHOP_SERVICE_URL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ExternalShopServiceTest {
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_MOVIE_ID = 2L;

    @InjectMocks
    private ExternalShopService externalShopService;

    @Mock
    private CommunicationService communicationService;

    @Mock
    private UserService userService;

    @Mock
    private DbMovieService dbMovieService;

    @Test
    public void buyAdultMovieByAdultUserTest() {
        Order order = createOrder();
        OrderDto dto = createOrderDto();
        Movie adultMovie = createAdultMovie();
        User adultUser = createAdultUser();
        when(userService.getUser(any())).thenReturn(adultUser);
        when(dbMovieService.getMovie(any())).thenReturn(adultMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(false);
        when(communicationService.sendPostRequest(any(), any(), any())).thenReturn(order);

        Order result = externalShopService.buyMovieInShopService(dto);

        verify(communicationService).sendPostRequest(eq(SHOP_SERVICE_URL + CREATE), eq(dto), eq(Order.class));
        assertThat(result).isNotNull();
    }

    @Test
    public void buyAlreadyBoughtAdultMovieByAdultUserTest() {
        Order order = createOrder();
        OrderDto dto = createOrderDto();
        Movie adultMovie = createAdultMovie();
        User adultUser = createAdultUser();
        when(userService.getUser(any())).thenReturn(adultUser);
        when(dbMovieService.getMovie(any())).thenReturn(adultMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(true);

        Order result = externalShopService.buyMovieInShopService(dto);

        assertThat(result).isNull();
    }

    @Test
    public void buyAdultMovieByYoungUserTest() {
        OrderDto dto = createOrderDto();
        Movie adultMovie = createAdultMovie();
        User youngUser = createYoungUser();
        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(adultMovie);

        Order result = externalShopService.buyMovieInShopService(dto);

        assertThat(result).isNull();
    }

    @Test
    public void buyNormalMovieByYoungUserTest() {
        Order order = createOrder();
        OrderDto dto = createOrderDto();
        Movie normalMovie = createNormalMovie();
        User youngUser = createYoungUser();
        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(normalMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(false);
        when(communicationService.sendPostRequest(any(), any(), any())).thenReturn(order);

        Order result = externalShopService.buyMovieInShopService(dto);

        assertThat(result).isNotNull();
    }

    @Test
    public void buyAlreadyBoughtNormalMovieByYoungUserTest() {
        Order order = createOrder();
        OrderDto dto = createOrderDto();
        Movie normalMovie = createNormalMovie();
        User youngUser = createYoungUser();
        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(normalMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(true);

        Order result = externalShopService.buyMovieInShopService(dto);

        assertThat(result).isNull();
    }

    @Test
    public void buyNormalMovieByAdultUserTest() {
        Order order = createOrder();
        OrderDto dto = createOrderDto();
        Movie normalMovie = createNormalMovie();
        User youngUser = createAdultUser();
        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(normalMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(false);
        when(communicationService.sendPostRequest(any(), any(), any())).thenReturn(order);

        Order result = externalShopService.buyMovieInShopService(dto);

        assertThat(result).isNotNull();
    }

    @Test
    public void buyAlreadyBougthNormalMovieByAdultUserTest() {
        Order order = createOrder();
        OrderDto dto = createOrderDto();
        Movie normalMovie = createNormalMovie();
        User youngUser = createAdultUser();
        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(normalMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(true);

        Order result = externalShopService.buyMovieInShopService(dto);

        assertThat(result).isNull();
    }

    @Test
    public void getAlreadyBoughtMoviesTest() {
        when(communicationService.sendGetRequest(any(), any())).thenReturn(true);
        boolean result = externalShopService.checkAlreadyBoughtMovieForUser(TEST_MOVIE_ID, TEST_USER_ID);

        verify(communicationService).sendGetRequest(
                eq(SHOP_SERVICE_URL + CHECK_ORDER + TEST_MOVIE_ID + "/" + TEST_USER_ID),
                eq(Boolean.class));

        assertThat(result).isTrue();
    }

    @Test
    public void isYoungUserAllowedToBuyAdultMovieTest() {
        User youngUser = createYoungUser();
        Movie adultMovie = createAdultMovie();

        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(adultMovie);

        boolean result = externalShopService.isUserAllowedToBuyMovie(TEST_MOVIE_ID, TEST_USER_ID);

        assertThat(result).isFalse();
    }

    @Test
    public void isAdultUserAllowedToBuyAlreadyBoughtAdultMovieTest() {
        User adultUser = createAdultUser();
        Movie adultMovie = createAdultMovie();

        when(userService.getUser(any())).thenReturn(adultUser);
        when(dbMovieService.getMovie(any())).thenReturn(adultMovie);
        when(communicationService.sendGetRequest(any(), any())).thenReturn(true);

        boolean result = externalShopService.isUserAllowedToBuyMovie(TEST_MOVIE_ID, TEST_USER_ID);

        assertThat(result).isFalse();
    }

    @Test
    public void validateYoungUserAgeToBuyAdultMovieTest() {
        User youngUser = createYoungUser();
        Movie adultMovie = createAdultMovie();

        when(userService.getUser(any())).thenReturn(youngUser);
        when(dbMovieService.getMovie(any())).thenReturn(adultMovie);

        assertThatThrownBy(() -> externalShopService.validateUserAgeForMovie(TEST_MOVIE_ID, TEST_USER_ID))
                .isInstanceOf(UserNotAllowedToBuyMovieException.class);
    }

    @Test
    public void checkThatUserAlreadyBoughtMovieTest() {
        when(communicationService.sendGetRequest(any(), any())).thenReturn(true);

        assertThatThrownBy(() -> externalShopService.validateUserAlreadyBoughtMovie(TEST_MOVIE_ID, TEST_USER_ID))
                .isInstanceOf(UserNotAllowedToBuyMovieException.class);

    }

    private User createAdultUser() {
        User adultUser = new User();
        adultUser.setBirthDate(LocalDate.now().minusYears(19));
        return adultUser;
    }

    private User createYoungUser() {
        User youngUser = new User();
        youngUser.setBirthDate(LocalDate.now().minusYears(17));
        return youngUser;
    }

    private Movie createAdultMovie() {
        return new MovieBuilder()
                .setForAdults(true)
                .build();
    }

    private Movie createNormalMovie() {
        return new MovieBuilder()
                .setForAdults(false)
                .build();
    }

    private OrderDto createOrderDto() {
        OrderDto dto = new OrderDto();
        dto.setUserId(TEST_USER_ID);
        dto.setMovieId(TEST_MOVIE_ID);
        return dto;
    }

    private Order createOrder() {
        Order order = new Order();
        order.setId(TEST_USER_ID);
        order.setUserId(TEST_USER_ID);
        order.setMovieId(TEST_MOVIE_ID);
        order.setTimestamp(LocalDateTime.now());
        return order;
    }

}
