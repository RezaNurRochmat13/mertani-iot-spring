package com.mertani.iot.mapper;

import com.mertani.iot.dto.SensorRequest;
import com.mertani.iot.dto.SensorResponse;
import com.mertani.iot.model.Sensor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SensorMapper {
    SensorMapper INSTANCE = Mappers.getMapper(SensorMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "device", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Sensor toEntity(SensorRequest request);
    
    @Mapping(source = "device.id", target = "deviceId")
    SensorResponse toDto(Sensor sensor);
}
