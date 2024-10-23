package com.inghubs.brokerageapi.security;

import java.io.IOException;

import com.inghubs.brokerageapi.service.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;


/**
 * JWT Authentication Filter for processing incoming requests and validating JWT tokens.
 * This filter extracts the JWT from the Authorization header and authenticates the user.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService; // Service for handling JWT operations
    private final CustomUserDetailsService userDetailsService; // Service for loading user details

    public JwtAuthenticationFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Filters incoming requests to authenticate users based on the JWT token.
     *
     * @param request       the HttpServletRequest object containing the request details
     * @param response      the HttpServletResponse object to send the response
     * @param filterChain   the filter chain for further processing
     * @throws ServletException if an error occurs during the filtering process
     * @throws IOException      if an input or output error occurs
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Extract the Authorization header from the request
        final String authHeader = request.getHeader("Authorization");

        // If no authorization header or it does not start with "Bearer ", continue the filter chain
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT from the header
            final String jwt = authHeader.substring(7);
            // Extract username from the JWT
            final String username = jwtService.extractUsername(jwt);

            // If username is present and authentication is not already set
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user details from the user service
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // Validate the JWT token
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Set the authentication in the security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Log any exceptions that occur during authentication
            logger.error("Cannot set user authentication: {}", e);
        }

        // Continue with the filter chain
        filterChain.doFilter(request, response);
    }
}