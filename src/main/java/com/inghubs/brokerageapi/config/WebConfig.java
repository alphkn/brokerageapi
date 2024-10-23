package com.inghubs.brokerageapi.config;

import com.inghubs.brokerageapi.interceptor.LoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


/**
 * Web configuration class that implements WebMvcConfigurer to customize Spring MVC settings.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    private final LoggingInterceptor loggingInterceptor;

    /**
     * Constructor for WebConfig that initializes the LoggingInterceptor.
     *
     * @param loggingInterceptor the interceptor used for logging requests.
     */
    @Autowired
    public WebConfig(LoggingInterceptor loggingInterceptor) {
        this.loggingInterceptor = loggingInterceptor;
        logger.info("WebConfig initialized with LoggingInterceptor.");
    }

    /**
     * Adds the LoggingInterceptor to the application's interceptor registry.
     *
     * @param registry the InterceptorRegistry to which interceptors are added.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add LoggingInterceptor for all API paths
        registry.addInterceptor(loggingInterceptor).addPathPatterns("/api/**");
        logger.debug("LoggingInterceptor added for path patterns: /api/**");
    }
}