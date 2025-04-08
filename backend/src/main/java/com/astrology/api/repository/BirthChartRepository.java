package com.astrology.api.repository;

import com.astrology.api.model.BirthChart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BirthChartRepository extends JpaRepository<BirthChart, Long> {
    // Add custom query methods if needed
} 