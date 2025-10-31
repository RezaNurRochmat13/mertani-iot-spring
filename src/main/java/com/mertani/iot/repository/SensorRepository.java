package com.mertani.iot.repository;

import com.mertani.iot.model.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Optional<Sensor> findBySensorId(String sensorId);
    boolean existsBySensorId(String sensorId);
    List<Sensor> findByDeviceId(Long deviceId);
    boolean existsByDeviceId(Long deviceId);
}
