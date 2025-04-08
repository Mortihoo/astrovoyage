package com.astrology.api.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;

public class AstrologyCalculatorTest {
    private final AstrologyCalculator calculator = new AstrologyCalculator();

    @Test
    public void testMercuryRetrograde() {
        // Test Mercury retrograde during inferior conjunction
        LocalDateTime testDate = LocalDateTime.of(2023, 12, 13, 0, 0);
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Mercury's position
        double meanLongitude = calculator.calculateMeanLongitude(2, T); // Mercury index is 2
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(2, meanLongitude, T);
        
        // Mercury should be retrograde during this period
        assertTrue(calculator.isRetrograde(2, T, meanLongitude, eccentricAnomaly));
    }

    @Test
    public void testVenusRetrograde() {
        // Test Venus retrograde during inferior conjunction
        LocalDateTime testDate = LocalDateTime.of(2023, 8, 13, 0, 0);
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Venus's position
        double meanLongitude = calculator.calculateMeanLongitude(3, T); // Venus index is 3
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(3, meanLongitude, T);
        
        // Venus should be retrograde during this period
        assertTrue(calculator.isRetrograde(3, T, meanLongitude, eccentricAnomaly));
    }

    @Test
    public void testMarsRetrograde() {
        // Test Mars retrograde during opposition
        LocalDateTime testDate = LocalDateTime.of(2022, 12, 8, 0, 0);
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Mars's position
        double meanLongitude = calculator.calculateMeanLongitude(4, T); // Mars index is 4
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(4, meanLongitude, T);
        
        // Mars should be retrograde during this period
        assertTrue(calculator.isRetrograde(4, T, meanLongitude, eccentricAnomaly));
    }

    @Test
    public void testJupiterRetrograde() {
        // Test Jupiter retrograde during opposition
        LocalDateTime testDate = LocalDateTime.of(2023, 11, 3, 0, 0);
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Jupiter's position
        double meanLongitude = calculator.calculateMeanLongitude(5, T); // Jupiter index is 5
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(5, meanLongitude, T);
        
        // Jupiter should be retrograde during this period
        assertTrue(calculator.isRetrograde(5, T, meanLongitude, eccentricAnomaly));
    }

    @Test
    public void testSaturnRetrograde() {
        // Test Saturn retrograde during opposition
        LocalDateTime testDate = LocalDateTime.of(2023, 8, 27, 0, 0);
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Saturn's position
        double meanLongitude = calculator.calculateMeanLongitude(6, T); // Saturn index is 6
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(6, meanLongitude, T);
        
        // Saturn should be retrograde during this period
        assertTrue(calculator.isRetrograde(6, T, meanLongitude, eccentricAnomaly));
    }

    @Test
    public void testSunNeverRetrograde() {
        // Test that Sun is never retrograde
        LocalDateTime testDate = LocalDateTime.now();
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Sun's position
        double meanLongitude = calculator.calculateMeanLongitude(0, T); // Sun index is 0
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(0, meanLongitude, T);
        
        // Sun should never be retrograde
        assertFalse(calculator.isRetrograde(0, T, meanLongitude, eccentricAnomaly));
    }

    @Test
    public void testMoonNeverRetrograde() {
        // Test that Moon is never retrograde
        LocalDateTime testDate = LocalDateTime.now();
        double julianDay = calculator.calculateJulianDay(testDate);
        double T = (julianDay - 2451545.0) / 36525.0;
        
        // Calculate Moon's position
        double meanLongitude = calculator.calculateMeanLongitude(1, T); // Moon index is 1
        double eccentricAnomaly = calculator.calculateEccentricAnomaly(1, meanLongitude, T);
        
        // Moon should never be retrograde
        assertFalse(calculator.isRetrograde(1, T, meanLongitude, eccentricAnomaly));
    }
} 