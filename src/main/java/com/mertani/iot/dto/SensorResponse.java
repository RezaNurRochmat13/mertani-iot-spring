package com.mertani.iot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SensorResponse {
    private Long id;
    private String name;
    private String sensorId;
    private String type;
    private String unit;
    private Double minValue;
    private Double maxValue;
    private boolean active;
    private Long deviceId;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
