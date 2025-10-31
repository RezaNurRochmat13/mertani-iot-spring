package com.mertani.iot.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mertani.iot.TestUtils;
import com.mertani.iot.dto.SensorRequest;
import com.mertani.iot.dto.SensorResponse;
import com.mertani.iot.model.Device;
import com.mertani.iot.model.Sensor;
import com.mertani.iot.repository.DeviceRepository;
import com.mertani.iot.repository.SensorRepository;
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
class SensorControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private SensorRepository sensorRepository;

    private Device testDevice;
    private Sensor testSensor;

    @BeforeEach
    void setUp() {
        // Clear the database before each test
        sensorRepository.deleteAll();
        deviceRepository.deleteAll();
        
        // Create a test device and sensor
        testDevice = TestUtils.createTestDevice();
        testDevice = deviceRepository.save(testDevice);
        
        testSensor = TestUtils.createTestSensor(testDevice);
        testSensor = sensorRepository.save(testSensor);
    }

    @Test
    void createSensor_ShouldReturnCreatedSensor() throws Exception {
        // Given
        SensorRequest request = TestUtils.createTestSensorRequest(testDevice.getId());
        request.setSensorId("NEW-SENSOR-123");

        // When/Then
        mockMvc.perform(post("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.sensorId").value(request.getSensorId()));

        // Verify the sensor was saved in the database
        List<Sensor> sensors = sensorRepository.findAll();
        assertThat(sensors).hasSize(2);
        assertThat(sensors.get(1).getSensorId()).isEqualTo("NEW-SENSOR-123");
    }

    @Test
    void createSensor_ShouldReturnBadRequest_WhenSensorIdExists() throws Exception {
        // Given
        SensorRequest request = TestUtils.createTestSensorRequest(testDevice.getId());
        request.setSensorId(testSensor.getSensorId());

        // When/Then
        mockMvc.perform(post("/sensors")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void getSensor_ShouldReturnSensor() throws Exception {
        // When/Then
        mockMvc.perform(get("/sensors/" + testSensor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testSensor.getId()))
                .andExpect(jsonPath("$.name").value(testSensor.getName()));
    }

    @Test
    void getSensor_ShouldReturnNotFound_WhenSensorDoesNotExist() throws Exception {
        // When/Then
        mockMvc.perform(get("/sensors/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getSensorsByDeviceId_ShouldReturnSensors() throws Exception {
        // When/Then
        mockMvc.perform(get("/sensors/device/" + testDevice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testSensor.getId()));
    }

    @Test
    void getAllSensors_ShouldReturnAllSensors() throws Exception {
        // When/Then
        mockMvc.perform(get("/sensors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(testSensor.getId()));
    }

    @Test
    void updateSensor_ShouldUpdateSensor() throws Exception {
        // Given
        SensorRequest request = TestUtils.createTestSensorRequest(testDevice.getId());
        request.setName("Updated Sensor Name");

        // When/Then
        mockMvc.perform(put("/sensors/" + testSensor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Sensor Name"));

        // Verify the sensor was updated in the database
        Sensor updatedSensor = sensorRepository.findById(testSensor.getId()).orElseThrow();
        assertThat(updatedSensor.getName()).isEqualTo("Updated Sensor Name");
    }

    @Test
    void deleteSensor_ShouldDeleteSensor() throws Exception {
        // When/Then
        mockMvc.perform(delete("/sensors/" + testSensor.getId()))
                .andExpect(status().isNoContent());

        // Verify the sensor was deleted from the database
        assertThat(sensorRepository.existsById(testSensor.getId())).isFalse();
    }

    @Test
    void deleteSensor_ShouldReturnNotFound_WhenSensorDoesNotExist() throws Exception {
        // When/Then
        mockMvc.perform(delete("/sensors/999"))
                .andExpect(status().isNotFound());
    }
}
