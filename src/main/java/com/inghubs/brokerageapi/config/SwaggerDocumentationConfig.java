package com.inghubs.brokerageapi.config;

import java.util.List;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Configuration class for setting up Swagger documentation for the Brokerage API.
 */
@Configuration
public class SwaggerDocumentationConfig {
    private static final Logger logger = LoggerFactory.getLogger(SwaggerDocumentationConfig.class);

    /**
     * Bean definition for OpenAPI documentation settings.
     *
     * @return an OpenAPI instance configured for the Brokerage API.
     */
    @Bean
    public OpenAPI brokerageOpenAPI() {
        // Set up the server information for local development
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local Development Server");

        // Create and return OpenAPI configuration with metadata
        OpenAPI openAPI = new OpenAPI()
                .servers(List.of(localServer))
                .info(new Info()
                              .title("Brokerage API Management")
                              .description("This is OpenAPI UI environment generated for the Brokerage API specification")
                              .version("4.0.0")
                              .contact(new Contact()
                                               .name("Brokerage")
                                               .url("http://www.brokerage.com/")
                                               .email("info@brokerage.com"))
                              .license(new License()
                                               .name("Apache License Version 2.0")
                                               .url("https://www.apache.org/licenses/LICENSE-2.0")));

        logger.info("Swagger documentation configured for Brokerage API with version 4.0.0.");
        return openAPI;
    }
}
