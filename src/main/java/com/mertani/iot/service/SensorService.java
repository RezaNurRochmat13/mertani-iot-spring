package com.mertani.iot.service;

import com.mertani.iot.dto.SensorRequest;
import com.mertani.iot.dto.SensorResponse;
import com.mertani.iot.exception.DeviceNotFoundException;
import com.mertani.iot.exception.DuplicateSensorIdException;
import com.mertani.iot.exception.SensorNotFoundException;
import com.mertani.iot.mapper.SensorMapper;
import com.mertani.iot.model.Device;
import com.mertani.iot.model.Sensor;
import com.mertani.iot.repository.DeviceRepository;
import com.mertani.iot.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final SensorRepository sensorRepository;
    private final DeviceRepository deviceRepository;
    private final SensorMapper sensorMapper;

    @Transactional
    public SensorResponse createSensor(SensorRequest request) {
        // Check if device exists
        Device device = deviceRepository.findById(request.getDeviceId())
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + request.getDeviceId()));

        // Check if sensor ID is already taken
        if (sensorRepository.existsBySensorId(request.getSensorId())) {
            throw new DuplicateSensorIdException("Sensor with ID " + request.getSensorId() + " already exists");
        }

        // Map request to entity and save
        Sensor sensor = sensorMapper.toEntity(request);
        sensor.setDevice(device);
        Sensor savedSensor = sensorRepository.save(sensor);
        
        return sensorMapper.toDto(savedSensor);
    }

    public SensorResponse getSensorById(Long id) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new SensorNotFoundException("Sensor not found with id: " + id));
        return sensorMapper.toDto(sensor);
    }

    public List<SensorResponse> getSensorsByDeviceId(Long deviceId) {
        return sensorRepository.findByDeviceId(deviceId).stream()
                .map(sensorMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<SensorResponse> getAllSensors() {
        return sensorRepository.findAll().stream()
                .map(sensorMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public SensorResponse updateSensor(Long id, SensorRequest request) {
        Sensor sensor = sensorRepository.findById(id)
                .orElseThrow(() -> new SensorNotFoundException("Sensor not found with id: " + id));

        // Check if the new sensor ID is already taken by another sensor
        if (!sensor.getSensorId().equals(request.getSensorId()) && 
            sensorRepository.existsBySensorId(request.getSensorId())) {
            throw new DuplicateSensorIdException("Sensor with ID " + request.getSensorId() + " already exists");
        }

        // Update sensor fields
        sensor.setName(request.getName());
        sensor.setSensorId(request.getSensorId());
        sensor.setType(request.getType());
        sensor.setUnit(request.getUnit());
        sensor.setMinValue(request.getMinValue());
        sensor.setMaxValue(request.getMaxValue());
        sensor.setActive(request.isActive());

        // Update device if changed
        if (!sensor.getDevice().getId().equals(request.getDeviceId())) {
            Device newDevice = deviceRepository.findById(request.getDeviceId())
                    .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + request.getDeviceId()));
            sensor.setDevice(newDevice);
        }

        Sensor updatedSensor = sensorRepository.save(sensor);
        return sensorMapper.toDto(updatedSensor);
    }

    @Transactional
    public void deleteSensor(Long id) {
        if (!sensorRepository.existsById(id)) {
            throw new SensorNotFoundException("Sensor not found with id: " + id);
        }
        sensorRepository.deleteById(id);
    }
}
