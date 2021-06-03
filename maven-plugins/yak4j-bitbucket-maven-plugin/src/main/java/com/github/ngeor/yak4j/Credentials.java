package com.github.ngeor.yak4j;

import java.util.Objects;

/**
 * Username and password for basic authentication.
 */
@SuppressWarnings("WeakerAccess")
public class Credentials {
    private final String username;
    private final String password;

    /**
     * Creates an instance of this class.
     *
     * @param username The username.
     * @param password The password.
     */
    public Credentials(String username, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("username cannot be empty");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("password cannot be empty");
        }

        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Credentials)) {
            return false;
        }

        Credentials that = (Credentials) o;
        return Objects.equals(username, that.username)
            && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password);
    }

    @Override
    public String toString() {
        return "Credentials{"
            + "username='" + username + '\''
            + ", password='" + password + '\''
            + '}';
    }
}
