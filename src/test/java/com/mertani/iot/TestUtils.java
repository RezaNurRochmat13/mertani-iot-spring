package com.mertani.iot;

import com.mertani.iot.dto.DeviceRequest;
import com.mertani.iot.dto.DeviceResponse;
import com.mertani.iot.dto.SensorRequest;
import com.mertani.iot.dto.SensorResponse;
import com.mertani.iot.model.Device;
import com.mertani.iot.model.Sensor;

import java.time.LocalDateTime;
import java.util.Collections;

public class TestUtils {

    public static Device createTestDevice() {
        return Device.builder()
                .id(1L)
                .name("Test Device")
                .serialNumber("TEST-123")
                .description("Test Description")
                .location("Test Location")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .sensors(Collections.emptyList())
                .build();
    }

    public static DeviceRequest createTestDeviceRequest() {
        return DeviceRequest.builder()
                .name("Test Device")
                .serialNumber("TEST-123")
                .description("Test Description")
                .location("Test Location")
                .active(true)
                .build();
    }

    public static DeviceResponse createTestDeviceResponse() {
        return DeviceResponse.builder()
                .id(1L)
                .name("Test Device")
                .serialNumber("TEST-123")
                .description("Test Description")
                .location("Test Location")
                .active(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .sensors(Collections.emptyList())
                .build();
    }

    public static Sensor createTestSensor(Device device) {
        return Sensor.builder()
                .id(1L)
                .name("Test Sensor")
                .sensorId("SENSOR-123")
                .type("DHT22")
                .unit("°C")
                .minValue(-20.0)
                .maxValue(60.0)
                .active(true)
                .device(device)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static SensorRequest createTestSensorRequest(Long deviceId) {
        return SensorRequest.builder()
                .name("Test Sensor")
                .sensorId("SENSOR-123")
                .type("DHT22")
                .unit("°C")
                .minValue(-20.0)
                .maxValue(60.0)
                .active(true)
                .deviceId(deviceId)
                .build();
    }

    public static SensorResponse createTestSensorResponse(Long deviceId) {
        return SensorResponse.builder()
                .id(1L)
                .name("Test Sensor")
                .sensorId("SENSOR-123")
                .type("DHT22")
                .unit("°C")
                .minValue(-20.0)
                .maxValue(60.0)
                .active(true)
                .deviceId(deviceId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
