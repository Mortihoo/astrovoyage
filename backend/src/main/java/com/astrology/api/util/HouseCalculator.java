package com.astrology.api.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HouseCalculator {
    private static final Logger logger = LoggerFactory.getLogger(HouseCalculator.class);

    private static final double DEGREES_PER_HOUR = 15.0;
    private static final double DEGREES_PER_MINUTE = 0.25;
    private static final double DEGREES_PER_SECOND = 0.004166666666666667;
    
    public static double calculateAscendant(LocalDateTime birthDateTime, double latitude, double longitude) {
        // Convert birth time to Julian Day
        double julianDay = calculateJulianDay(birthDateTime);
        
        // Calculate sidereal time
        double siderealTime = calculateSiderealTime(julianDay, longitude);
        
        // Calculate declination
        double declination = calculateDeclination(julianDay);
        
        // Calculate ascendant
        double ascendant = calculateAscendantFromSiderealTime(siderealTime, latitude, declination);
        
        return normalizeAngle(ascendant);
    }
    
    public static double[] calculateHouseCusps(double ascendant, double latitude) {
        double[] cusps = new double[12];
        
        // Calculate houses using Placidus house system
        for (int i = 0; i < 12; i++) {
            double houseAngle = (ascendant + i * 30.0) % 360.0;
            cusps[i] = calculatePlacidusCusp(houseAngle, latitude);
        }
        
        return cusps;
    }
    
    private static double calculateJulianDay(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        
        // Convert to Julian Day
        double a = Math.floor((14 - month) / 12);
        double y = year + 4800 - a;
        double m = month + 12 * a - 3;
        
        double jd = day + Math.floor((153 * m + 2) / 5) + 365 * y + Math.floor(y / 4) 
                  - Math.floor(y / 100) + Math.floor(y / 400) - 32045;
        
        // Add time component
        double time = (hour + minute / 60.0 + second / 3600.0) / 24.0;
        return jd + time;
    }
    
    private static double calculateSiderealTime(double julianDay, double longitude) {
        // Calculate Greenwich sidereal time
        double t = (julianDay - 2451545.0) / 36525.0;
        double gmst = 280.46061837 + 360.98564736629 * (julianDay - 2451545.0)
                    + 0.000387933 * t * t - t * t * t / 38710000.0;
        
        // Convert to local sidereal time
        double lst = gmst + longitude;
        
        return normalizeAngle(lst);
    }
    
    private static double calculateDeclination(double julianDay) {
        // Calculate obliquity of the ecliptic (Earth's axial tilt)
        double t = (julianDay - 2451545.0) / 36525.0;
        double obliquity = 23.4392911 - 0.0130042 * t - 1.64e-7 * t * t + 5.04e-7 * t * t * t;
        
        // Calculate Sun's position in ecliptic
        double L = 280.46646 + 36000.76983 * t + 0.0003032 * t * t;
        L = normalizeAngle(L);
        
        // Calculate Sun's declination
        double declination = Math.asin(Math.sin(obliquity * Math.PI / 180.0) * 
                                     Math.sin(L * Math.PI / 180.0)) * 180.0 / Math.PI;
        
        return declination;
    }
    
    private static double calculateAscendantFromSiderealTime(double siderealTime, double latitude, double declination) {
        // Calculate ascendant
        double ascendant = Math.atan2(
            Math.cos(siderealTime * Math.PI / 180.0),
            -Math.sin(siderealTime * Math.PI / 180.0) * Math.sin(latitude * Math.PI / 180.0)
            + Math.tan(declination * Math.PI / 180.0) * Math.cos(latitude * Math.PI / 180.0)
        ) * 180.0 / Math.PI;
        
        return normalizeAngle(ascendant);
    }
    
    private static double calculatePlacidusCusp(double houseAngle, double latitude) {
        // Calculate Placidus house system
        double cusp = Math.atan2(
            Math.tan(houseAngle * Math.PI / 180.0),
            Math.cos(latitude * Math.PI / 180.0)
        ) * 180.0 / Math.PI;
        
        return normalizeAngle(cusp);
    }
    
    private static double normalizeAngle(double angle) {
        angle = angle % 360;
        if (angle < 0) {
            angle += 360;
        }
        return angle;
    }

    public static double[] calculateHouses(double julianDay, double latitude, double longitude) {
        // Calculate Greenwich sidereal time
        double lst = calculateLST(julianDay, longitude);
        
        // Calculate house cusps using Placidus system
        double[] houses = new double[12];
        
        // RAMC (Right Ascension of Midheaven)
        double ramc = lst * 15.0; // Convert hours to degrees
        houses[9] = ramc; // Midheaven (10th house)
        
        // Calculate other house cusps
        for (int i = 0; i < 12; i++) {
            // Simple equal house system: each house is 30 degrees
            houses[i] = (ramc + (i * 30.0)) % 360.0;
        }
        
        return houses;
    }
    
    private static double calculateLST(double julianDay, double longitude) {
        // Calculate Greenwich sidereal time
        double t = (julianDay - 2451545.0) / 36525.0;
        double gmst = 280.46061837 + 360.98564736629 * (julianDay - 2451545.0) +
                     t * t * (0.000387933 - t / 38710000.0);
        
        // Normalize to [0, 360)
        gmst = (gmst + 360.0) % 360.0;
        
        // Convert to hours
        double gmsth = gmst / 15.0;
        
        // Add local longitude (in hours)
        double lst = gmsth + (longitude / 15.0);
        
        // Normalize to [0, 24)
        lst = (lst + 24.0) % 24.0;
        
        return lst;
    }
} 