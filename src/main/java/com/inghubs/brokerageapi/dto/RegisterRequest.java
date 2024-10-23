package com.inghubs.brokerageapi.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;


/**
 * Represents a request to register a new user, containing the necessary user details.
 */
@Getter
@Setter
public class RegisterRequest {
    private String username;      // The username for the new user account
    private String password;      // The password for the new user account
    private List<String> roles;   // List of roles assigned to the new user (e.g., ADMIN, CUSTOMER)

    // Additional validation annotations can be added if necessary
}