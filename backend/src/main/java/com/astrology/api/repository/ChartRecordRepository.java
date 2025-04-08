package com.astrology.api.repository;

import com.astrology.api.entity.ChartRecord;
import com.astrology.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChartRecordRepository extends JpaRepository<ChartRecord, Long> {
    List<ChartRecord> findByUserOrderByCreatedAtDesc(User user);
} 