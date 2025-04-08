package com.astrology.api.service;

import com.astrology.api.entity.ChartRecord;
import com.astrology.api.model.User;
import com.astrology.api.repository.ChartRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ChartRecordService {
    private final ChartRecordRepository chartRecordRepository;
    private final UserService userService;

    @Autowired
    public ChartRecordService(ChartRecordRepository chartRecordRepository, UserService userService) {
        this.chartRecordRepository = chartRecordRepository;
        this.userService = userService;
    }

    public ChartRecord saveChartRecord(String username, ChartRecord chartRecord) {
        User user = userService.findByUsername(username);
        chartRecord.setUser(user);
        return chartRecordRepository.save(chartRecord);
    }

    public List<ChartRecord> getChartRecords(String username) {
        User user = userService.findByUsername(username);
        return chartRecordRepository.findByUserOrderByCreatedAtDesc(user);
    }

    private ChartRecord validateUserAccess(String username, Long id) {
        User user = userService.findByUsername(username);
        Optional<ChartRecord> record = chartRecordRepository.findById(id);
        if (record.isPresent() && record.get().getUser().getId().equals(user.getId())) {
            return record.get();
        }
        throw new RuntimeException("Chart record not found or access denied");
    }

    public ChartRecord getChartRecord(String username, Long id) {
        return validateUserAccess(username, id);
    }

    public void deleteChartRecord(String username, Long id) {
        ChartRecord record = validateUserAccess(username, id);
        chartRecordRepository.deleteById(id);
    }
} 