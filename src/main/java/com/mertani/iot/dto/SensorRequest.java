package com.mertani.iot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SensorRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Sensor ID is required")
    private String sensorId;
    
    private String type;
    private String unit;
    private Double minValue;
    private Double maxValue;
    private boolean active = true;
    
    @NotNull(message = "Device ID is required")
    private Long deviceId;
}
