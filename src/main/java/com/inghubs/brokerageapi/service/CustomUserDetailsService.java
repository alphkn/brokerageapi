package com.inghubs.brokerageapi.service;

import com.inghubs.brokerageapi.constant.CommonConstants;
import com.inghubs.brokerageapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Custom implementation of UserDetailsService to load user-specific data.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads a user by their username.
     *
     * @param username the username of the user to be loaded
     * @return UserDetails object representing the user
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Attempting to load user with username: {}", username);

        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found with username: {}", username);
            return new UsernameNotFoundException(CommonConstants.USER_NOT_FOUND_WITH_USERNAME + username);
        });
    }
}
