package com.astrology.api.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@Configuration
public class DeepSeekConfig {
    private static final Logger logger = LoggerFactory.getLogger(DeepSeekConfig.class);
    
    @Value("${deepseek.api.key}")
    private String apiKey;
    
    @Value("${deepseek.api.url}")
    private String apiUrl;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    public String getApiKey() {
        logger.info("DeepSeek API URL: {}", apiUrl);
        logger.info("DeepSeek API Key length: {}", apiKey != null ? apiKey.length() : 0);
        return apiKey;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
} 