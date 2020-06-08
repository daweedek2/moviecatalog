package kostka.moviecatalog.dto;

import java.time.LocalDate;

public class UserDetailDto {

    private Long userId;
    private LocalDate birthDate;
    private String fullName;
    private boolean banned = false;
    private String role;
    private int commentsCount;
    private int ratingsCount;
    private int boughtMoviesCount;

    public UserDetailDto() {
    }

    public UserDetailDto(final Long userId, final LocalDate birthDate, final String fullName, final boolean banned,
                         final String role, final int commentsCount, final int ratingsCount,
                         final int boughtMoviesCount) {
        this.userId = userId;
        this.birthDate = birthDate;
        this.fullName = fullName;
        this.banned = banned;
        this.role = role;
        this.commentsCount = commentsCount;
        this.ratingsCount = ratingsCount;
        this.boughtMoviesCount = boughtMoviesCount;
    }

    public String getRole() {
        return role;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String fullName) {
        this.fullName = fullName;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(final boolean banned) {
        this.banned = banned;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(final int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public void setRatingsCount(final int ratingsCount) {
        this.ratingsCount = ratingsCount;
    }

    public int getBoughtMoviesCount() {
        return boughtMoviesCount;
    }

    public void setBoughtMoviesCount(final int boughtMoviesCount) {
        this.boughtMoviesCount = boughtMoviesCount;
    }
}
