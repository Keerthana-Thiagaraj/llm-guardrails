package com.llmguardrails.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "rules")
public class RulesConfiguration {
    private List<RuleDefinition> block = List.of();
    private List<RuleDefinition> redact = List.of();
    
    public static class RuleDefinition {
        private String name;
        private String pattern;
        private Double severity;
        private String description;

        public RuleDefinition() {}

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPattern() {
            return pattern;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }

        public Double getSeverity() {
            return severity;
        }

        public void setSeverity(Double severity) {
            this.severity = severity;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public RulesConfiguration() {}

    public List<RuleDefinition> getBlock() {
        return block;
    }

    public void setBlock(List<RuleDefinition> block) {
        this.block = block;
    }

    public List<RuleDefinition> getRedact() {
        return redact;
    }

    public void setRedact(List<RuleDefinition> redact) {
        this.redact = redact;
    }
}

