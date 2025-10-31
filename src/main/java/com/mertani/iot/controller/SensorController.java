package com.mertani.iot.controller;

import com.mertani.iot.dto.SensorRequest;
import com.mertani.iot.dto.SensorResponse;
import com.mertani.iot.service.SensorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sensors")
@RequiredArgsConstructor
public class SensorController {

    private final SensorService sensorService;

    @PostMapping
    public ResponseEntity<SensorResponse> createSensor(@Valid @RequestBody SensorRequest request) {
        SensorResponse response = sensorService.createSensor(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SensorResponse> getSensor(@PathVariable Long id) {
        SensorResponse response = sensorService.getSensorById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<SensorResponse>> getAllSensors() {
        List<SensorResponse> sensors = sensorService.getAllSensors();
        return ResponseEntity.ok(sensors);
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<SensorResponse>> getSensorsByDeviceId(@PathVariable Long deviceId) {
        List<SensorResponse> sensors = sensorService.getSensorsByDeviceId(deviceId);
        return ResponseEntity.ok(sensors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SensorResponse> updateSensor(
            @PathVariable Long id,
            @Valid @RequestBody SensorRequest request) {
        SensorResponse response = sensorService.updateSensor(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSensor(@PathVariable Long id) {
        sensorService.deleteSensor(id);
        return ResponseEntity.noContent().build();
    }
}
