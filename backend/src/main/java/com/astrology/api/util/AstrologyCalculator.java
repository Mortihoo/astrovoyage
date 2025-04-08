package com.astrology.api.util;

import org.springframework.stereotype.Component;
import java.time.*;
import java.util.*;

@Component
public class AstrologyCalculator {
    // Zodiac signs
    private static final String[] ZODIAC_SIGNS = {
        "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
        "Libra", "Scorpio", "Sagittarius", "Capricorn", "Aquarius", "Pisces"
    };

    // Planet names
    private static final String[] PLANETS = {
        "Sun", "Moon", "Mercury", "Venus", "Mars", 
        "Jupiter", "Saturn", "Uranus", "Neptune", "Pluto"
    };

    // Orbital elements for planets (simplified)
    private static final double[] MEAN_MOTIONS = {
        0.985647324828,  // Sun
        13.176396489,    // Moon
        4.092338633,     // Mercury
        1.602130859,     // Venus
        0.524020645,     // Mars
        0.083091873,     // Jupiter
        0.033459837,     // Saturn
        0.011725806,     // Uranus
        0.005995147,     // Neptune
        0.003964472      // Pluto
    };

    // Semi-major axes (AU)
    private static final double[] SEMI_MAJOR_AXIS = {
        1.000000000,     // Sun
        0.002571,        // Moon
        0.387098310,     // Mercury
        0.723332484,     // Venus
        1.523679342,     // Mars
        5.202603191,     // Jupiter
        9.554909596,     // Saturn
        19.218446062,    // Uranus
        30.110386869,    // Neptune
        39.482117208     // Pluto
    };

    // Eccentricities
    private static final double[] ECCENTRICITIES = {
        0.016708634,     // Sun
        0.054900489,     // Moon
        0.205630692,     // Mercury
        0.006771882,     // Venus
        0.093405115,     // Mars
        0.048498007,     // Jupiter
        0.054151160,     // Saturn
        0.047167171,     // Uranus
        0.008585955,     // Neptune
        0.248808833      // Pluto
    };

    // Inclinations (degrees)
    private static final double[] INCLINATIONS = {
        0.0,       // Sun
        5.145,     // Moon
        7.005,     // Mercury
        3.395,     // Venus
        1.850,     // Mars
        1.304,     // Jupiter
        2.486,     // Saturn
        0.772,     // Uranus
        1.769,     // Neptune
        17.140     // Pluto
    };

    public Map<String, Object> calculateBirthChart(LocalDateTime birthDateTime, double latitude, double longitude) {
        Map<String, Object> chartData = new HashMap<>();
        
        // Calculate Julian Day
        double julianDay = calculateJulianDay(birthDateTime);
        
        // Calculate Ascendant (Rising Sign)
        double ascendant = calculateAscendant(birthDateTime, latitude, longitude);
        
        // Calculate house cusps
        double[] houses = calculateHouses(birthDateTime, latitude, longitude);
        Map<Integer, Double> houseMap = new HashMap<>();
        for (int i = 1; i <= 12; i++) {
            houseMap.put(i, houses[i]);
        }
        
        // Calculate angles
        Map<String, Double> angles = new HashMap<>();
        angles.put("Ascendant", ascendant);
        angles.put("Midheaven", (ascendant + 270) % 360);
        angles.put("Descendant", (ascendant + 180) % 360);
        angles.put("ImumCoeli", (ascendant + 90) % 360);
        
        // Calculate planetary positions
        Map<String, Map<String, Object>> planets = calculatePlanetaryPositions(birthDateTime);
        
        // Calculate aspects between planets
        List<Map<String, Object>> aspects = calculateAspects(planets);
        
        // Add zodiac signs information
        Map<String, Object> zodiacInfo = new HashMap<>();
        for (String sign : ZODIAC_SIGNS) {
            Map<String, Object> signInfo = new HashMap<>();
            signInfo.put("element", getElementForSign(sign));
            signInfo.put("quality", getQualityForSign(sign));
            zodiacInfo.put(sign, signInfo);
        }
        
        chartData.put("houses", houseMap);
        chartData.put("angles", angles);
        chartData.put("planets", planets);
        chartData.put("aspects", aspects);
        chartData.put("zodiacSigns", zodiacInfo);
        
        return chartData;
    }

