package com.mertani.iot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceRequest {
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    
    private String description;
    private String location;
    private boolean active = true;
}
