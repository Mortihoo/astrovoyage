package com.astrology.api.controller;

import com.astrology.api.entity.ChartRecord;
import com.astrology.api.service.ChartRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chart-records")
public class ChartRecordController {
    private final ChartRecordService chartRecordService;

    @Autowired
    public ChartRecordController(ChartRecordService chartRecordService) {
        this.chartRecordService = chartRecordService;
    }

    @PostMapping
    public ResponseEntity<ChartRecord> saveChartRecord(@RequestBody ChartRecord chartRecord, Authentication authentication) {
        String username = authentication.getName();
        ChartRecord savedRecord = chartRecordService.saveChartRecord(username, chartRecord);
        return ResponseEntity.ok(savedRecord);
    }

    @GetMapping
    public ResponseEntity<List<ChartRecord>> getChartRecords(Authentication authentication) {
        String username = authentication.getName();
        List<ChartRecord> records = chartRecordService.getChartRecords(username);
        return ResponseEntity.ok(records);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChartRecord> getChartRecord(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        ChartRecord record = chartRecordService.getChartRecord(username, id);
        return ResponseEntity.ok(record);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChartRecord(@PathVariable Long id, Authentication authentication) {
        String username = authentication.getName();
        chartRecordService.deleteChartRecord(username, id);
        return ResponseEntity.ok().build();
    }
} 