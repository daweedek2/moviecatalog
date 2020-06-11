package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.dto.UserDetailDto;
import kostka.moviecatalog.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserDetailService.class);
    private final UserService userService;
    private final ExternalShopService externalShopService;
    private final ExternalCommentService externalCommentService;
    private final ExternalRatingService externalRatingService;

    public UserDetailService(
            final UserService userService,
            final ExternalShopService externalShopService,
            final ExternalCommentService externalCommentService,
            final ExternalRatingService externalRatingService) {
        this.userService = userService;
        this.externalShopService = externalShopService;
        this.externalCommentService = externalCommentService;
        this.externalRatingService = externalRatingService;
    }

    public UserDetailDto getUserDetailDto(final Long userId) throws JsonProcessingException {
        User user = userService.getUser(userId);
        int boughtMoviesCount = externalShopService.getBoughtMoviesByUserCount(userId);
        int commentsCount = externalCommentService.getCommentsByUserCount(userId);
        int ratingsCount = externalRatingService.getRatingsByUserCount(userId);
        LOGGER.info("User detail data are prepared.");
        return populateUserDetailDtoWithData(user, boughtMoviesCount, ratingsCount, commentsCount);
    }

    private UserDetailDto populateUserDetailDtoWithData(
            final User user,
            final int boughtMoviesCount,
            final int ratingsCount,
            final int commentsCount) {
        UserDetailDto dto = new UserDetailDto();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setBirthDate(user.getBirthDate());
        dto.setBanned(user.isBanned());
        dto.setRole(user.getRoles().iterator().next().getName());
        dto.setBoughtMoviesCount(boughtMoviesCount);
        dto.setCommentsCount(commentsCount);
        dto.setRatingsCount(ratingsCount);
        return dto;
    }

}
