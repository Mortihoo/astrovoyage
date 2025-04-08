package com.astrology.api.config;

import com.astrology.api.model.City;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class CityConfig {

    @Bean
    public Map<String, List<City>> cities(ObjectMapper objectMapper) throws IOException {
        ClassPathResource resource = new ClassPathResource("cities.json");
        return objectMapper.readValue(resource.getInputStream(), 
            new TypeReference<Map<String, List<City>>>() {});
    }
} 