    private String getElementForSign(String sign) {
        return switch (sign) {
            case "Aries", "Leo", "Sagittarius" -> "Fire";
            case "Taurus", "Virgo", "Capricorn" -> "Earth";
            case "Gemini", "Libra", "Aquarius" -> "Air";
            case "Cancer", "Scorpio", "Pisces" -> "Water";
            default -> "Unknown";
        };
    }

    private String getQualityForSign(String sign) {
        return switch (sign) {
            case "Aries", "Cancer", "Libra", "Capricorn" -> "Cardinal";
            case "Taurus", "Leo", "Scorpio", "Aquarius" -> "Fixed";
            case "Gemini", "Virgo", "Sagittarius", "Pisces" -> "Mutable";
            default -> "Unknown";
        };
    }

    private Map<String, Map<String, Object>> calculatePlanetaryPositions(LocalDateTime birthDateTime) {
        Map<String, Map<String, Object>> positions = new HashMap<>();
        double julianDay = calculateJulianDay(birthDateTime);
        double T = (julianDay - 2451545.0) / 36525.0; // Julian centuries since J2000.0
        
        for (int i = 0; i < PLANETS.length; i++) {
            Map<String, Object> position = new HashMap<>();
            
            // Calculate mean longitude
            double meanLongitude = calculateMeanLongitude(i, T);
            
            // Calculate eccentric anomaly
            double eccentricAnomaly = calculateEccentricAnomaly(i, meanLongitude, T);
            
            // Calculate true anomaly
            double trueAnomaly = calculateTrueAnomaly(i, eccentricAnomaly, T);
            
            // Calculate longitude
            double longitude = (meanLongitude + trueAnomaly) % 360;
            if (longitude < 0) longitude += 360;
            
            // Calculate speed (simplified)
            double speed = MEAN_MOTIONS[i] * (1 + ECCENTRICITIES[i] * Math.cos(Math.toRadians(trueAnomaly)));
            
            // Determine if planet is retrograde
            boolean isRetrograde = isRetrograde(i, T, meanLongitude, eccentricAnomaly);
            
            position.put("longitude", longitude);
            position.put("sign", getZodiacSign(longitude));
            position.put("degree", longitude % 30);
            position.put("minutes", (int)((longitude % 1) * 60));
            position.put("speed", speed);
            position.put("retrograde", isRetrograde);
            
            positions.put(PLANETS[i], position);
        }
        
        return positions;
    }

    protected boolean isRetrograde(int planetIndex, double T, double meanLongitude, double eccentricAnomaly) {
        // For Sun and Moon (never retrograde)
        if (planetIndex <= 1) {
            return false;
        }

        // Calculate positions at three points in time to determine motion
        double dt = 1.0; // One day interval
        double[] longitudes = new double[3];
        
        for (int i = 0; i < 3; i++) {
            double t = T + (i - 1) * dt/36525.0; // Convert days to centuries
            
            // Calculate mean elements for the planet
            double M = meanLongitude + MEAN_MOTIONS[planetIndex] * (i - 1) * dt;
            double E = solveKeplersEquation(M, ECCENTRICITIES[planetIndex]);
            double v = calculateTrueAnomaly(E, ECCENTRICITIES[planetIndex]);
            
            // Calculate heliocentric position
            double a = SEMI_MAJOR_AXIS[planetIndex];
            double e = ECCENTRICITIES[planetIndex];
            double r = a * (1 - e * Math.cos(E));
            double xh = r * Math.cos(v);
            double yh = r * Math.sin(v);
            
            // Calculate Earth's position
            double Me = meanLongitude + MEAN_MOTIONS[0] * (i - 1) * dt;
            double Ee = solveKeplersEquation(Me, ECCENTRICITIES[0]);
            double ve = calculateTrueAnomaly(Ee, ECCENTRICITIES[0]);
            double re = SEMI_MAJOR_AXIS[0] * (1 - ECCENTRICITIES[0] * Math.cos(Ee));
            double xe = re * Math.cos(ve);
            double ye = re * Math.sin(ve);
            
            // Calculate geocentric position
            double xg = xh - xe;
            double yg = yh - ye;
            
            // Calculate geocentric longitude
            longitudes[i] = Math.toDegrees(Math.atan2(yg, xg));
            if (longitudes[i] < 0) {
                longitudes[i] += 360;
            }
        }
        
        // Calculate daily motion
        double dailyMotion1 = normalizeDegrees(longitudes[1] - longitudes[0]);
        double dailyMotion2 = normalizeDegrees(longitudes[2] - longitudes[1]);
        
        // Planet is retrograde if both daily motions are negative
        return dailyMotion1 < 0 && dailyMotion2 < 0;
    }
    
