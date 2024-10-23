package com.inghubs.brokerageapi.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;


/**
 * Custom Authentication Entry Point for handling unauthorized access attempts.
 * This class implements the AuthenticationEntryPoint interface to customize the response
 * when an authentication exception occurs.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Commences an authentication scheme, generating a response when authentication fails.
     *
     * @param request       the HttpServletRequest object containing the request details
     * @param response      the HttpServletResponse object to send the response
     * @param authException the exception that triggered the authentication failure
     * @throws IOException      if an input or output error occurs
     * @throws ServletException if the request could not be handled
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        // Set response content type to JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        // Set response status to 401 Unauthorized
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a response body to provide details about the error
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());

        // Write the response body to the output stream
        objectMapper.writeValue(response.getOutputStream(), body);
    }
}