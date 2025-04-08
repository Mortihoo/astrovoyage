package com.astrology.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
public class BirthChartRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Birth date and time are required")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime birthDateTime;

    @NotBlank(message = "Location is required")
    private String location;

    @NotBlank(message = "Timezone is required")
    private String timezone;
    
    @NotBlank(message = "Gender is required")
    private String gender;
} 