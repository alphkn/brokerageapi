package com.inghubs.brokerageapi.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.constant.Roles;
import com.inghubs.brokerageapi.dto.AuthenticationRequest;
import com.inghubs.brokerageapi.dto.AuthenticationResponse;
import com.inghubs.brokerageapi.dto.RegisterRequest;
import com.inghubs.brokerageapi.entity.Customer;
import com.inghubs.brokerageapi.entity.User;
import com.inghubs.brokerageapi.exception.UnauthorizedAccessException;
import com.inghubs.brokerageapi.repository.CustomerRepository;
import com.inghubs.brokerageapi.repository.UserRepository;
import com.inghubs.brokerageapi.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service for handling user authentication and registration processes.
 */
@Service
public class AuthenticationService {
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registers a new user and creates a customer record.
     *
     * @param request the registration request containing user details
     * @return an AuthenticationResponse containing the generated JWT and user details
     */
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        logger.info("Registering new user with username: {}", request.getUsername());

        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException(CommonConstants.USERNAME_ALREADY_EXISTS);
        }

        validatePassword(request.getPassword());
        validateRoles(request.getRoles());

        Customer customer = new Customer();
        User user = new User();
        try {
            user.setUsername(request.getUsername());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRoles(request.getRoles());
            user.setEnabled(true);
            user.setCustomer(customer);
            customer.setUser(userRepository.save(user));
            customer.setEnabled(true);
            customerRepository.save(customer);
        } catch (Exception e) {
            logger.error("Error during user registration: {}", e.getMessage());
            customerRepository.delete(customer); // Clean up in case of an error
        }

        // Generate JWT token
        String jwt = jwtService.generateToken(user);
        logger.info("User registered successfully. JWT generated.");

        return new AuthenticationResponse(jwt, user.getUsername(), user.getRoles(), user.getCustomer().getId());
    }

    /**
     * Validates the password's length.
     *
     * @param password the password to validate
     */
    private void validatePassword(String password) {
        if (password.length() < 8) {
            throw new RuntimeException(CommonConstants.PASSWORD_MUST_BE_AT_LEAST_8_CHARACTERS_LONG);
        }
    }

    /**
     * Validates the roles provided during registration.
     *
     * @param roles the list of roles to validate
     */
    private void validateRoles(List<String> roles) {
        Set<Roles> validRoles = Set.of(Roles.ADMIN, Roles.CUSTOMER);

        // Convert List<String> roles to Set<Roles>
        Set<Roles> providedRoles = roles.stream().map(role -> {
            try {
                return Roles.valueOf(role); // Convert String to Enum
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(CommonConstants.INVALID_ROLE_PROVIDED);
            }
        }).collect(Collectors.toSet());

        // Validate if provided roles are all valid
        if (!validRoles.containsAll(providedRoles)) {
            throw new RuntimeException(CommonConstants.INVALID_ROLE_PROVIDED);
        }
    }

    /**
     * Authenticates a user based on the provided credentials.
     *
     * @param request the authentication request containing user credentials
     * @return an AuthenticationResponse containing the generated JWT and user details
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        logger.info("Authenticating user with username: {}", request.getUsername());

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            User user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> new RuntimeException(CommonConstants.USER_NOT_FOUND));

            String jwt = jwtService.generateToken(user);
            logger.info("User authenticated successfully. JWT generated.");

            return new AuthenticationResponse(jwt, user.getUsername(), user.getRoles(), user.getCustomer().getId());
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for username: {}. Error: {}", request.getUsername(), e.getMessage());
            throw new RuntimeException(CommonConstants.INVALID_USERNAME_OR_PASSWORD);
        }
    }

    /**
     * Checks if the user has access to a specific customer's resources.
     *
     * @param userDetails the authenticated user's details
     * @param customerId  the ID of the customer to check access for
     */
    public void checkCustomerAccess(UserDetails userDetails, Long customerId) {
        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) {
            String username = userDetails.getUsername();
            Long requesterCustId = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username)).getCustomer().getId();

            if (!customerId.equals(requesterCustId)) {
                logger.error("Unauthorized access attempt by user: {} for customer ID: {}", username, customerId);
                throw new UnauthorizedAccessException("You do not have permission to access this customer's transactions.");
            }
        }
    }
}
