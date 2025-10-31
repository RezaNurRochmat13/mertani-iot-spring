package com.mertani.iot.service;

import com.mertani.iot.TestUtils;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private SensorMapper sensorMapper;

    @InjectMocks
    private SensorService sensorService;

    private Sensor sensor;
    private SensorRequest sensorRequest;
    private SensorResponse sensorResponse;
    private Device device;
    private final Long DEVICE_ID = 1L;

    @BeforeEach
    void setUp() {
        device = TestUtils.createTestDevice();
        sensor = TestUtils.createTestSensor(device);
        sensorRequest = TestUtils.createTestSensorRequest(DEVICE_ID);
        sensorResponse = TestUtils.createTestSensorResponse(DEVICE_ID);
    }

    @Test
    void createSensor_ShouldReturnSensorResponse_WhenValidRequest() {
        // Arrange
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(device));
        when(sensorRepository.existsBySensorId(anyString())).thenReturn(false);
        when(sensorMapper.toEntity(any(SensorRequest.class))).thenReturn(sensor);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);
        when(sensorMapper.toDto(any(Sensor.class))).thenReturn(sensorResponse);

        // Act
        SensorResponse result = sensorService.createSensor(sensorRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sensorResponse, result);
        verify(deviceRepository, times(1)).findById(anyLong());
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void createSensor_ShouldThrowException_WhenDeviceNotFound() {
        // Arrange
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DeviceNotFoundException.class, () -> sensorService.createSensor(sensorRequest));
        verify(sensorRepository, never()).save(any(Sensor.class));
    }

    @Test
    void createSensor_ShouldThrowException_WhenSensorIdExists() {
        // Arrange
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(device));
        when(sensorRepository.existsBySensorId(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateSensorIdException.class, () -> sensorService.createSensor(sensorRequest));
        verify(sensorRepository, never()).save(any(Sensor.class));
    }

    @Test
    void getSensorById_ShouldReturnSensorResponse_WhenSensorExists() {
        // Arrange
        when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
        when(sensorMapper.toDto(any(Sensor.class))).thenReturn(sensorResponse);

        // Act
        SensorResponse result = sensorService.getSensorById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(sensorResponse, result);
        verify(sensorRepository, times(1)).findById(anyLong());
    }

    @Test
    void getSensorById_ShouldThrowException_WhenSensorNotFound() {
        // Arrange
        when(sensorRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SensorNotFoundException.class, () -> sensorService.getSensorById(1L));
    }

    @Test
    void getSensorsByDeviceId_ShouldReturnListOfSensorResponses() {
        // Arrange
        List<Sensor> sensors = Arrays.asList(sensor);
        when(sensorRepository.findByDeviceId(anyLong())).thenReturn(sensors);
        when(sensorMapper.toDto(any(Sensor.class))).thenReturn(sensorResponse);

        // Act
        List<SensorResponse> results = sensorService.getSensorsByDeviceId(DEVICE_ID);

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(sensorRepository, times(1)).findByDeviceId(anyLong());
    }

    @Test
    void updateSensor_ShouldReturnUpdatedSensor_WhenValidRequest() {
        // Arrange
        when(sensorRepository.findById(anyLong())).thenReturn(Optional.of(sensor));
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(device));
        when(sensorRepository.existsBySensorId(anyString())).thenReturn(false);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);
        when(sensorMapper.toDto(any(Sensor.class))).thenReturn(sensorResponse);

        // Act
        SensorResponse result = sensorService.updateSensor(1L, sensorRequest);

        // Assert
        assertNotNull(result);
        assertEquals(sensorResponse, result);
        verify(sensorRepository, times(1)).findById(anyLong());
        verify(sensorRepository, times(1)).save(any(Sensor.class));
    }

    @Test
    void deleteSensor_ShouldDeleteSensor_WhenSensorExists() {
        // Arrange
        when(sensorRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(sensorRepository).deleteById(anyLong());

        // Act & Assert
        assertDoesNotThrow(() -> sensorService.deleteSensor(1L));
        verify(sensorRepository, times(1)).existsById(anyLong());
        verify(sensorRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteSensor_ShouldThrowException_WhenSensorNotFound() {
        // Arrange
        when(sensorRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(SensorNotFoundException.class, () -> sensorService.deleteSensor(1L));
        verify(sensorRepository, never()).deleteById(anyLong());
    }
}
