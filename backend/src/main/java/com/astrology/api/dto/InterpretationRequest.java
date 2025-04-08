package com.astrology.api.dto;

import lombok.Data;
import java.util.Map;

@Data
public class InterpretationRequest {
    private String question;
    private Map<String, Object> chartData;
} 