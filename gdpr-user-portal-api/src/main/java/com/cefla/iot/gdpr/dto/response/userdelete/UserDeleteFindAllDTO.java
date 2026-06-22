package com.cefla.iot.gdpr.dto.response.userdelete;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserDeleteFindAllDTO {
    private List<UserDeleteFindDTO> UserDeleteFindAllDTO;
}