package com.mertani.iot.service;

import com.mertani.iot.dto.DeviceRequest;
import com.mertani.iot.dto.DeviceResponse;
import com.mertani.iot.dto.SensorResponse;
import com.mertani.iot.exception.DeviceNotFoundException;
import com.mertani.iot.exception.DuplicateSerialNumberException;
import com.mertani.iot.mapper.DeviceMapper;
import com.mertani.iot.model.Device;
import com.mertani.iot.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final SensorService sensorService;
    private final DeviceMapper deviceMapper;

    @Transactional
    public DeviceResponse createDevice(DeviceRequest request) {
        // Check if serial number already exists
        if (deviceRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateSerialNumberException("Device with serial number " + request.getSerialNumber() + " already exists");
        }

        // Map request to entity and save
        Device device = deviceMapper.toEntity(request);
        Device savedDevice = deviceRepository.save(device);
        
        return deviceMapper.toDto(savedDevice);
    }

    public DeviceResponse getDeviceById(Long id) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));
        
        // Get sensors for this device
        List<SensorResponse> sensors = sensorService.getSensorsByDeviceId(device.getId());
        DeviceResponse response = deviceMapper.toDto(device);
        response.setSensors(sensors);
        
        return response;
    }

    public List<DeviceResponse> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(device -> {
                    DeviceResponse response = deviceMapper.toDto(device);
                    response.setSensors(sensorService.getSensorsByDeviceId(device.getId()));
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public DeviceResponse updateDevice(Long id, DeviceRequest request) {
        Device device = deviceRepository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException("Device not found with id: " + id));

        // Check if the new serial number is already taken by another device
        if (!device.getSerialNumber().equals(request.getSerialNumber()) && 
            deviceRepository.existsBySerialNumber(request.getSerialNumber())) {
            throw new DuplicateSerialNumberException("Device with serial number " + request.getSerialNumber() + " already exists");
        }

        // Update device fields
        device.setName(request.getName());
        device.setSerialNumber(request.getSerialNumber());
        device.setDescription(request.getDescription());
        device.setLocation(request.getLocation());
        device.setActive(request.isActive());

        Device updatedDevice = deviceRepository.save(device);
        return deviceMapper.toDto(updatedDevice);
    }

    @Transactional
    public void deleteDevice(Long id) {
        if (!deviceRepository.existsById(id)) {
            throw new DeviceNotFoundException("Device not found with id: " + id);
        }
        deviceRepository.deleteById(id);
    }
}