    private double normalizeDegrees(double angle) {
        angle = angle % 360;
        if (angle > 180) {
            angle -= 360;
        } else if (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    protected double calculateEccentricAnomaly(int planetIndex, double meanLongitude, double T) {
        // Simplified calculation of eccentric anomaly using Newton's method
        double eccentricity = ECCENTRICITIES[planetIndex];
        double E = meanLongitude; // Initial guess
        
        for (int i = 0; i < 5; i++) { // 5 iterations should be sufficient
            double deltaE = (meanLongitude - (E - eccentricity * Math.sin(Math.toRadians(E)))) / 
                          (1 - eccentricity * Math.cos(Math.toRadians(E)));
            E += deltaE;
        }
        
        return E;
    }

    protected double calculateTrueAnomaly(int planetIndex, double eccentricAnomaly, double T) {
        double eccentricity = ECCENTRICITIES[planetIndex];
        double v = 2 * Math.atan(Math.sqrt((1 + eccentricity) / (1 - eccentricity)) * 
                                Math.tan(Math.toRadians(eccentricAnomaly) / 2));
        return Math.toDegrees(v);
    }

    protected double calculateMeanLongitude(int planetIndex, double T) {
        // Simplified mean longitude calculations
        // These are basic approximations - real calculations are much more complex
        switch (planetIndex) {
            case 0: // Sun
                return 280.46646 + 36000.76983 * T; // Approximation for the Sun
            case 1: // Moon
                return 218.3164477 + 481267.88123421 * T; // Approximation for the Moon
            default:
                // For other planets, use a simple approximation based on their orbital periods
                return (360 * T / getPlanetOrbitalPeriod(planetIndex)) % 360;
        }
    }

    private double getPlanetOrbitalPeriod(int planetIndex) {
        // Orbital periods in Earth years
        double[] periods = {1.0, 0.0748, 0.24, 0.615, 1.88, 11.86, 29.46, 84.01, 164.79, 248.09};
        return periods[planetIndex];
    }

    private List<Map<String, Object>> calculateAspects(Map<String, Map<String, Object>> planets) {
        List<Map<String, Object>> aspects = new ArrayList<>();
        
        // Define major aspects and their orbs
        double[][] majorAspects = {
            {0, 10},    // Conjunction
            {60, 6},    // Sextile
            {90, 8},    // Square
            {120, 8},   // Trine
            {180, 10}   // Opposition
        };

        String[] planetNames = planets.keySet().toArray(new String[0]);
        for (int i = 0; i < planetNames.length; i++) {
            for (int j = i + 1; j < planetNames.length; j++) {
                double long1 = (double) planets.get(planetNames[i]).get("longitude");
                double long2 = (double) planets.get(planetNames[j]).get("longitude");
                
                double distance = Math.abs(long1 - long2);
                if (distance > 180) distance = 360 - distance;
                
                for (double[] aspect : majorAspects) {
                    if (Math.abs(distance - aspect[0]) <= aspect[1]) {
                        Map<String, Object> aspectData = new HashMap<>();
                        aspectData.put("planet1", planetNames[i]);
                        aspectData.put("planet2", planetNames[j]);
                        aspectData.put("aspect", getAspectName(aspect[0]));
                        aspectData.put("orb", Math.abs(distance - aspect[0]));
                        aspectData.put("exact", Math.abs(distance - aspect[0]) < 1);
                        aspects.add(aspectData);
                    }
                }
            }
        }
        
        return aspects;
    }

    private double[] calculateHouses(LocalDateTime birthDateTime, double latitude, double longitude) {
        double[] houses = new double[13];
        
        // Calculate RAMC (Right Ascension of Midheaven)
        double julianDay = calculateJulianDay(birthDateTime);
        double RAMC = calculateRAMC(julianDay, longitude);
        
        // Calculate house cusps using a simplified Placidus system
        for (int i = 1; i <= 12; i++) {
            houses[i] = calculateHouseCusp(RAMC, latitude, i);
        }
        
        return houses;
    }

    private double calculateRAMC(double julianDay, double longitude) {
        double T = (julianDay - 2451545.0) / 36525.0;
        double theta0 = 280.46061837 + 360.98564736629 * (julianDay - 2451545.0) +
                       0.000387933 * T * T - T * T * T / 38710000.0;
        
        return (theta0 + longitude) % 360;
    }

    private double calculateHouseCusp(double RAMC, double latitude, int houseNumber) {
        // Simplified Placidus house cusp calculation
        double A = RAMC + (houseNumber - 1) * 30.0;
        return A % 360;
    }

    private String getZodiacSign(double longitude) {
        int signIndex = (int)(longitude / 30) % 12;
        return ZODIAC_SIGNS[signIndex];
    }

    private String getAspectName(double angle) {
        if (angle == 0) return "Conjunction";
        if (angle == 60) return "Sextile";
        if (angle == 90) return "Square";
        if (angle == 120) return "Trine";
        if (angle == 180) return "Opposition";
        return "Unknown";
    }

    protected double calculateJulianDay(LocalDateTime dateTime) {
        // Convert to UTC
        ZonedDateTime utcDateTime = dateTime.atZone(ZoneId.systemDefault())
                                          .withZoneSameInstant(ZoneOffset.UTC);
        
        int year = utcDateTime.getYear();
        int month = utcDateTime.getMonthValue();
        int day = utcDateTime.getDayOfMonth();
        double hour = utcDateTime.getHour() + 
                     utcDateTime.getMinute() / 60.0 + 
                     utcDateTime.getSecond() / 3600.0;

        if (month <= 2) {
            year--;
            month += 12;
        }

        int a = year / 100;
        int b = 2 - a + (a / 4);

        return (int)(365.25 * (year + 4716)) + 
               (int)(30.6001 * (month + 1)) + 
               day + hour/24.0 + b - 1524.5;
    }

    private double calculateAscendant(LocalDateTime birthDateTime, double latitude, double longitude) {
        // Simplified calculation of the Ascendant
        // This is a basic approximation - real calculations are much more complex
        double julianDay = calculateJulianDay(birthDateTime);
        double T = (julianDay - 2451545.0) / 36525.0; // Julian centuries since J2000.0
        
        // Local Sidereal Time calculation (simplified)
        double LST = 100.46 + 0.985647 * julianDay + longitude + 15 * birthDateTime.getHour();
        LST = LST % 360;
        if (LST < 0) LST += 360;
        
        // Simple ascendant calculation
        double ascendant = LST + Math.atan2(Math.cos(Math.toRadians(LST)), 
                                          Math.cos(Math.toRadians(latitude)) * Math.sin(Math.toRadians(LST)));
        
        return (ascendant + 360) % 360;
    }

    private double solveKeplersEquation(double M, double e) {
        // Normalize mean anomaly
        M = Math.toRadians(M % 360);
        if (M < 0) M += 2 * Math.PI;
        
        // Initial guess
        double E = M;
        
        // Newton-Raphson iteration
        for (int i = 0; i < 10; i++) {
            double dE = (E - e * Math.sin(E) - M) / (1 - e * Math.cos(E));
            E -= dE;
            if (Math.abs(dE) < 1e-12) break;
        }
        
        return E;
    }
    
    private double calculateTrueAnomaly(double E, double e) {
        double cosE = Math.cos(E);
        double sinE = Math.sin(E);
        double cosv = (cosE - e) / (1 - e * cosE);
        double sinv = (Math.sqrt(1 - e * e) * sinE) / (1 - e * cosE);
        return Math.atan2(sinv, cosv);
    }
} 