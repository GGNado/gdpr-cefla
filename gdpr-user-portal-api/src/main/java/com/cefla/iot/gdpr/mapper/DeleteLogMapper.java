package com.cefla.iot.gdpr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import com.cefla.iot.gdpr.entity.DeleteLog;
import com.cefla.iot.gdpr.dto.request.deletelog.DeleteLogCreateRequestDTO;
import com.cefla.iot.gdpr.dto.request.deletelog.DeleteLogUpdateRequestDTO;
import com.cefla.iot.gdpr.dto.response.deletelog.DeleteLogFindDTO;

@Mapper(componentModel = "spring")
public interface DeleteLogMapper {

    DeleteLog convert(DeleteLogCreateRequestDTO dto);

    DeleteLog convert(DeleteLogUpdateRequestDTO dto);

    DeleteLogFindDTO convert(DeleteLog entity);

    List<DeleteLogFindDTO> convert(List<DeleteLog> entities);
}