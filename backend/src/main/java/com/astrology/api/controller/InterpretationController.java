package com.astrology.api.controller;

import com.astrology.api.config.AppConfig;
import com.astrology.api.dto.InterpretationRequest;
import com.astrology.api.dto.InterpretationResponse;
import com.astrology.api.service.ChartInterpretationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "#{appConfig.frontendUrl}")
public class InterpretationController {

    private final ChartInterpretationService interpretationService;

    @Autowired
    public InterpretationController(ChartInterpretationService interpretationService) {
        this.interpretationService = interpretationService;
    }

    @PostMapping("/interpret")
    public ResponseEntity<InterpretationResponse> interpretChart(
            @RequestBody InterpretationRequest request) {
        String interpretation = interpretationService.interpretChart(
            request.getQuestion(),
            request.getChartData()
        );
        return ResponseEntity.ok(new InterpretationResponse(interpretation));
    }
} 