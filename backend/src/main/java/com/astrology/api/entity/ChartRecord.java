package com.astrology.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import com.astrology.api.model.User;

@Data
@Entity
@Table(name = "chart_records")
public class ChartRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(name = "birth_date_time", nullable = false)
    private LocalDateTime birthDateTime;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private String timezone = "UTC";

    @Column(nullable = false)
    private String gender;

    @Column(name = "chart_data", columnDefinition = "TEXT")
    private String chartData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (timezone == null) {
            timezone = "UTC";
        }
    }
} 