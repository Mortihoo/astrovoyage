package com.astrology.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.astrology.api.util.MapSerializer;
import com.astrology.api.util.MapDeserializer;
import com.astrology.api.util.ArraySerializer;
import com.astrology.api.util.ArrayDeserializer;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import org.hibernate.annotations.Type;
import com.astrology.api.util.AstrologyCalculationUtil;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "birth_charts")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BirthChart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "birth_date_time")
    private String birthDateTime;

    @Column(name = "location")
    private String location;

    @Column(name = "gender")
    private String gender;

    @Column(columnDefinition = "TEXT")
    private String chartData;

    @Column(columnDefinition = "TEXT")
    private String interpretation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @ElementCollection
    @CollectionTable(name = "planet_positions", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "planet")
    @Column(name = "position")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, double[]> planetPositions = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "planet_speeds", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "planet")
    @Column(name = "speed")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, Double> planetSpeeds = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "houses", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @Column(name = "position")
    @JsonSerialize(using = ArraySerializer.class)
    @JsonDeserialize(using = ArrayDeserializer.class)
    private double[] houses;

    @ElementCollection
    @CollectionTable(name = "aspects", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "planet_pair")
    @Column(name = "angle")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, Double> aspects = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "aspects_details", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "planet_pair")
    @Column(name = "detail")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> aspectsDetails = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "elements", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "element")
    @Column(name = "count")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> elements = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "modalities", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "modality")
    @Column(name = "count")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> modalities = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "signs", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "sign")
    @Column(name = "range")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> signs = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "houses_signs", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "house")
    @Column(name = "sign")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> housesSigns = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "planet_signs", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "planet")
    @Column(name = "sign")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> planetSigns = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "chart_image_url", joinColumns = @JoinColumn(name = "birth_chart_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "url", columnDefinition = "TEXT")
    @JsonSerialize(using = MapSerializer.class)
    @JsonDeserialize(using = MapDeserializer.class)
    private Map<String, String> chartImageUrl = new HashMap<>();

    public BirthChart(Map<String, double[]> planetPositions, double[] houses) {
        this.planetPositions = planetPositions;
        this.houses = houses;
        this.planetSpeeds = new HashMap<>();
        this.aspects = new HashMap<>();
        this.aspectsDetails = new HashMap<>();
        this.elements = new HashMap<>();
        this.modalities = new HashMap<>();
        this.signs = new HashMap<>();
        this.housesSigns = new HashMap<>();
        this.planetSigns = new HashMap<>();
        this.chartImageUrl = new HashMap<>();
    }

    public void setPlanetPositions(Map<String, double[]> planetPositions) {
        this.planetPositions = planetPositions;
        updatePlanetSpeeds();
    }

    private void updatePlanetSpeeds() {
        if (planetPositions != null) {
            for (Map.Entry<String, double[]> entry : planetPositions.entrySet()) {
                if (entry.getValue() != null && entry.getValue().length > 3) {
                    planetSpeeds.put(entry.getKey(), entry.getValue()[3]);
                }
            }
        }
    }

    public void setPlanetSigns(Map<String, String> planetSigns) {
        this.planetSigns = planetSigns;
        if (planetSigns != null) {
            this.elements = AstrologyCalculationUtil.calculateElements(planetSigns);
            this.modalities = AstrologyCalculationUtil.calculateModalities(planetSigns);
        }
    }
} 