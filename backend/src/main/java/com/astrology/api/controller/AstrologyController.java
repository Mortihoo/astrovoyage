package com.astrology.api.controller;

import com.astrology.api.model.BirthChart;
import com.astrology.api.model.BirthData;
import com.astrology.api.service.BirthChartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class AstrologyController {
    private final BirthChartService birthChartService;

    @Autowired
    public AstrologyController(BirthChartService birthChartService) {
        this.birthChartService = birthChartService;
    }

    @PostMapping("/birth-chart")
    public ResponseEntity<BirthChart> calculateBirthChart(@RequestBody BirthData birthData) throws IOException {
        BirthChart birthChart = birthChartService.calculateBirthChart(birthData);
        return ResponseEntity.ok(birthChart);
    }

    @GetMapping("/birth-chart/{id}")
    public ResponseEntity<BirthChart> getBirthChart(@PathVariable Long id) {
        BirthChart birthChart = birthChartService.getBirthChart(id);
        return ResponseEntity.ok(birthChart);
    }
} 