package com.astrology.api.service;

import com.astrology.api.config.DeepSeekConfig;
import com.astrology.api.dto.DeepSeekRequest;
import com.astrology.api.dto.DeepSeekResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ChartInterpretationService {

    private static final Logger logger = LoggerFactory.getLogger(ChartInterpretationService.class);
    private final RestTemplate restTemplate;
    private final DeepSeekConfig deepSeekConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public ChartInterpretationService(RestTemplate restTemplate, 
                                    DeepSeekConfig deepSeekConfig,
                                    ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.deepSeekConfig = deepSeekConfig;
        this.objectMapper = objectMapper;
    }

    public String interpretChart(String question, Map<String, Object> chartData) {
        try {
            // Prepare the prompt
            String prompt = buildPrompt(question, chartData);
            logger.debug("Generated prompt: {}", prompt);
            
            // Create request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + deepSeekConfig.getApiKey());
            
            // Create request body
            DeepSeekRequest request = new DeepSeekRequest();
            request.setModel("deepseek-chat");
            request.setTemperature(0.7);
            request.setMaxTokens(1000);
            request.setPrompt(prompt);
            
            // Make API call
            HttpEntity<DeepSeekRequest> entity = new HttpEntity<>(request, headers);
            logger.debug("Making API request to: {}", deepSeekConfig.getApiUrl());
            
            ResponseEntity<DeepSeekResponse> responseEntity = restTemplate.postForEntity(
                deepSeekConfig.getApiUrl(),
                entity,
                DeepSeekResponse.class
            );
            
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                DeepSeekResponse response = responseEntity.getBody();
                if (response != null && !response.getChoices().isEmpty()) {
                    String interpretation = response.getChoices().get(0).getText();
                    logger.debug("Received interpretation: {}", interpretation);
                    return interpretation;
                }
                logger.warn("Empty response from DeepSeek API");
                return "I received an empty response. Please try asking your question again.";
            }
            
            logger.error("API request failed with status: {}", responseEntity.getStatusCode());
            return "The interpretation service is currently unavailable. Please try again later.";
            
        } catch (Exception e) {
            logger.error("Error generating interpretation", e);
            return "An error occurred while generating the interpretation. Please try again later.";
        }
    }
    
    private String buildPrompt(String question, Map<String, Object> chartData) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are an expert astrologer with deep knowledge of Western astrology. ");
        prompt.append("Analyze the following birth chart and provide detailed, insightful interpretations. ");
        prompt.append("Focus on the specific question asked while considering the overall chart dynamics. ");
        prompt.append("Be precise with astrological terminology and explain complex concepts clearly.\n\n");
        
        prompt.append("Here is the birth chart data:\n\n");
        
        try {
            // Add birth date and gender
            Object birthDateTimeObj = chartData.get("birthDateTime");
            Object genderObj = chartData.get("gender");
            if (birthDateTimeObj != null) {
                prompt.append("Birth Date and Time: ").append(birthDateTimeObj).append("\n");
            }
            if (genderObj != null) {
                prompt.append("Gender: ").append(genderObj).append("\n");
            }
            prompt.append("\n");
            
            // Add chart data in a structured format
            prompt.append("Planet Positions:\n");
            Object planetsObj = chartData.get("planetPositions");
            if (planetsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> planets = (Map<String, Object>) planetsObj;
                planets.forEach((planet, data) -> {
                    if (data instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Object> positionData = (List<Object>) data;
                        if (!positionData.isEmpty()) {
                            Object positionObj = positionData.get(0);
                            double position;
                            if (positionObj instanceof Integer) {
                                position = ((Integer) positionObj).doubleValue();
                            } else if (positionObj instanceof Double) {
                                position = (Double) positionObj;
                            } else {
                                return; // Skip if not a number
                            }
                            int degree = (int) position;
                            int minute = (int) ((position % 1) * 60);
                            String sign = getZodiacSign(position);
                            prompt.append(String.format("%s: %d° %d' in %s\n",
                                planet,
                                degree,
                                minute,
                                sign
                            ));
                        }
                    }
                });
            }
            
            prompt.append("\nHouses:\n");
            Object housesObj = chartData.get("houses");
            if (housesObj instanceof List) {
                @SuppressWarnings("unchecked")
                List<Object> houses = (List<Object>) housesObj;
                for (int i = 0; i < houses.size(); i++) {
                    Object positionObj = houses.get(i);
                    double position;
                    if (positionObj instanceof Integer) {
                        position = ((Integer) positionObj).doubleValue();
                    } else if (positionObj instanceof Double) {
                        position = (Double) positionObj;
                    } else {
                        continue; // Skip if not a number
                    }
                    int degree = (int) position;
                    int minute = (int) ((position % 1) * 60);
                    String sign = getZodiacSign(position);
                    String houseName = getHouseName(i);
                    prompt.append(String.format("%s: %d° %d' in %s\n",
                        houseName,
                        degree,
                        minute,
                        sign
                    ));
                }
            }
            
            prompt.append("\nAspects:\n");
            Object aspectsObj = chartData.get("aspects");
            if (aspectsObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Double> aspects = (Map<String, Double>) aspectsObj;
                aspects.forEach((aspect, angle) -> {
                    prompt.append(String.format("%s: %.2f°\n", aspect, angle));
                });
            }
            
            prompt.append("\nQuestion: ").append(question);
            prompt.append("\n\nPlease provide a detailed interpretation of this chart, focusing on the specific question asked. ");
            prompt.append("Include relevant astrological concepts and explain how they relate to the question. ");
            prompt.append("Be specific about planetary positions, aspects, and house placements that are particularly significant.");
            
        } catch (Exception e) {
            logger.error("Error building prompt", e);
            return "An error occurred while processing the chart data. Please try again later.";
        }
        
        return prompt.toString();
    }
    
    private String getZodiacSign(double position) {
        String[] signs = {
            "Aries", "Taurus", "Gemini", "Cancer",
            "Leo", "Virgo", "Libra", "Scorpio",
            "Sagittarius", "Capricorn", "Aquarius", "Pisces"
        };
        int signIndex = (int) (position / 30) % 12;
        return signs[signIndex];
    }
    
    private String getHouseName(int index) {
        switch (index) {
            case 0: return "Ascendant";
            case 1: return "House 2";
            case 2: return "House 3";
            case 3: return "IC";
            case 4: return "House 5";
            case 5: return "House 6";
            case 6: return "Descendant";
            case 7: return "House 8";
            case 8: return "House 9";
            case 9: return "MC";
            case 10: return "House 11";
            case 11: return "House 12";
            default: return "House " + (index + 1);
        }
    }
} 