package com.cefla.iot.gdpr.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchPreviewDto {
    private String idUtente;
    private String nome;
    private String cognome;
    private String email;
    private Date ultimaScadenzaLicenza;
}
