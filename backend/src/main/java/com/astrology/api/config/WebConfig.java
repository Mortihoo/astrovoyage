package com.astrology.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final AppConfig appConfig;
    
    public WebConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        
        // Allowed origins
        config.addAllowedOrigin(appConfig.getFrontendUrl());
        
        // Allowed HTTP methods
        for (String method : appConfig.getAllowedMethods()) {
            config.addAllowedMethod(method);
        }
        
        // Allowed headers
        for (String header : appConfig.getAllowedHeaders()) {
            config.addAllowedHeader(header);
        }
        
        // Allow credentials
        config.setAllowCredentials(true);
        
        // Preflight request validity period
        config.setMaxAge(appConfig.getMaxAge());
        
        // Exposed response headers
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Set-Cookie");
        
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
} 