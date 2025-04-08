package com.astrology.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    
    @Value("${app.frontend.url}")
    private String frontendUrl;
    
    @Value("${app.cors.allowed-methods}")
    private String[] allowedMethods;
    
    @Value("${app.cors.allowed-headers}")
    private String[] allowedHeaders;
    
    @Value("${app.cors.max-age}")
    private long maxAge;
    
    public String getFrontendUrl() {
        return frontendUrl;
    }
    
    public String[] getAllowedMethods() {
        return allowedMethods;
    }
    
    public String[] getAllowedHeaders() {
        return allowedHeaders;
    }
    
    public long getMaxAge() {
        return maxAge;
    }
} 