package com.astrology.api.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BirthData {
    private String name;
    private LocalDateTime birthDateTime;
    private String timezone;
    private double latitude;
    private double longitude;
    private String location;
} 