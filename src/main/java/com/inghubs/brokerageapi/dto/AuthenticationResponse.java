package com.inghubs.brokerageapi.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents the response after successful authentication, containing the user's token and details.
 */
@Getter
@Setter
public class AuthenticationResponse {
    private String token;        // The JWT or session token for the authenticated user
    private String username;     // The username of the authenticated user
    private Long customerId;     // The ID of the customer associated with the user
    private List<String> roles;  // List of roles assigned to the authenticated user

    /**
     * Constructor for creating an AuthenticationResponse.
     *
     * @param token      the token issued upon successful authentication
     * @param username   the username of the authenticated user
     * @param roles      the list of roles assigned to the authenticated user
     * @param customerId the ID of the customer associated with the user
     */
    public AuthenticationResponse(String token, String username, List<String> roles, Long customerId) {
        this.token = token;
        this.username = username;
        this.roles = roles;
        this.customerId = customerId;
    }
}
