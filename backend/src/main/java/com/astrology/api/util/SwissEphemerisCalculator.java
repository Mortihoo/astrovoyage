package com.astrology.api.util;

import swisseph.SweDate;
import swisseph.SwissEph;
import swisseph.SweConst;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwissEphemerisCalculator {
    private static final Logger logger = LoggerFactory.getLogger(SwissEphemerisCalculator.class);
    private static final SwissEph swissEph = new SwissEph();
    
    // Planet index constants
    private static final int SUN = SweConst.SE_SUN;
    private static final int MOON = SweConst.SE_MOON;
    private static final int MERCURY = SweConst.SE_MERCURY;
    private static final int VENUS = SweConst.SE_VENUS;
    private static final int MARS = SweConst.SE_MARS;
    private static final int JUPITER = SweConst.SE_JUPITER;
    private static final int SATURN = SweConst.SE_SATURN;
    private static final int URANUS = SweConst.SE_URANUS;
    private static final int NEPTUNE = SweConst.SE_NEPTUNE;
    private static final int PLUTO = SweConst.SE_PLUTO;
    
    public static Map<String, double[]> calculatePlanetPositions(LocalDateTime dateTime, double latitude, double longitude) {
        Map<String, double[]> positions = new HashMap<>();
        
        // Convert to Julian Day
        SweDate sweDate = convertToSweDate(dateTime);
        
        // Calculate planetary positions
        positions.put("SUN", calculatePlanetPosition(sweDate, SUN));
        positions.put("MOON", calculatePlanetPosition(sweDate, MOON));
        positions.put("MERCURY", calculatePlanetPosition(sweDate, MERCURY));
        positions.put("VENUS", calculatePlanetPosition(sweDate, VENUS));
        positions.put("MARS", calculatePlanetPosition(sweDate, MARS));
        positions.put("JUPITER", calculatePlanetPosition(sweDate, JUPITER));
        positions.put("SATURN", calculatePlanetPosition(sweDate, SATURN));
        positions.put("URANUS", calculatePlanetPosition(sweDate, URANUS));
        positions.put("NEPTUNE", calculatePlanetPosition(sweDate, NEPTUNE));
        positions.put("PLUTO", calculatePlanetPosition(sweDate, PLUTO));
        
        return positions;
    }
    
    public static double[] calculateHouses(LocalDateTime dateTime, double latitude, double longitude) {
        SweDate sweDate = convertToSweDate(dateTime);
        double[] cusps = new double[13]; // Swiss Ephemeris returns 13 house cusps (including Ascendant)
        double[] ascmc = new double[10]; // Contains Ascendant, Midheaven, etc.
        
        // Calculate houses
        swissEph.swe_houses(sweDate.getJulDay(), SweConst.SEFLG_SWIEPH, latitude, longitude, 
                           'P', // P = Placidus house system
                           cusps, ascmc);
        
        return cusps;
    }
    
    private static double[] calculatePlanetPosition(SweDate sweDate, int planet) {
        double[] xx = new double[6];
        StringBuffer serr = new StringBuffer();
        
        // Calculate planetary positions
        int ret = swissEph.swe_calc_ut(sweDate.getJulDay(), planet, 
                                     SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED,
                                     xx, serr);
        
        if (ret < 0) {
            logger.error("Error calculating planet position: " + serr.toString());
            return new double[]{0, 0, 0, 0, 0, 0};
        }
        
        // xx[0] = longitude
        // xx[1] = latitude
        // xx[2] = distance
        // xx[3] = longitude speed
        // xx[4] = latitude speed
        // xx[5] = distance speed
        return new double[]{xx[0], xx[1], xx[2], xx[3], xx[4], xx[5]};
    }
    
    private static SweDate convertToSweDate(LocalDateTime dateTime) {
        int year = dateTime.getYear();
        int month = dateTime.getMonthValue();
        int day = dateTime.getDayOfMonth();
        int hour = dateTime.getHour();
        int minute = dateTime.getMinute();
        int second = dateTime.getSecond();
        
        // Convert to Julian Day
        return new SweDate(year, month, day, 
                          hour + minute/60.0 + second/3600.0);
    }
    
    public static boolean isRetrograde(int planet, LocalDateTime dateTime) {
        SweDate sweDate = convertToSweDate(dateTime);
        double[] xx = new double[6];
        StringBuffer serr = new StringBuffer();
        
        // Calculate planetary positions
        int ret = swissEph.swe_calc_ut(sweDate.getJulDay(), planet, 
                                     SweConst.SEFLG_SWIEPH | SweConst.SEFLG_SPEED,
                                     xx, serr);
        
        if (ret < 0) {
            logger.error("Error calculating planet position: " + serr.toString());
            return false;
        }
        
        // xx[3]  is longitude speed, if negative then retrograde
        return xx[3] < 0;
    }
} 