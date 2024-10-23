package com.inghubs.brokerageapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Configuration class for setting up application-level security components.
 */
@Configuration
public class ApplicationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor for ApplicationConfig that initializes UserDetailsService and PasswordEncoder.
     *
     * @param userDetailsService the service used to load user-specific data.
     * @param passwordEncoder    the encoder used to encode passwords.
     */
    public ApplicationConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        logger.info("ApplicationConfig initialized with UserDetailsService and PasswordEncoder.");
    }

    /**
     * Bean definition for AuthenticationProvider that utilizes the specified UserDetailsService and PasswordEncoder.
     *
     * @return an AuthenticationProvider instance.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        logger.debug("AuthenticationProvider created with UserDetailsService and PasswordEncoder.");
        return authProvider;
    }

    /**
     * Bean definition for AuthenticationManager used in the application.
     *
     * @param config the AuthenticationConfiguration instance.
     *
     * @return an AuthenticationManager instance.
     *
     * @throws Exception if an error occurs while retrieving the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.debug("Retrieving AuthenticationManager from AuthenticationConfiguration.");
        return config.getAuthenticationManager();
    }
}
