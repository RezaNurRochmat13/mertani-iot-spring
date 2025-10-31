package com.mertani.iot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertani.iot.TestUtils;
import com.mertani.iot.dto.DeviceRequest;
import com.mertani.iot.dto.DeviceResponse;
import com.mertani.iot.model.Device;
import com.mertani.iot.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
class DeviceControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    private Device testDevice;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        deviceRepository.deleteAll();
        
        // Create a test device
        testDevice = TestUtils.createTestDevice();
        testDevice = deviceRepository.save(testDevice);
    }

    @Test
    void createDevice_ShouldReturnCreatedDevice() throws Exception {
        // Given
        DeviceRequest request = TestUtils.createTestDeviceRequest();
        request.setSerialNumber("NEW-123");

        // When/Then
        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.serialNumber").value(request.getSerialNumber()));

        // Verify the device was saved in the database
        List<Device> devices = deviceRepository.findAll();
        assertThat(devices).hasSize(2);
        assertThat(devices.get(1).getSerialNumber()).isEqualTo("NEW-123");
    }

    @Test
    void createDevice_ShouldReturnBadRequest_WhenSerialNumberExists() throws Exception {
        // Given
        DeviceRequest request = TestUtils.createTestDeviceRequest();
        request.setSerialNumber(testDevice.getSerialNumber());

        // When/Then
        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void getDevice_ShouldReturnDevice() throws Exception {
        // When/Then
        mockMvc.perform(get("/devices/" + testDevice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testDevice.getId()))
                .andExpect(jsonPath("$.name").value(testDevice.getName()));
    }

    @Test
    void getDevice_ShouldReturnNotFound_WhenDeviceDoesNotExist() throws Exception {
        // When/Then
        mockMvc.perform(get("/devices/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllDevices_ShouldReturnAllDevices() throws Exception {
        // When/Then
        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testDevice.getId()));
    }

    @Test
    void updateDevice_ShouldUpdateDevice() throws Exception {
        // Given
        DeviceRequest request = TestUtils.createTestDeviceRequest();
        request.setName("Updated Device Name");

        // When/Then
        mockMvc.perform(put("/devices/" + testDevice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Device Name"));

        // Verify the device was updated in the database
        Device updatedDevice = deviceRepository.findById(testDevice.getId()).orElseThrow();
        assertThat(updatedDevice.getName()).isEqualTo("Updated Device Name");
    }

    @Test
    void deleteDevice_ShouldDeleteDevice() throws Exception {
        // When/Then
        mockMvc.perform(delete("/devices/" + testDevice.getId()))
                .andExpect(status().isNoContent());

        // Verify the device was deleted from the database
        assertThat(deviceRepository.existsById(testDevice.getId())).isFalse();
    }

    @Test
    void deleteDevice_ShouldReturnNotFound_WhenDeviceDoesNotExist() throws Exception {
        // When/Then
        mockMvc.perform(delete("/devices/999"))
                .andExpect(status().isNotFound());
    }
}
