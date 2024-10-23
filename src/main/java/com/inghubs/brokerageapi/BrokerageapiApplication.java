package com.inghubs.brokerageapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.inghubs.brokerageapi.repository")
@EntityScan("com.inghubs.brokerageapi.entity")
public class BrokerageapiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BrokerageapiApplication.class, args);
    }
}
