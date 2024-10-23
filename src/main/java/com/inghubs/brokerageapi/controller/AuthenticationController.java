package com.inghubs.brokerageapi.controller;

import com.inghubs.brokerageapi.dto.AuthenticationRequest;
import com.inghubs.brokerageapi.dto.AuthenticationResponse;
import com.inghubs.brokerageapi.dto.RegisterRequest;
import com.inghubs.brokerageapi.service.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller for handling authentication-related API endpoints.
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth API", description = "Auth API endpoints")
public class AuthenticationController {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationService authenticationService;

    /**
     * Constructor for AuthenticationController that initializes AuthenticationService.
     *
     * @param authenticationService the service responsible for authentication-related logic.
     */
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
        logger.info("AuthenticationController initialized with AuthenticationService.");
    }

    /**
     * Endpoint for registering a new user.
     *
     * @param request the registration request containing user details.
     * @return a ResponseEntity containing the authentication response after registration.
     */
    @Operation(summary = "Register New User endpoint")
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        logger.debug("Registering new user with request: {}", request);
        AuthenticationResponse response = authenticationService.register(request);
        logger.info("User registered successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint for authenticating a user.
     *
     * @param request the authentication request containing user credentials.
     * @return a ResponseEntity containing the authentication response after successful login.
     */
    @Operation(summary = "Authenticate User endpoint")
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        logger.debug("Authenticating user with request: {}", request);
        AuthenticationResponse response = authenticationService.authenticate(request);
        logger.info("User authenticated successfully: {}", response.getUsername());
        return ResponseEntity.ok(response);
    }
}



