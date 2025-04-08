package com.astrology.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeepSeekRequest {
    private String prompt;
    private String model;
    private double temperature;
    private int maxTokens;
    private List<Message> messages;
    
    @Data
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
} 