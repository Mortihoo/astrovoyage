package com.astrology.api.service;

import com.astrology.api.dto.BirthChartRequest;
import com.astrology.api.model.BirthChart;
import com.astrology.api.model.BirthData;
import com.astrology.api.model.City;
import com.astrology.api.repository.BirthChartRepository;
import com.astrology.api.util.AstrologyCalculator;
import com.astrology.api.util.JPLDE405Reader;
import com.astrology.api.util.HouseCalculator;
import com.astrology.api.util.SwissEphemerisCalculator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BirthChartService {
    
    private static final Logger logger = LoggerFactory.getLogger(BirthChartService.class);

    private final BirthChartRepository birthChartRepository;
    private final AstrologyCalculator astrologyCalculator;
    private final ObjectMapper objectMapper;
    private final JPLDE405Reader de405Reader;
    private final Map<String, List<City>> cities;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    public BirthChartService(BirthChartRepository birthChartRepository,
                            AstrologyCalculator astrologyCalculator,
                            ObjectMapper objectMapper,
                            Map<String, List<City>> cities) throws IOException {
        this.birthChartRepository = birthChartRepository;
        this.astrologyCalculator = astrologyCalculator;
        this.objectMapper = objectMapper;
        this.de405Reader = new JPLDE405Reader();
        this.de405Reader.open();
        this.cities = cities;
    }

    public BirthChart generateBirthChart(BirthChartRequest request) {
        logger.info("Generating birth chart for: {}", request);
        BirthChart birthChart = new BirthChart();
        birthChart.setName(request.getName());
        birthChart.setBirthDateTime(request.getBirthDateTime().format(DATE_TIME_FORMATTER));
        birthChart.setLocation(request.getLocation());
        birthChart.setGender(request.getGender());
        
        // Parse location string to get city and country
        String[] locationParts = request.getLocation().split(", ");
        if (locationParts.length != 2) {
            throw new IllegalArgumentException("Invalid location format. Expected format: 'City, Country'");
        }
        
        String city = locationParts[0];
        String country = locationParts[1];
        
        // Get latitude and longitude from cities data
        double latitude = 0.0;
        double longitude = 0.0;
        boolean found = false;
        
        // Find the city in the cities data
        List<City> countryCities = this.cities.get(country);
        if (countryCities != null) {
            for (City c : countryCities) {
                if (c.getName().equals(city)) {
                    latitude = c.getLat();
                    longitude = c.getLng();
                    found = true;
                    break;
                }
            }
        }
        
        if (!found) {
            throw new IllegalArgumentException("Location not found in database: " + request.getLocation());
        }
        
        logger.info("Using latitude: {}, longitude: {} for location: {}", latitude, longitude, request.getLocation());
        
        // Create BirthData object
        BirthData birthData = new BirthData();
        birthData.setBirthDateTime(request.getBirthDateTime());
        birthData.setTimezone(request.getTimezone());
        birthData.setLatitude(latitude);
        birthData.setLongitude(longitude);
        
        try {
            // Calculate complete chart data
            BirthChart calculatedChart = calculateBirthChart(birthData);
            
            // Set all calculated data
            birthChart.setPlanetPositions(calculatedChart.getPlanetPositions());
            birthChart.setPlanetSpeeds(calculatedChart.getPlanetSpeeds());
            birthChart.setHouses(calculatedChart.getHouses());
            birthChart.setAspects(calculatedChart.getAspects());
            birthChart.setAspectsDetails(calculatedChart.getAspectsDetails());
            birthChart.setElements(calculatedChart.getElements());
            birthChart.setModalities(calculatedChart.getModalities());
            birthChart.setSigns(calculatedChart.getSigns());
            birthChart.setHousesSigns(calculatedChart.getHousesSigns());
            birthChart.setPlanetSigns(calculatedChart.getPlanetSigns());
            
            // Convert chart data to JSON string
            String chartDataJson = objectMapper.writeValueAsString(calculatedChart);
            birthChart.setChartData(chartDataJson);
            
            // Generate interpretation based on chart data
            String interpretation = generateInterpretation(calculatedChart);
            birthChart.setInterpretation(interpretation);
            
            return birthChartRepository.save(birthChart);
        } catch (IOException e) {
            logger.error("Error calculating birth chart", e);
            throw new RuntimeException("Error calculating birth chart", e);
        }
    }

    private String generateInterpretation(BirthChart chart) {
        StringBuilder interpretation = new StringBuilder();
        
        // Get basic chart information
        Map<String, String> planetSigns = chart.getPlanetSigns();
        
        // Generate sun sign interpretation
        String sunSign = planetSigns.get("Sun");
        if (sunSign != null) {
            interpretation.append(String.format(
                "Your Sun is in %s, indicating that your core personality and life force are expressed through %s qualities. ",
                sunSign, getSignQualities(sunSign)
            ));
        } else {
            logger.warn("Sun sign is missing from chart data");
        }
        
        // Generate moon sign interpretation
        String moonSign = planetSigns.get("Moon");
        if (moonSign != null) {
            interpretation.append(String.format(
                "Your Moon is in %s, suggesting that your emotional nature and inner self resonate with %s characteristics. ",
                moonSign, getSignQualities(moonSign)
            ));
        } else {
            logger.warn("Moon sign is missing from chart data");
        }
        
        // Generate ascendant interpretation
        Map<String, String> housesSigns = chart.getHousesSigns();
        String ascendant = housesSigns.get("House1");
        if (ascendant != null) {
            interpretation.append(String.format(
                "With %s rising, you present yourself to the world with %s traits. ",
                ascendant, getSignQualities(ascendant)
            ));
        } else {
            logger.warn("Ascendant is missing from chart data");
        }
        
        // Add aspect interpretations
        Map<String, Double> aspects = chart.getAspects();
        if (aspects != null && !aspects.isEmpty()) {
            interpretation.append("\n\nSignificant planetary aspects in your chart:\n");
            for (Map.Entry<String, Double> entry : aspects.entrySet()) {
                String[] planets = entry.getKey().split("-");
                String planet1 = planets[0];
                String planet2 = planets[1];
                double orb = entry.getValue();
                String aspectType = determineAspectType(orb);
                
                interpretation.append(String.format(
                    "- %s %s %s (orb: %.1f°): %s\n",
                    planet1, aspectType, planet2, orb,
                    getAspectInterpretation(planet1, aspectType, planet2)
                ));
            }
        }
        
        return interpretation.toString();
    }

    private String getSignQualities(String sign) {
        return switch (sign) {
            case "Aries" -> "assertive, energetic, and pioneering";
            case "Taurus" -> "stable, practical, and sensual";
            case "Gemini" -> "versatile, curious, and communicative";
            case "Cancer" -> "nurturing, emotional, and protective";
            case "Leo" -> "creative, confident, and dramatic";
            case "Virgo" -> "analytical, practical, and detail-oriented";
            case "Libra" -> "harmonious, diplomatic, and relationship-oriented";
            case "Scorpio" -> "intense, transformative, and deep";
            case "Sagittarius" -> "adventurous, philosophical, and optimistic";
            case "Capricorn" -> "ambitious, disciplined, and responsible";
            case "Aquarius" -> "innovative, independent, and humanitarian";
            case "Pisces" -> "intuitive, compassionate, and artistic";
            default -> "mysterious and unique";
        };
    }

    private String getAspectInterpretation(String planet1, String aspectType, String planet2) {
        return switch (aspectType) {
            case "Conjunction" -> 
                String.format("The energies of %s and %s blend and intensify each other", planet1, planet2);
            case "Sextile" ->
                String.format("There is a harmonious flow of energy between %s and %s", planet1, planet2);
            case "Square" ->
                String.format("There is dynamic tension between %s and %s, promoting growth through challenge", 
                            planet1, planet2);
            case "Trine" ->
                String.format("There is a natural, flowing harmony between %s and %s", planet1, planet2);
            case "Opposition" ->
                String.format("There is a dynamic polarity between %s and %s, calling for balance", 
                            planet1, planet2);
            default -> 
                String.format("The relationship between %s and %s is complex", planet1, planet2);
        };
    }

    public BirthChart getBirthChart(Long id) {
        return birthChartRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Birth chart not found"));
    }

    public BirthChart calculateBirthChart(BirthData birthData) throws IOException {
        // Convert local time to UTC
        LocalDateTime localDateTime = birthData.getBirthDateTime();
        ZoneId zoneId = ZoneId.of(birthData.getTimezone());
        LocalDateTime utcDateTime = localDateTime.atZone(zoneId).withZoneSameInstant(ZoneId.of("UTC")).toLocalDateTime();

        // Calculate planetary positions using Swiss Ephemeris
        Map<String, double[]> planetPositions = SwissEphemerisCalculator.calculatePlanetPositions(
            utcDateTime, birthData.getLatitude(), birthData.getLongitude());

        // Calculate house positions
        double[] houses = SwissEphemerisCalculator.calculateHouses(
            utcDateTime, birthData.getLatitude(), birthData.getLongitude());

        // Create birth chart object
        BirthChart birthChart = new BirthChart(planetPositions, houses);
        
        // Calculate aspects
        Map<String, Double> aspects = calculateAspects(planetPositions);
        birthChart.setAspects(aspects);

        // Calculate aspect details
        Map<String, String> aspectsDetails = calculateAspectDetails(aspects);
        birthChart.setAspectsDetails(aspectsDetails);

        // Set zodiac ranges
        Map<String, String> signs = new HashMap<>();
        signs.put("Aries", "0-30");
        signs.put("Taurus", "30-60");
        signs.put("Gemini", "60-90");
        signs.put("Cancer", "90-120");
        signs.put("Leo", "120-150");
        signs.put("Virgo", "150-180");
        signs.put("Libra", "180-210");
        signs.put("Scorpio", "210-240");
        signs.put("Sagittarius", "240-270");
        signs.put("Capricorn", "270-300");
        signs.put("Aquarius", "300-330");
        signs.put("Pisces", "330-360");
        birthChart.setSigns(signs);

        // Calculate planet signs and retrograde status
        Map<String, String> planetSigns = new HashMap<>();
        Map<String, Double> planetSpeeds = new HashMap<>();
        for (Map.Entry<String, double[]> entry : planetPositions.entrySet()) {
            String planet = entry.getKey();
            double[] position = entry.getValue();
            double longitude = position[0];
            double speed = position[3];  // Use the 4th element (index 3) as speed
            String sign = determineSign(longitude, signs);
            String status = speed < 0 ? " (R)" : "";  // R indicates retrograde
            planetSigns.put(planet, sign + status);
            planetSpeeds.put(planet, speed);
        }
        birthChart.setPlanetSigns(planetSigns);
        birthChart.setPlanetSpeeds(planetSpeeds);

        // Calculate house signs
        Map<String, String> housesSigns = calculateHouseSigns(houses, signs);
        birthChart.setHousesSigns(housesSigns);

        // Calculate element distribution
        Map<String, String> elements = calculateElements(planetSigns);
        birthChart.setElements(elements);

        // Calculate mode distribution
        Map<String, String> modalities = calculateModalities(planetSigns);
        birthChart.setModalities(modalities);

        return birthChart;
    }

    private double calculateJulianDay(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();

        // Convert to Julian Day
        double a = (14 - month) / 12;
        double y = year + 4800 - a;
        double m = month + 12 * a - 3;

        double jd = day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045;
        double time = (hour + minute / 60.0 + second / 3600.0) / 24.0;

        return jd + time;
    }

    private Map<String, Double> calculateAspects(Map<String, double[]> planetPositions) {
        Map<String, Double> aspects = new HashMap<>();
        
        // Calculate aspects between planets
        String[] planets = {"SUN", "MOON", "MERCURY", "VENUS", "MARS", 
                          "JUPITER", "SATURN", "URANUS", "NEPTUNE", "PLUTO"};
        
        for (int i = 0; i < planets.length; i++) {
            for (int j = i + 1; j < planets.length; j++) {
                String planet1 = planets[i];
                String planet2 = planets[j];
                double[] pos1 = planetPositions.get(planet1);
                double[] pos2 = planetPositions.get(planet2);
                
                if (pos1 != null && pos2 != null) {
                    double angle = calculateAngle(pos1, pos2);
                    
                    // Only record major aspects
                    if (isMajorAspect(angle)) {
                        aspects.put(planet1 + "-" + planet2, angle);
                    }
                }
            }
        }
        
        return aspects;
    }

    private double calculateAngle(double[] pos1, double[] pos2) {
        // Calculate angle between two positions
        double x1 = pos1[0];
        double y1 = pos1[1];
        double x2 = pos2[0];
        double y2 = pos2[1];
        
        double angle = Math.atan2(y2 - y1, x2 - x1) * 180.0 / Math.PI;
        if (angle < 0) {
            angle += 360.0;
        }
        return angle;
    }

    private boolean isMajorAspect(double angle) {
        // Check if angle is a major aspect (0°, 60°, 90°, 120°, 180°)
        double[] majorAspects = {0, 60, 90, 120, 180};
        for (double aspect : majorAspects) {
            if (Math.abs(angle - aspect) < 5) { // Allow 5 degrees orb
                return true;
            }
        }
        return false;
    }

    private Map<String, String> calculateAspectDetails(Map<String, Double> aspects) {
        Map<String, String> details = new HashMap<>();
        
        for (Map.Entry<String, Double> entry : aspects.entrySet()) {
            double angle = entry.getValue();
            String aspectType = determineAspectType(angle);
            details.put(entry.getKey(), aspectType);
        }
        
        return details;
    }

    private String determineAspectType(double angle) {
        if (angle < 5) return "conjunction";
        if (Math.abs(angle - 60) < 5) return "sextile";
        if (Math.abs(angle - 90) < 5) return "square";
        if (Math.abs(angle - 120) < 5) return "trine";
        if (Math.abs(angle - 180) < 5) return "opposition";
        return "unknown";
    }

    private Map<String, String> calculateHouseSigns(double[] houses, Map<String, String> signs) {
        Map<String, String> houseSigns = new HashMap<>();
        
        for (int i = 0; i < houses.length; i++) {
            double position = houses[i];
            String sign = determineSign(position, signs);
            houseSigns.put("House" + (i + 1), sign);
        }
        
        return houseSigns;
    }

    private String determineSign(double position, Map<String, String> signs) {
        for (Map.Entry<String, String> entry : signs.entrySet()) {
            String[] range = entry.getValue().split("-");
            double start = Double.parseDouble(range[0]);
            double end = Double.parseDouble(range[1]);
            if (position >= start && position < end) {
                return entry.getKey();
            }
        }
        return "Unknown";
    }

    private Map<String, String> calculateElements(Map<String, String> planetSigns) {
        Map<String, String> elements = new HashMap<>();
        for (Map.Entry<String, String> entry : planetSigns.entrySet()) {
            String planet = entry.getKey();
            String sign = entry.getValue();
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
        return elements;
    }

    private Map<String, String> calculateModalities(Map<String, String> planetSigns) {
        Map<String, String> modalities = new HashMap<>();
        for (Map.Entry<String, String> entry : planetSigns.entrySet()) {
            String planet = entry.getKey();
            String sign = entry.getValue();
            if (sign.contains("Aries") || sign.contains("Cancer") || sign.contains("Libra") || sign.contains("Capricorn")) {
                modalities.put("Cardinal", modalities.getOrDefault("Cardinal", "") + planet + ", ");
            } else if (sign.contains("Taurus") || sign.contains("Leo") || sign.contains("Scorpio") || sign.contains("Aquarius")) {
                modalities.put("Fixed", modalities.getOrDefault("Fixed", "") + planet + ", ");
            } else if (sign.contains("Gemini") || sign.contains("Virgo") || sign.contains("Sagittarius") || sign.contains("Pisces")) {
                modalities.put("Mutable", modalities.getOrDefault("Mutable", "") + planet + ", ");
            }
        }
        return modalities;
    }
} 