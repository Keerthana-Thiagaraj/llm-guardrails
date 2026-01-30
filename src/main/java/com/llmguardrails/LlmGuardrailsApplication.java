package com.llmguardrails;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class LlmGuardrailsApplication {
    
    public static void main(String[] args) {
        // Workaround for Java 24 class file compatibility with Spring Boot 3.3.5
        System.setProperty("spring.classformat.ignore", "true");
        SpringApplication.run(LlmGuardrailsApplication.class, args);
    }
}

