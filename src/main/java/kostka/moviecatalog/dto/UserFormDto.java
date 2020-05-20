package kostka.moviecatalog.dto;

import javax.validation.constraints.NotEmpty;

public final class UserFormDto {
    private String firstName;
    private String lastName;
    @NotEmpty(message = "Birth date cannot be empty.")
    private String birthDate;
    @NotEmpty(message = "User name cannot be empty.")
    private String userName;
    @NotEmpty(message = "Password cannot be empty.")
    private String password;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(final String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(final String lastName) {
        this.lastName = lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(final String birthDate) {
        this.birthDate = birthDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(final String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }
}
