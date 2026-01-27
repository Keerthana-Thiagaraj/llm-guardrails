package com.llmguardrails.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "llm-guardrails")
public class GuardrailsConfig {
    private DecisionConfig decision = new DecisionConfig();
    private AIConfig ai = new AIConfig();
    private RulesConfig rules = new RulesConfig();
    private RedactionConfig redaction = new RedactionConfig();
    private MetricsConfig metrics = new MetricsConfig();
    
    public static class DecisionConfig {
        private Double blockThreshold = 0.7;
        private Double redactThreshold = 0.4;
        private Double allowThreshold = 0.0;

        public DecisionConfig() {}

        public Double getBlockThreshold() {
            return blockThreshold;
        }

        public void setBlockThreshold(Double blockThreshold) {
            this.blockThreshold = blockThreshold;
        }

        public Double getRedactThreshold() {
            return redactThreshold;
        }

        public void setRedactThreshold(Double redactThreshold) {
            this.redactThreshold = redactThreshold;
        }

        public Double getAllowThreshold() {
            return allowThreshold;
        }

        public void setAllowThreshold(Double allowThreshold) {
            this.allowThreshold = allowThreshold;
        }
    }
    
    public static class AIConfig {
        private String provider = "openai";
        private OpenAIConfig openai = new OpenAIConfig();
        
        public static class OpenAIConfig {
            private String apiKey;
            private String model = "gpt-3.5-turbo";
            private Integer maxTokens = 100;
            private Double temperature = 0.0;
            private Boolean enabled = true;

            public OpenAIConfig() {}

            public String getApiKey() {
                return apiKey;
            }

            public void setApiKey(String apiKey) {
                this.apiKey = apiKey;
            }

            public String getModel() {
                return model;
            }

            public void setModel(String model) {
                this.model = model;
            }

            public Integer getMaxTokens() {
                return maxTokens;
            }

            public void setMaxTokens(Integer maxTokens) {
                this.maxTokens = maxTokens;
            }

            public Double getTemperature() {
                return temperature;
            }

            public void setTemperature(Double temperature) {
                this.temperature = temperature;
            }

            public Boolean getEnabled() {
                return enabled;
            }

            public void setEnabled(Boolean enabled) {
                this.enabled = enabled;
            }
        }

        public AIConfig() {}

        public String getProvider() {
            return provider;
        }

        public void setProvider(String provider) {
            this.provider = provider;
        }

        public OpenAIConfig getOpenai() {
            return openai;
        }

        public void setOpenai(OpenAIConfig openai) {
            this.openai = openai;
        }
    }
    
    public static class RulesConfig {
        private String file = "classpath:guard-rules.yml";
        private Boolean enabled = true;

        public RulesConfig() {}

        public String getFile() {
            return file;
        }

        public void setFile(String file) {
            this.file = file;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    public static class RedactionConfig {
        private String replacementText = "[REDACTED]";
        private Boolean enabled = true;

        public RedactionConfig() {}

        public String getReplacementText() {
            return replacementText;
        }

        public void setReplacementText(String replacementText) {
            this.replacementText = replacementText;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    public static class MetricsConfig {
        private String storage = "memory";
        private RedisConfig redis = new RedisConfig();
        
        public static class RedisConfig {
            private String host = "localhost";
            private Integer port = 6379;
            private Boolean enabled = false;

            public RedisConfig() {}

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public Integer getPort() {
                return port;
            }

            public void setPort(Integer port) {
                this.port = port;
            }

            public Boolean getEnabled() {
                return enabled;
            }

            public void setEnabled(Boolean enabled) {
                this.enabled = enabled;
            }
        }

        public MetricsConfig() {}

        public String getStorage() {
            return storage;
        }

        public void setStorage(String storage) {
            this.storage = storage;
        }

        public RedisConfig getRedis() {
            return redis;
        }

        public void setRedis(RedisConfig redis) {
            this.redis = redis;
        }
    }

    public GuardrailsConfig() {}

    public DecisionConfig getDecision() {
        return decision;
    }

    public void setDecision(DecisionConfig decision) {
        this.decision = decision;
    }

    public AIConfig getAi() {
        return ai;
    }

    public void setAi(AIConfig ai) {
        this.ai = ai;
    }

    public RulesConfig getRules() {
        return rules;
    }

    public void setRules(RulesConfig rules) {
        this.rules = rules;
    }

    public RedactionConfig getRedaction() {
        return redaction;
    }

    public void setRedaction(RedactionConfig redaction) {
        this.redaction = redaction;
    }

    public MetricsConfig getMetrics() {
        return metrics;
    }

    public void setMetrics(MetricsConfig metrics) {
        this.metrics = metrics;
    }
}

