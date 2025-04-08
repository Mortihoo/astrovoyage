package com.astrology.api.dto;

import lombok.Data;
import java.util.List;

@Data
public class DeepSeekResponse {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    public static class Choice {
        private int index;
        private String text;
        private String finishReason;
    }
    
    @Data
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }
} 