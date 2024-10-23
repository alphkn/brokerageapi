package com.inghubs.brokerageapi.dto;

/**
 * Represents a request for authentication containing the username and password.
 */
public class AuthenticationRequest {
    private String username; // The username of the user attempting to authenticate
    private String password; // The password of the user attempting to authenticate

    /**
     * Gets the username of the authentication request.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the authentication request.
     *
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the password of the authentication request.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password for the authentication request.
     *
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
}
