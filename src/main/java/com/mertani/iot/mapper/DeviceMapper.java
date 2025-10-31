package com.mertani.iot.mapper;

import com.mertani.iot.dto.DeviceRequest;
import com.mertani.iot.dto.DeviceResponse;
import com.mertani.iot.model.Device;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeviceMapper {
    DeviceMapper INSTANCE = Mappers.getMapper(DeviceMapper.class);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sensors", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Device toEntity(DeviceRequest request);
    
    DeviceResponse toDto(Device device);
}
