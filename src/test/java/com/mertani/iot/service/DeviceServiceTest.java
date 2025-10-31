package com.mertani.iot.service;

import com.mertani.iot.TestUtils;
import com.mertani.iot.dto.DeviceRequest;
import com.mertani.iot.dto.DeviceResponse;
import com.mertani.iot.exception.DeviceNotFoundException;
import com.mertani.iot.exception.DuplicateSerialNumberException;
import com.mertani.iot.mapper.DeviceMapper;
import com.mertani.iot.model.Device;
import com.mertani.iot.repository.DeviceRepository;
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
class DeviceServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private SensorService sensorService;

    @Mock
    private DeviceMapper deviceMapper;

    @InjectMocks
    private DeviceService deviceService;

    private Device device;
    private DeviceRequest deviceRequest;
    private DeviceResponse deviceResponse;

    @BeforeEach
    void setUp() {
        device = TestUtils.createTestDevice();
        deviceRequest = TestUtils.createTestDeviceRequest();
        deviceResponse = TestUtils.createTestDeviceResponse();
    }

    @Test
    void createDevice_ShouldReturnDeviceResponse_WhenValidRequest() {
        // Arrange
        when(deviceRepository.existsBySerialNumber(anyString())).thenReturn(false);
        when(deviceMapper.toEntity(any(DeviceRequest.class))).thenReturn(device);
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        when(deviceMapper.toDto(any(Device.class))).thenReturn(deviceResponse);

        // Act
        DeviceResponse result = deviceService.createDevice(deviceRequest);

        // Assert
        assertNotNull(result);
        assertEquals(deviceResponse, result);
        verify(deviceRepository, times(1)).existsBySerialNumber(anyString());
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    void createDevice_ShouldThrowException_WhenSerialNumberExists() {
        // Arrange
        when(deviceRepository.existsBySerialNumber(anyString())).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateSerialNumberException.class, () -> deviceService.createDevice(deviceRequest));
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    void getDeviceById_ShouldReturnDeviceResponse_WhenDeviceExists() {
        // Arrange
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(device));
        when(deviceMapper.toDto(any(Device.class))).thenReturn(deviceResponse);

        // Act
        DeviceResponse result = deviceService.getDeviceById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(deviceResponse, result);
        verify(deviceRepository, times(1)).findById(anyLong());
    }

    @Test
    void getDeviceById_ShouldThrowException_WhenDeviceNotFound() {
        // Arrange
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DeviceNotFoundException.class, () -> deviceService.getDeviceById(1L));
    }

    @Test
    void getAllDevices_ShouldReturnListOfDeviceResponses() {
        // Arrange
        List<Device> devices = Arrays.asList(device);
        when(deviceRepository.findAll()).thenReturn(devices);
        when(deviceMapper.toDto(any(Device.class))).thenReturn(deviceResponse);

        // Act
        List<DeviceResponse> results = deviceService.getAllDevices();

        // Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        verify(deviceRepository, times(1)).findAll();
    }

    @Test
    void updateDevice_ShouldReturnUpdatedDevice_WhenValidRequest() {
        // Arrange
        when(deviceRepository.findById(anyLong())).thenReturn(Optional.of(device));
        when(deviceRepository.existsBySerialNumber(anyString())).thenReturn(false);
        when(deviceRepository.save(any(Device.class))).thenReturn(device);
        when(deviceMapper.toDto(any(Device.class))).thenReturn(deviceResponse);

        // Act
        DeviceResponse result = deviceService.updateDevice(1L, deviceRequest);

        // Assert
        assertNotNull(result);
        assertEquals(deviceResponse, result);
        verify(deviceRepository, times(1)).findById(anyLong());
        verify(deviceRepository, times(1)).save(any(Device.class));
    }

    @Test
    void deleteDevice_ShouldDeleteDevice_WhenDeviceExists() {
        // Arrange
        when(deviceRepository.existsById(anyLong())).thenReturn(true);
        doNothing().when(deviceRepository).deleteById(anyLong());

        // Act & Assert
        assertDoesNotThrow(() -> deviceService.deleteDevice(1L));
        verify(deviceRepository, times(1)).existsById(anyLong());
        verify(deviceRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void deleteDevice_ShouldThrowException_WhenDeviceNotFound() {
        // Arrange
        when(deviceRepository.existsById(anyLong())).thenReturn(false);

        // Act & Assert
        assertThrows(DeviceNotFoundException.class, () -> deviceService.deleteDevice(1L));
        verify(deviceRepository, never()).deleteById(anyLong());
    }
}
