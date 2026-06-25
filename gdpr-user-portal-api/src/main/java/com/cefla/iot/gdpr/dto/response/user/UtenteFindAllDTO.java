package com.cefla.iot.gdpr.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UtenteFindAllDTO {
    private List<UtenteFindDTO> UtenteFindAllDTO;
}