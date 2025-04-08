package com.astrology.api.controller;

import com.astrology.api.config.AppConfig;
import com.astrology.api.dto.BirthChartRequest;
import com.astrology.api.model.BirthChart;
import com.astrology.api.service.BirthChartService;
import com.astrology.api.service.ChartImageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/birth-charts")
@CrossOrigin(origins = "#{appConfig.frontendUrl}")
public class BirthChartController {

    private final BirthChartService birthChartService;
    private final ChartImageService chartImageService;
    private final ObjectMapper objectMapper;

    @Autowired
    public BirthChartController(BirthChartService birthChartService,
                               ChartImageService chartImageService,
                               ObjectMapper objectMapper) {
        this.birthChartService = birthChartService;
        this.chartImageService = chartImageService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<BirthChart> generateBirthChart(@Valid @RequestBody BirthChartRequest request) {
        BirthChart birthChart = birthChartService.generateBirthChart(request);
        return ResponseEntity.ok(birthChart);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BirthChart> getBirthChart(@PathVariable Long id) {
        BirthChart birthChart = birthChartService.getBirthChart(id);
        return ResponseEntity.ok(birthChart);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<String> getChartImageUrl(@PathVariable Long id) {
        try {
            BirthChart birthChart = birthChartService.getBirthChart(id);
            Map<String, Object> chartData = objectMapper.readValue(birthChart.getChartData(), Map.class);
            String imageUrl = chartImageService.generateChartImageUrl(chartData);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error generating chart image URL: " + e.getMessage());
        }
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
} 