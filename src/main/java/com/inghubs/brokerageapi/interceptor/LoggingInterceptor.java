package com.inghubs.brokerageapi.interceptor;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class LoggingInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingInterceptor.class);

    /**
     * Intercepts incoming requests to log request details.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param handler  the handler for the request
     * @return true to continue the request handling; false to abort
     * @throws IOException if an input or output exception occurs
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        // Log the incoming request method and URI
        logger.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());
        return true; // Continue with the next interceptor or the controller
    }

    /**
     * Intercepts the completion of the request to log response details.
     *
     * @param request  the HttpServletRequest object
     * @param response the HttpServletResponse object
     * @param handler  the handler for the request
     * @param ex       any exception thrown during request processing
     * @throws Exception if any error occurs during completion
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Log the outgoing response status
        logger.info("Outgoing response: status {}", response.getStatus());
        if (ex != null) {
            // Log any exception that occurred during request processing
            logger.error("Request raised exception", ex);
        }
    }
}

