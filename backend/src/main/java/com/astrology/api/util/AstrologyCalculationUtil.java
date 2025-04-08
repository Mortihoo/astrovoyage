package com.astrology.api.util;

import java.util.HashMap;
import java.util.Map;

public class AstrologyCalculationUtil {
    
    public static Map<String, String> calculateElements(Map<String, String> planetSigns) {
        Map<String, String> elements = new HashMap<>();
        for (Map.Entry<String, String> entry : planetSigns.entrySet()) {
            String planet = entry.getKey();
            String sign = entry.getValue();
            if (sign != null) {
                if (sign.contains("Aries") || sign.contains("Leo") || sign.contains("Sagittarius")) {
                    elements.put("Fire", elements.getOrDefault("Fire", "") + planet + ", ");
                } else if (sign.contains("Taurus") || sign.contains("Virgo") || sign.contains("Capricorn")) {
                    elements.put("Earth", elements.getOrDefault("Earth", "") + planet + ", ");
                } else if (sign.contains("Gemini") || sign.contains("Libra") || sign.contains("Aquarius")) {
                    elements.put("Air", elements.getOrDefault("Air", "") + planet + ", ");
                } else if (sign.contains("Cancer") || sign.contains("Scorpio") || sign.contains("Pisces")) {
                    elements.put("Water", elements.getOrDefault("Water", "") + planet + ", ");
                }
            }
        }
        return elements;
    }

    public static Map<String, String> calculateModalities(Map<String, String> planetSigns) {
        Map<String, String> modalities = new HashMap<>();
        for (Map.Entry<String, String> entry : planetSigns.entrySet()) {
            String planet = entry.getKey();
            String sign = entry.getValue();
            if (sign != null) {
                if (sign.contains("Aries") || sign.contains("Cancer") || sign.contains("Libra") || sign.contains("Capricorn")) {
                    modalities.put("Cardinal", modalities.getOrDefault("Cardinal", "") + planet + ", ");
                } else if (sign.contains("Taurus") || sign.contains("Leo") || sign.contains("Scorpio") || sign.contains("Aquarius")) {
                    modalities.put("Fixed", modalities.getOrDefault("Fixed", "") + planet + ", ");
                } else if (sign.contains("Gemini") || sign.contains("Virgo") || sign.contains("Sagittarius") || sign.contains("Pisces")) {
                    modalities.put("Mutable", modalities.getOrDefault("Mutable", "") + planet + ", ");
                }
            }
        }
        return modalities;
    }
} 