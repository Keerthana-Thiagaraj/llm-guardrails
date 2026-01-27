package com.llmguardrails.config;

import com.llmguardrails.rules.RuleEngine;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yaml.snakeyaml.Yaml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;

@Configuration
@EnableConfigurationProperties({GuardrailsConfig.class, RulesConfiguration.class})
public class AppConfig {
    
    private static final Logger log = LoggerFactory.getLogger(AppConfig.class);

    private final GuardrailsConfig guardrailsConfig;

    public AppConfig(GuardrailsConfig guardrailsConfig) {
        this.guardrailsConfig = guardrailsConfig;
    }
    
    @Bean
    public RulesConfiguration rulesConfiguration() {
        try {
            Yaml yaml = new Yaml();
            String rulesFile = guardrailsConfig.getRules().getFile();
            
            // Remove "classpath:" prefix if present
            String resourcePath = rulesFile.startsWith("classpath:") 
                    ? rulesFile.substring("classpath:".length()) 
                    : rulesFile;
            
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                log.warn("Rules file not found: {}. Using empty rules.", resourcePath);
                return new RulesConfiguration();
            }
            
            RulesConfiguration config = yaml.loadAs(inputStream, RulesConfiguration.class);
            log.info("Loaded rules configuration from: {}", resourcePath);
            return config;
        } catch (Exception e) {
            log.error("Failed to load rules configuration", e);
            return new RulesConfiguration();
        }
    }
    
    @Bean
    public RuleEngine ruleEngine(RulesConfiguration rulesConfiguration) {
        RuleEngine engine = new RuleEngine(rulesConfiguration);
        engine.initialize();
        return engine;
    }
}

