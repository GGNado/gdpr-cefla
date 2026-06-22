package com.cefla.iot.gdpr.dto.response.deletelog;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class DeleteLogFindAllDTO {
    private List<DeleteLogFindDTO> DeleteLogFindAllDTO;
}