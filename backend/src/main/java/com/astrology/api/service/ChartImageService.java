package com.astrology.api.service;

import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.Map;

@Service
public class ChartImageService {
    private static final String ASTRO_SEEK_URL = "https://horoscopes.astro-seek.com/horoscope-chart4def-700__radix_1-1-1970_00-00.png";

    public String generateChartImageUrl(Map<String, Object> chartData) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(ASTRO_SEEK_URL);

        // Add fixed parameters
        builder.queryParam("fortune_asp", 1)
               .queryParam("vertex_asp", 1)
               .queryParam("chiron_asp", 1)
               .queryParam("lilith_asp", 1)
               .queryParam("uzel_asp", 1)
               .queryParam("tolerance", 1)
               .queryParam("tolerance_paral", 1.2)
               .queryParam("domy_cisla", 0)
               .queryParam("barva_planet", 0)
               .queryParam("barva_stupne", 0)
               .queryParam("barva_pozadi", 0)
               .queryParam("barva_domy", 1)
               .queryParam("barva_vzduch", 1);

        // Add house cusps
        @SuppressWarnings("unchecked")
        Map<String, Double> houses = (Map<String, Double>) chartData.get("houses");
        if (houses != null) {
            builder.queryParam("dum_1", houses.get("house1"))
                   .queryParam("dum_2", houses.get("house2"))
                   .queryParam("dum_3", houses.get("house3"))
                   .queryParam("dum_4", houses.get("house4"))
                   .queryParam("dum_5", houses.get("house5"))
                   .queryParam("dum_6", houses.get("house6"))
                   .queryParam("dum_7", houses.get("house7"))
                   .queryParam("dum_8", houses.get("house8"))
                   .queryParam("dum_9", houses.get("house9"))
                   .queryParam("dum_10", houses.get("house10"))
                   .queryParam("dum_11", houses.get("house11"))
                   .queryParam("dum_12", houses.get("house12"));
        }

        // Add planets
        @SuppressWarnings("unchecked")
        Map<String, Map<String, Object>> planets = (Map<String, Map<String, Object>>) chartData.get("planets");
        if (planets != null) {
            // Map our planet names to astro-seek parameter names
            Map<String, String> planetParams = Map.of(
                "Sun", "p_slunce",
                "Moon", "p_luna",
                "Mercury", "p_merkur",
                "Venus", "p_venuse",
                "Mars", "p_mars",
                "Jupiter", "p_jupiter",
                "Saturn", "p_saturn",
                "Uranus", "p_uran",
                "Neptune", "p_neptun",
                "Pluto", "p_pluto"
            );

            planets.forEach((planet, data) -> {
                String paramName = planetParams.get(planet);
                if (paramName != null) {
                    Double position = (Double) data.get("position");
                    if (position != null) {
                        builder.queryParam(paramName, position);
                    }
                }
            });
        }

        // Add special points
        @SuppressWarnings("unchecked")
        Map<String, Double> points = (Map<String, Double>) chartData.get("points");
        if (points != null) {
            builder.queryParam("p_vertex", points.get("vertex"))
                   .queryParam("p_fortune", points.get("fortune"))
                   .queryParam("p_spirit", points.get("spirit"));
        }

        // Add retrograde planets
        builder.queryParam("r_saturn", "ANO")
               .queryParam("r_pluto", "ANO")
               .queryParam("r_uzel", "ANO");

        return builder.build().toUriString();
    }
} 