package com.cefla.iot.gdpr.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

import com.cefla.iot.gdpr.entity.UserDelete;
import com.cefla.iot.gdpr.dto.request.userdelete.UserDeleteCreateRequestDTO;
import com.cefla.iot.gdpr.dto.request.userdelete.UserDeleteUpdateRequestDTO;
import com.cefla.iot.gdpr.dto.response.userdelete.UserDeleteFindDTO;

@Mapper(componentModel = "spring")
public interface UserDeleteMapper {

    UserDelete convert(UserDeleteCreateRequestDTO dto);

    UserDelete convert(UserDeleteUpdateRequestDTO dto);

    UserDeleteFindDTO convert(UserDelete entity);

    List<UserDeleteFindDTO> convert(List<UserDelete> entities);
}