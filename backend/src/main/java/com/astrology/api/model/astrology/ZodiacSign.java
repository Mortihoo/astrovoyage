package com.astrology.api.model.astrology;

public enum ZodiacSign {
    ARIES(0, "Fire", "Cardinal"),
    TAURUS(30, "Earth", "Fixed"),
    GEMINI(60, "Air", "Mutable"),
    CANCER(90, "Water", "Cardinal"),
    LEO(120, "Fire", "Fixed"),
    VIRGO(150, "Earth", "Mutable"),
    LIBRA(180, "Air", "Cardinal"),
    SCORPIO(210, "Water", "Fixed"),
    SAGITTARIUS(240, "Fire", "Mutable"),
    CAPRICORN(270, "Earth", "Cardinal"),
    AQUARIUS(300, "Air", "Fixed"),
    PISCES(330, "Water", "Mutable");

    private final double startLongitude;
    private final String element;
    private final String modality;

    ZodiacSign(double startLongitude, String element, String modality) {
        this.startLongitude = startLongitude;
        this.element = element;
        this.modality = modality;
    }

    public double getStartLongitude() {
        return startLongitude;
    }

    public String getElement() {
        return element;
    }

    public String getModality() {
        return modality;
    }

    public static ZodiacSign fromLongitude(double longitude) {
        longitude = longitude % 360;
        if (longitude < 0) {
            longitude += 360;
        }
        
        for (ZodiacSign sign : values()) {
            double nextStartLongitude = (sign.ordinal() == values().length - 1) ? 360 : values()[sign.ordinal() + 1].startLongitude;
            if (longitude >= sign.startLongitude && longitude < nextStartLongitude) {
                return sign;
            }
        }
        return ARIES; // Default case
    }

    public String getRange() {
        double endLongitude = (this.ordinal() == values().length - 1) ? 360 : values()[this.ordinal() + 1].startLongitude;
        return String.format("%.0f-%.0f", startLongitude, endLongitude);
    }
} 