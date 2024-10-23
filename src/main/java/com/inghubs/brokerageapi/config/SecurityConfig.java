package com.inghubs.brokerageapi.config;

import com.inghubs.brokerageapi.constant.Roles;
import com.inghubs.brokerageapi.security.CustomAuthenticationEntryPoint;
import com.inghubs.brokerageapi.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Security configuration class to set up security filters and CORS settings.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Constructor for SecurityConfig that initializes JwtAuthenticationFilter and CustomAuthenticationEntryPoint.
     *
     * @param jwtAuthFilter          the filter responsible for JWT authentication.
     * @param authenticationEntryPoint the entry point for handling authentication errors.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, CustomAuthenticationEntryPoint authenticationEntryPoint) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        logger.info("SecurityConfig initialized with JwtAuthenticationFilter and CustomAuthenticationEntryPoint.");
    }

    /**
     * Configures the security filter chain for HTTP requests.
     *
     * @param http the HttpSecurity object to configure.
     * @return the configured SecurityFilterChain.
     * @throws Exception if an error occurs while configuring the security settings.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/error/**").permitAll() // Permit all requests to the error endpoint
                .requestMatchers("/h2-console/**").permitAll() // Permit access to H2 console
                .requestMatchers("/api/auth/**").permitAll() // Permit all auth-related endpoints
                .requestMatchers("/v3/api-docs/**", "/v3/api-docs.yaml", "/swagger-ui/**", "/swagger-ui.html").permitAll() // Permit Swagger documentation access
                .requestMatchers("/api/admin/**").hasRole(String.valueOf(Roles.ADMIN)) // Admin role required for admin endpoints
                .requestMatchers("/api/customer/**").hasAnyRole(String.valueOf(Roles.ADMIN), String.valueOf(Roles.CUSTOMER)) // Customer or Admin role required for customer endpoints
                .anyRequest().authenticated() // All other requests require authentication
                .and()
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before username/password authentication

        http.headers().frameOptions().disable(); // Disable frame options for H2 console access
        logger.debug("Security filter chain configured successfully.");
        return http.build();
    }

    /**
     * Bean definition for PasswordEncoder using BCrypt algorithm.
     *
     * @return a PasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("PasswordEncoder bean created using BCrypt algorithm.");
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures CORS settings for the application.
     *
     * @return the CORS configuration source.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("*"); // In production, specify exact origins for security
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setMaxAge(3600L); // CORS preflight cache duration

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration); // Apply CORS settings to API endpoints
        logger.debug("CORS configuration source created and registered.");
        return source;
    }
}
